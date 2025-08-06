package com.jmin.monthlytodo

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    var client = createSupabaseClient(
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNlZnBxdGJmZHh4ZHVpZGxqaGpuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTM5NTQ0MjIsImV4cCI6MjA2OTUzMDQyMn0.Jsxcv45Y4GwCQ-EldiBnxtw0pz1-qPoHrIAvAjw4nwM",
        supabaseUrl = "https://sefpqtbfdxxduidljhjn.supabase.co"
    ) {
        install(Postgrest)
//        install(Auth)
    }
}