package com.jmin.monthlytodo.database

import androidx.room.*
import com.jmin.monthlytodo.model.Holiday
import java.util.Date

@Dao
interface HolidayDao {
    @Query("SELECT * FROM holidays")
    suspend fun getAllHolidays(): List<Holiday>
    
    @Query("SELECT * FROM holidays WHERE date = :date")
    suspend fun getHolidaysForDate(date: Date): List<Holiday>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoliday(holiday: Holiday)
    
    @Delete
    suspend fun deleteHoliday(holiday: Holiday)
}