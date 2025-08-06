package com.jmin.monthlytodo.repository

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.jmin.monthlytodo.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Locale

class DeviceRepository {
    private val TAG = "DeviceRepository"
    
    suspend fun recordDeviceInfo(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val deviceInfo = getDeviceInfo(context)
                
                // 将所有值转换为字符串以避免序列化问题
                val deviceInfoMap = mapOf(
                    "os" to deviceInfo["os"]?.toString(),
                    "device_name" to deviceInfo["deviceName"]?.toString(),
                    "device_version" to deviceInfo["deviceVersion"]?.toString(),
                    "package_name" to deviceInfo["packageName"]?.toString(),
                    "device_id" to deviceInfo["deviceId"]?.toString(),
                    "app_version_code" to deviceInfo["appVersionCode"]?.toString(),
                    "device_region" to deviceInfo["deviceRegion"]?.toString(),
                    "device_language" to deviceInfo["deviceLanguage"]?.toString(),
                    "channel_source" to deviceInfo["channelSource"]?.toString(),
                    "network_status" to deviceInfo["networkStatus"]?.toString(),
                    "ip_address" to deviceInfo["ipAddress"]?.toString(),
                    "install_referrer" to deviceInfo["installReferrer"]?.toString(),
                    "referrer_click_timestamp" to deviceInfo["referrerClickTimestamp"]?.toString(),
                    "install_begin_timestamp" to deviceInfo["installBeginTimestamp"]?.toString()
                )
                
                SupabaseClient.client
                    .from("device_info")
                    .insert(deviceInfoMap)
                Log.d(TAG, "Device info recorded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to record device info: ${e.message}", e)
                throw e
            }
        }
    }
    
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun getDeviceInfo(context: Context): Map<String, Any> {
        val deviceInfo = mutableMapOf<String, Any>()
        deviceInfo["os"] = "Android"

        // 生成具体的设备型号格式，例如"OPPO Reno11 PJH110"
        val brand = Build.BRAND ?: "Unknown"
        val model = Build.MODEL ?: "Unknown"
        val device = Build.DEVICE ?: "Unknown"

        // 根据实际输出情况调整逻辑，确保输出格式正确
        val detailedDeviceName = if (device != model && device.isNotEmpty() && device != "Unknown") {
            "$brand $model $device"
        } else {
            "$brand $model"
        }

        deviceInfo["deviceName"] = detailedDeviceName
        deviceInfo["deviceBrand"] = brand
        deviceInfo["deviceManufacturer"] = Build.MANUFACTURER ?: "Unknown"
        deviceInfo["deviceProduct"] = Build.PRODUCT ?: "Unknown"
        deviceInfo["deviceDevice"] = device
        deviceInfo["deviceBoard"] = Build.BOARD ?: "Unknown"
        deviceInfo["deviceHardware"] = Build.HARDWARE ?: "Unknown"
        deviceInfo["deviceVersion"] = "Android ${Build.VERSION.RELEASE}"
        deviceInfo["packageName"] = context.packageName
        deviceInfo["deviceId"] = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "Unknown"

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            deviceInfo["appVersionCode"] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
            deviceInfo["appVersionName"] = packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            deviceInfo["appVersionCode"] = 0
            deviceInfo["appVersionName"] = "Unknown"
        }
        
        // 获取Install Referrer信息（如果可用）
        getInstallReferrerInfo(context, deviceInfo)
        
        // deviceRegion: 设备地区代码
        deviceInfo["deviceRegion"] = Locale.getDefault().country
        
        // deviceLanguage: 设备语言设置
        deviceInfo["deviceLanguage"] = Locale.getDefault().toString()
        
        // channelSource: 应用分发渠道(通常在构建时设置，这里使用默认值)
        deviceInfo["channelSource"] = "GooglePlay"
        
        // networkStatus: 当前网络状态
        deviceInfo["networkStatus"] = getNetworkStatus(context)
        
        // ipAddress: 设备网络IP地址
        deviceInfo["ipAddress"] = getIpAddress() ?: ""
        
        return deviceInfo
    }

    private fun getInstallReferrerInfo(context: Context, deviceInfo: MutableMap<String, Any>) {
        try {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            try {
                                // 从Google Play获取安装来源信息
                                val response: ReferrerDetails = referrerClient.installReferrer
                                // 推广参数，标识用户从哪个渠道安装应用
                                deviceInfo["installReferrer"] = response.installReferrer ?: "Unknown"
                                // 用户点击推广链接的时间戳（秒）
                                deviceInfo["referrerClickTimestamp"] = response.referrerClickTimestampSeconds
                                // 应用安装开始的时间戳（秒）
                                deviceInfo["installBeginTimestamp"] = response.installBeginTimestampSeconds
                                // 是否为Google Play Instant体验
                                deviceInfo["googlePlayInstant"] = response.googlePlayInstantParam
                                // 安装时的应用版本
                                deviceInfo["installVersion"] = response.installVersion ?: "Unknown"
                            } catch (e: Exception) {
                                Log.e(TAG, "Error getting referrer details", e)
                                deviceInfo["installReferrerError"] = e.message ?: "Unknown error"
                            } finally {
                                referrerClient.endConnection()
                            }
                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            // API not available on the current Play Store app.
                            deviceInfo["installReferrerError"] = "FEATURE_NOT_SUPPORTED"
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            // Connection couldn't be established.
                            deviceInfo["installReferrerError"] = "SERVICE_UNAVAILABLE"
                        }
                    }
                }
                
                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    deviceInfo["installReferrerError"] = "SERVICE_DISCONNECTED"
                }
            })
        } catch (e: Exception) {
            deviceInfo["installReferrerError"] = e.message ?: "Unknown error"
        }
    }

    /**
     * 获取网络状态
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun getNetworkStatus(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                when {
                    networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WIFI"
                    networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                        "5G"
                    }
                    else -> "OTHER"
                }
            } else {
                "NONE"
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                when (networkInfo.type) {
                    ConnectivityManager.TYPE_WIFI -> "WIFI"
                    ConnectivityManager.TYPE_MOBILE -> "5G"
                    else -> "OTHER"
                }
            } else {
                "NONE"
            }
        }
    }

    // 获取IP地址
    private fun getIpAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in networkInterfaces) {
                val inetAddresses = networkInterface.inetAddresses
                for (inetAddress in inetAddresses) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting IP address", e)
        }
        return null
    }
}