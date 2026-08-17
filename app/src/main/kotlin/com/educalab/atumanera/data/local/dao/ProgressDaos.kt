package com.educalab.atumanera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.educalab.atumanera.data.local.entity.BadgeEntity
import com.educalab.atumanera.data.local.entity.CityMetricEntity
import com.educalab.atumanera.data.local.entity.DecorationEntity
import com.educalab.atumanera.data.local.entity.MissionEntity
import com.educalab.atumanera.data.local.entity.MissionProgressEntity
import com.educalab.atumanera.data.local.entity.MissionRequirementEntity
import com.educalab.atumanera.data.local.entity.ProgressEntity
import com.educalab.atumanera.data.local.entity.ServiceCoverageEntity
import com.educalab.atumanera.data.local.entity.UnlockedDecorationEntity
import com.educalab.atumanera.data.local.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceCoverageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coverage: List<ServiceCoverageEntity>)

    @Query("DELETE FROM service_coverage WHERE cityId = :cityId AND category = :category")
    suspend fun clearCategory(cityId: Long, category: String)

    @Query("SELECT COUNT(*) FROM service_coverage WHERE cityId = :cityId AND category = :category AND covered = 1")
    suspend fun coveredCount(cityId: Long, category: String): Int

    @Query("SELECT * FROM service_coverage WHERE cityId = :cityId")
    fun observeForCity(cityId: Long): Flow<List<ServiceCoverageEntity>>
}

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mission: MissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(missions: List<MissionEntity>)

    @Query("SELECT * FROM mission ORDER BY orderIndex ASC")
    fun observeAll(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM mission ORDER BY orderIndex ASC")
    suspend fun getAllList(): List<MissionEntity>

    @Query("SELECT * FROM mission WHERE id = :id")
    suspend fun getById(id: Long): MissionEntity?

    @Query("SELECT * FROM mission WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): MissionEntity?

    @Query("SELECT COUNT(*) FROM mission")
    suspend fun count(): Int
}

@Dao
interface MissionRequirementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(requirements: List<MissionRequirementEntity>)

    @Query("SELECT * FROM mission_requirement WHERE missionId = :missionId")
    suspend fun getForMission(missionId: Long): List<MissionRequirementEntity>
}

@Dao
interface MissionProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: MissionProgressEntity): Long

    @Query("SELECT * FROM mission_progress WHERE userId = :userId AND cityId = :cityId")
    fun observeForCity(userId: Long, cityId: Long): Flow<List<MissionProgressEntity>>

    @Query("SELECT * FROM mission_progress WHERE userId = :userId AND cityId = :cityId AND missionId = :missionId LIMIT 1")
    suspend fun getFor(userId: Long, cityId: Long, missionId: Long): MissionProgressEntity?

    @Query("SELECT COUNT(*) FROM mission_progress WHERE userId = :userId AND cityId = :cityId AND status = 'COMPLETED'")
    suspend fun completedCount(userId: Long, cityId: Long): Int
}

@Dao
interface CityMetricDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: CityMetricEntity): Long

    @Query("SELECT * FROM city_metric WHERE cityId = :cityId ORDER BY timestamp ASC")
    fun observeHistory(cityId: Long): Flow<List<CityMetricEntity>>

    @Query("SELECT * FROM city_metric WHERE cityId = :cityId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(cityId: Long): CityMetricEntity?

    @Query("DELETE FROM city_metric WHERE cityId = :cityId AND id NOT IN (SELECT id FROM city_metric WHERE cityId = :cityId ORDER BY timestamp DESC LIMIT :keep)")
    suspend fun trimHistory(cityId: Long, keep: Int)
}

@Dao
interface DecorationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(decorations: List<DecorationEntity>)

    @Query("SELECT * FROM decoration ORDER BY id")
    fun observeAll(): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM decoration WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): DecorationEntity?

    @Query("SELECT COUNT(*) FROM decoration")
    suspend fun count(): Int
}

@Dao
interface UnlockedDecorationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(unlocked: UnlockedDecorationEntity): Long

    @Query("SELECT * FROM unlocked_decoration WHERE userId = :userId")
    fun observeForUser(userId: Long): Flow<List<UnlockedDecorationEntity>>

    @Query("SELECT decorationId FROM unlocked_decoration WHERE userId = :userId")
    suspend fun getUnlockedIds(userId: Long): List<Long>
}

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity): Long

    @Update
    suspend fun update(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE userId = :userId LIMIT 1")
    fun observeForUser(userId: Long): Flow<ProgressEntity?>

    @Query("SELECT * FROM progress WHERE userId = :userId LIMIT 1")
    suspend fun getForUser(userId: Long): ProgressEntity?
}

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Query("SELECT * FROM badge ORDER BY id")
    fun observeAll(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badge WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): BadgeEntity?

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun count(): Int
}

@Dao
interface UserBadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userBadge: UserBadgeEntity): Long

    @Query("SELECT * FROM user_badge WHERE userId = :userId")
    fun observeForUser(userId: Long): Flow<List<UserBadgeEntity>>

    @Query("SELECT badgeId FROM user_badge WHERE userId = :userId")
    suspend fun getEarnedBadgeIds(userId: Long): List<Long>
}
