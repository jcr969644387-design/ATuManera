package com.educalab.atumanera

import android.app.Application
import com.educalab.atumanera.data.local.AppDatabase
import com.educalab.atumanera.data.repository.CityRepository
import com.educalab.atumanera.data.seed.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AtuManeraApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: CityRepository by lazy { CityRepository(database) }
    private val seeder: DatabaseSeeder by lazy { DatabaseSeeder(database) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            seeder.seedCatalogIfNeeded()
            seeder.ensureUserAndCity()
        }
    }
}
