package com.educalab.atumanera.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.educalab.atumanera.data.local.dao.BadgeDao
import com.educalab.atumanera.data.local.dao.CityDao
import com.educalab.atumanera.data.local.dao.CityMetricDao
import com.educalab.atumanera.data.local.dao.CityTileDao
import com.educalab.atumanera.data.local.dao.DecorationDao
import com.educalab.atumanera.data.local.dao.InfrastructureTypeDao
import com.educalab.atumanera.data.local.dao.MissionDao
import com.educalab.atumanera.data.local.dao.MissionProgressDao
import com.educalab.atumanera.data.local.dao.MissionRequirementDao
import com.educalab.atumanera.data.local.dao.PlacedInfrastructureDao
import com.educalab.atumanera.data.local.dao.ProgressDao
import com.educalab.atumanera.data.local.dao.RoadConnectionDao
import com.educalab.atumanera.data.local.dao.ServiceCoverageDao
import com.educalab.atumanera.data.local.dao.UnlockedDecorationDao
import com.educalab.atumanera.data.local.dao.UserBadgeDao
import com.educalab.atumanera.data.local.dao.UserProfileDao
import com.educalab.atumanera.data.local.entity.BadgeEntity
import com.educalab.atumanera.data.local.entity.CityEntity
import com.educalab.atumanera.data.local.entity.CityMetricEntity
import com.educalab.atumanera.data.local.entity.CityTileEntity
import com.educalab.atumanera.data.local.entity.DecorationEntity
import com.educalab.atumanera.data.local.entity.InfrastructureTypeEntity
import com.educalab.atumanera.data.local.entity.MissionEntity
import com.educalab.atumanera.data.local.entity.MissionProgressEntity
import com.educalab.atumanera.data.local.entity.MissionRequirementEntity
import com.educalab.atumanera.data.local.entity.PlacedInfrastructureEntity
import com.educalab.atumanera.data.local.entity.ProgressEntity
import com.educalab.atumanera.data.local.entity.RoadConnectionEntity
import com.educalab.atumanera.data.local.entity.ServiceCoverageEntity
import com.educalab.atumanera.data.local.entity.UnlockedDecorationEntity
import com.educalab.atumanera.data.local.entity.UserBadgeEntity
import com.educalab.atumanera.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        CityEntity::class,
        CityTileEntity::class,
        InfrastructureTypeEntity::class,
        PlacedInfrastructureEntity::class,
        RoadConnectionEntity::class,
        ServiceCoverageEntity::class,
        MissionEntity::class,
        MissionRequirementEntity::class,
        MissionProgressEntity::class,
        CityMetricEntity::class,
        DecorationEntity::class,
        UnlockedDecorationEntity::class,
        ProgressEntity::class,
        BadgeEntity::class,
        UserBadgeEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun cityDao(): CityDao
    abstract fun cityTileDao(): CityTileDao
    abstract fun infrastructureTypeDao(): InfrastructureTypeDao
    abstract fun placedInfrastructureDao(): PlacedInfrastructureDao
    abstract fun roadConnectionDao(): RoadConnectionDao
    abstract fun serviceCoverageDao(): ServiceCoverageDao
    abstract fun missionDao(): MissionDao
    abstract fun missionRequirementDao(): MissionRequirementDao
    abstract fun missionProgressDao(): MissionProgressDao
    abstract fun cityMetricDao(): CityMetricDao
    abstract fun decorationDao(): DecorationDao
    abstract fun unlockedDecorationDao(): UnlockedDecorationDao
    abstract fun progressDao(): ProgressDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userBadgeDao(): UserBadgeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "atumanera.db"
                ).build().also { INSTANCE = it }
            }
    }
}
