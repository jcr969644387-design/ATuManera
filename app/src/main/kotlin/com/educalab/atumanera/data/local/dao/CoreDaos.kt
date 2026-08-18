package com.educalab.atumanera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.educalab.atumanera.data.local.entity.CityEntity
import com.educalab.atumanera.data.local.entity.CityTileEntity
import com.educalab.atumanera.data.local.entity.InfrastructureTypeEntity
import com.educalab.atumanera.data.local.entity.PlacedInfrastructureEntity
import com.educalab.atumanera.data.local.entity.RoadConnectionEntity
import com.educalab.atumanera.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserProfileEntity): Long

    @Update
    suspend fun update(user: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun observeById(id: Long): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getById(id: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profile ORDER BY id ASC LIMIT 1")
    suspend fun getFirst(): UserProfileEntity?

    @Query("SELECT * FROM user_profile ORDER BY id ASC LIMIT 1")
    fun observeFirst(): Flow<UserProfileEntity?>

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun count(): Int
}

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity): Long

    @Update
    suspend fun update(city: CityEntity)

    @Query("SELECT * FROM city WHERE userId = :userId AND name != 'Modo Libre' ORDER BY id DESC LIMIT 1")
    fun observeLatestForUser(userId: Long): Flow<CityEntity?>

    @Query("SELECT * FROM city WHERE userId = :userId AND name != 'Modo Libre' ORDER BY id DESC LIMIT 1")
    suspend fun getLatestForUser(userId: Long): CityEntity?

    @Query("SELECT * FROM city WHERE userId = :userId AND name = 'Modo Libre' ORDER BY id DESC LIMIT 1")
    fun observeFreeCityForUser(userId: Long): Flow<CityEntity?>

    @Query("SELECT * FROM city WHERE userId = :userId AND name = 'Modo Libre' ORDER BY id DESC LIMIT 1")
    suspend fun getFreeCityForUser(userId: Long): CityEntity?

    @Query("SELECT * FROM city WHERE id = :id")
    suspend fun getById(id: Long): CityEntity?
}

@Dao
interface CityTileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tiles: List<CityTileEntity>): List<Long>

    @Query("SELECT * FROM city_tile WHERE cityId = :cityId ORDER BY row, col")
    fun observeTilesForCity(cityId: Long): Flow<List<CityTileEntity>>

    @Query("SELECT * FROM city_tile WHERE cityId = :cityId ORDER BY row, col")
    suspend fun getTilesForCity(cityId: Long): List<CityTileEntity>

    @Query("SELECT * FROM city_tile WHERE cityId = :cityId AND row = :row AND col = :col LIMIT 1")
    suspend fun getTileAt(cityId: Long, row: Int, col: Int): CityTileEntity?

    @Query("SELECT COUNT(*) FROM city_tile WHERE cityId = :cityId")
    suspend fun countForCity(cityId: Long): Int
}

@Dao
interface InfrastructureTypeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(types: List<InfrastructureTypeEntity>)

    @Query("SELECT * FROM infrastructure_type ORDER BY category, cost")
    fun observeAll(): Flow<List<InfrastructureTypeEntity>>

    @Query("SELECT * FROM infrastructure_type WHERE category = :category ORDER BY cost")
    fun observeByCategory(category: String): Flow<List<InfrastructureTypeEntity>>

    @Query("SELECT * FROM infrastructure_type WHERE id = :id")
    suspend fun getById(id: Long): InfrastructureTypeEntity?

    @Query("SELECT * FROM infrastructure_type WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): InfrastructureTypeEntity?

    @Query("SELECT COUNT(*) FROM infrastructure_type")
    suspend fun count(): Int
}

@Dao
interface PlacedInfrastructureDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(placed: PlacedInfrastructureEntity): Long

    @Query("DELETE FROM placed_infrastructure WHERE cityId = :cityId AND tileId = :tileId")
    suspend fun removeAt(cityId: Long, tileId: Long)

    @Query("DELETE FROM placed_infrastructure WHERE cityId = :cityId")
    suspend fun clearCity(cityId: Long)

    @Query(
        """
        DELETE FROM placed_infrastructure
        WHERE cityId = :cityId AND infrastructureTypeId IN
            (SELECT id FROM infrastructure_type WHERE category = :category)
        """
    )
    suspend fun clearCategory(cityId: Long, category: String)

    @Query("SELECT * FROM placed_infrastructure WHERE cityId = :cityId")
    fun observeForCity(cityId: Long): Flow<List<PlacedInfrastructureEntity>>

    @Query("SELECT * FROM placed_infrastructure WHERE cityId = :cityId")
    suspend fun getForCity(cityId: Long): List<PlacedInfrastructureEntity>

    @Query("SELECT * FROM placed_infrastructure WHERE cityId = :cityId AND tileId = :tileId LIMIT 1")
    suspend fun getAt(cityId: Long, tileId: Long): PlacedInfrastructureEntity?

    @Query(
        """
        SELECT it.code AS code, COUNT(*) AS count FROM placed_infrastructure pi
        JOIN infrastructure_type it ON it.id = pi.infrastructureTypeId
        WHERE pi.cityId = :cityId
        GROUP BY it.code
        """
    )
    suspend fun countsByCode(cityId: Long): List<InfraCountRow>

    @Query(
        """
        SELECT COALESCE(SUM(it.cost), 0) FROM placed_infrastructure pi
        JOIN infrastructure_type it ON it.id = pi.infrastructureTypeId
        WHERE pi.cityId = :cityId
        """
    )
    suspend fun totalSpent(cityId: Long): Int

    @Query(
        """
        SELECT pi.*, it.category as infraCategory, it.coverageRadius as infraCoverageRadius, it.code as infraCode, ct.row as tileRow, ct.col as tileCol
        FROM placed_infrastructure pi
        JOIN infrastructure_type it ON it.id = pi.infrastructureTypeId
        JOIN city_tile ct ON ct.id = pi.tileId
        WHERE pi.cityId = :cityId
        """
    )
    suspend fun getEnrichedForCity(cityId: Long): List<EnrichedPlacementRow>

    @Transaction
    suspend fun placeAndLinkRoads(
        placed: PlacedInfrastructureEntity,
        roadConnections: List<RoadConnectionEntity>,
        roadConnectionDao: RoadConnectionDao
    ): Long {
        val id = insert(placed)
        if (roadConnections.isNotEmpty()) {
            roadConnectionDao.insertAll(roadConnections)
        }
        return id
    }
}

data class InfraCountRow(val code: String, val count: Int)

/** Fila enriquecida usada para reconstruir el estado de la cuadrícula sin N+1 queries. */
data class EnrichedPlacementRow(
    val id: Long,
    val cityId: Long,
    val tileId: Long,
    val infrastructureTypeId: Long,
    val placedAt: Long,
    val infraCategory: String,
    val infraCoverageRadius: Int,
    val infraCode: String,
    val tileRow: Int,
    val tileCol: Int
)

@Dao
interface RoadConnectionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(connections: List<RoadConnectionEntity>)

    @Query("DELETE FROM road_connection WHERE cityId = :cityId AND (tileAId = :tileId OR tileBId = :tileId)")
    suspend fun removeInvolvingTile(cityId: Long, tileId: Long)

    @Query("DELETE FROM road_connection WHERE cityId = :cityId")
    suspend fun clearCity(cityId: Long)

    @Query("SELECT * FROM road_connection WHERE cityId = :cityId")
    suspend fun getForCity(cityId: Long): List<RoadConnectionEntity>
}
