package com.educalab.atumanera.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alias: String,
    val avatarCode: String,
    val createdAt: Long
)

@Entity(
    tableName = "city",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("userId")]
)
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val budgetTotal: Int,
    val rows: Int,
    val cols: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "city_tile",
    foreignKeys = [
        ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["cityId", "row", "col"], unique = true)]
)
data class CityTileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityId: Long,
    val row: Int,
    val col: Int,
    val buildable: Boolean = true
)

@Entity(tableName = "infrastructure_type", indices = [Index(value = ["code"], unique = true)])
data class InfrastructureTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val category: String,
    val description: String,
    val cost: Int,
    val coverageRadius: Int,
    val iconRes: String
)

@Entity(
    tableName = "placed_infrastructure",
    foreignKeys = [
        ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CityTileEntity::class, parentColumns = ["id"], childColumns = ["tileId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = InfrastructureTypeEntity::class, parentColumns = ["id"], childColumns = ["infrastructureTypeId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["cityId", "tileId"], unique = true),
        Index("infrastructureTypeId")
    ]
)
data class PlacedInfrastructureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityId: Long,
    val tileId: Long,
    val infrastructureTypeId: Long,
    val placedAt: Long
)

@Entity(
    tableName = "road_connection",
    foreignKeys = [
        ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CityTileEntity::class, parentColumns = ["id"], childColumns = ["tileAId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CityTileEntity::class, parentColumns = ["id"], childColumns = ["tileBId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["cityId", "tileAId", "tileBId"], unique = true)]
)
data class RoadConnectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityId: Long,
    val tileAId: Long,
    val tileBId: Long
)

@Entity(
    tableName = "service_coverage",
    foreignKeys = [
        ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CityTileEntity::class, parentColumns = ["id"], childColumns = ["tileId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["cityId", "tileId", "category"], unique = true)]
)
data class ServiceCoverageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityId: Long,
    val tileId: Long,
    val category: String,
    val covered: Boolean,
    val computedAt: Long
)

@Entity(tableName = "mission", indices = [Index(value = ["code"], unique = true)])
data class MissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val category: String,
    val orderIndex: Int,
    val rewardXp: Int,
    val rewardBadgeCode: String?
)

@Entity(
    tableName = "mission_requirement",
    foreignKeys = [ForeignKey(entity = MissionEntity::class, parentColumns = ["id"], childColumns = ["missionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("missionId")]
)
data class MissionRequirementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val missionId: Long,
    val type: String,
    val requirementKey: String,
    val targetValue: Int
)

@Entity(
    tableName = "mission_progress",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MissionEntity::class, parentColumns = ["id"], childColumns = ["missionId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["userId", "cityId", "missionId"], unique = true)]
)
data class MissionProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val cityId: Long,
    val missionId: Long,
    val status: String,
    val progressPercent: Int,
    val completedAt: Long?
)

@Entity(
    tableName = "city_metric",
    foreignKeys = [ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("cityId")]
)
data class CityMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityId: Long,
    val timestamp: Long,
    val mobility: Int,
    val servicesScore: Int,
    val greenScore: Int,
    val educationCoverage: Int,
    val healthCoverage: Int,
    val waterCoverage: Int,
    val budgetSpent: Int,
    val budgetTotal: Int
)

@Entity(tableName = "decoration", indices = [Index(value = ["code"], unique = true)])
data class DecorationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val description: String,
    val iconRes: String,
    val unlockConditionCode: String
)

@Entity(
    tableName = "unlocked_decoration",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DecorationEntity::class, parentColumns = ["id"], childColumns = ["decorationId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["userId", "decorationId"], unique = true)]
)
data class UnlockedDecorationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val decorationId: Long,
    val unlockedAt: Long
)

@Entity(
    tableName = "progress",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val cityId: Long,
    val currentChapter: Int,
    val totalXp: Int,
    val missionsCompleted: Int,
    val updatedAt: Long
)

@Entity(tableName = "badge", indices = [Index(value = ["code"], unique = true)])
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val description: String,
    val iconRes: String,
    val category: String
)

@Entity(
    tableName = "user_badge",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = BadgeEntity::class, parentColumns = ["id"], childColumns = ["badgeId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["userId", "badgeId"], unique = true)]
)
data class UserBadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val badgeId: Long,
    val earnedAt: Long
)
