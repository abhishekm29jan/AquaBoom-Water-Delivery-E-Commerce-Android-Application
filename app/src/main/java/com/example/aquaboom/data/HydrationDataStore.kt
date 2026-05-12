package com.example.aquaboom.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.hydrationStore by preferencesDataStore(name = "hydration")

object HydrationKeys {
    val GOAL = intPreferencesKey("goal")
    val INTAKE = intPreferencesKey("intake")
    val TIME = longPreferencesKey("time")
}

class HydrationDataStore(private val context: Context) {

    val data = context.hydrationStore.data.map { prefs ->
        Triple(
            prefs[HydrationKeys.GOAL] ?: 2500,
            prefs[HydrationKeys.INTAKE] ?: 0,
            prefs[HydrationKeys.TIME] ?: System.currentTimeMillis()
        )
    }

    suspend fun save(goal: Int, intake: Int) {
        context.hydrationStore.edit { prefs ->
            prefs[HydrationKeys.GOAL] = goal
            prefs[HydrationKeys.INTAKE] = intake
            prefs[HydrationKeys.TIME] = System.currentTimeMillis()
        }
    }
}