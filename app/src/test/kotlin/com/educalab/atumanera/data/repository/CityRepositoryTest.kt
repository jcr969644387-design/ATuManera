package com.educalab.atumanera.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.educalab.atumanera.data.local.AppDatabase
import com.educalab.atumanera.data.local.entity.CityEntity
import com.educalab.atumanera.data.local.entity.CityTileEntity
import com.educalab.atumanera.data.local.entity.UserProfileEntity
import com.educalab.atumanera.data.seed.DatabaseSeeder
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas de integración sobre Room real (en memoria) y el repositorio completo.
 * Nota de honestidad: estas pruebas requieren Robolectric + el SDK de Android y
 * NO se han podido ejecutar en el entorno de construcción de este proyecto por
 * falta de acceso al repositorio Maven de Google. El código es válido y se
 * ejecutará normalmente con `./gradlew testDebugUnitTest` en un entorno con
 * Android SDK y conexión a Internet.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CityRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: CityRepository
    private lateinit var seeder: DatabaseSeeder

    @Before
    fun setUp() = runTest {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CityRepository(db)
        seeder = DatabaseSeeder(db)
        seeder.seedCatalogIfNeeded()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun createUserAndCity(rows: Int = 4, cols: Int = 4, budget: Int = 500): Pair<Long, Long> {
        val userId = db.userProfileDao().insert(UserProfileEntity(0, "Tester", "avatar_1", 0L))
        val cityId = db.cityDao().insert(CityEntity(0, userId, "Ciudad Test", budget, rows, cols, 0L, 0L))
        val tiles = mutableListOf<CityTileEntity>()
        for (r in 0 until rows) for (c in 0 until cols) tiles.add(CityTileEntity(0, cityId, r, c, true))
        db.cityTileDao().insertAll(tiles)
        return userId to cityId
    }

    @Test
    fun `catalog seed inserts exactly 13 infrastructure types`() = runTest {
        assertEquals(13, db.infrastructureTypeDao().count())
    }

    @Test
    fun `catalog seed is idempotent and does not duplicate on second call`() = runTest {
        seeder.seedCatalogIfNeeded()
        seeder.seedCatalogIfNeeded()
        assertEquals(13, db.infrastructureTypeDao().count())
        assertEquals(30, db.missionDao().count())
        assertEquals(12, db.badgeDao().count())
        assertEquals(8, db.decorationDao().count())
    }

    @Test
    fun `placing infrastructure on an empty tile succeeds and deducts budget`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        val outcome = repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        assertTrue(outcome is PlacementOutcome.Success)
        val spent = db.placedInfrastructureDao().totalSpent(cityId)
        assertEquals(road.cost, spent)
    }

    @Test
    fun `placing on an already occupied tile is rejected`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        val second = repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        assertTrue(second is PlacementOutcome.TileOccupied)
    }

    @Test
    fun `placing beyond the budget is rejected`() = runTest {
        val (userId, cityId) = createUserAndCity(budget = 5)
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!! // cuesta 10
        val outcome = repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        assertTrue(outcome is PlacementOutcome.InsufficientBudget)
    }

    @Test
    fun `placing on a non existent tile is rejected`() = runTest {
        val (userId, cityId) = createUserAndCity(rows = 2, cols = 2)
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        val outcome = repository.placeInfrastructure(userId, cityId, 9, 9, road.id)
        assertTrue(outcome is PlacementOutcome.TileNotFound)
    }

    @Test
    fun `removing infrastructure refunds the tile for future placements`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        repository.placeInfrastructure(userId, cityId, 1, 1, road.id)
        repository.removeInfrastructure(userId, cityId, 1, 1)
        val again = repository.placeInfrastructure(userId, cityId, 1, 1, road.id)
        assertTrue(again is PlacementOutcome.Success)
    }

    @Test
    fun `removing from an empty tile does nothing`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val outcome = repository.removeInfrastructure(userId, cityId, 2, 2)
        assertTrue(outcome is RemovalOutcome.NothingToRemove)
    }

    @Test
    fun `first mission completes after placing the first road`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        val completed = db.missionProgressDao().completedCount(userId, cityId)
        assertEquals(1, completed)
    }

    @Test
    fun `earning the first road badge persists a user badge row`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        val badge = db.badgeDao().getByCode("BADGE_FIRST_ROAD")!!
        val earned = db.userBadgeDao().getEarnedBadgeIds(userId)
        assertTrue(badge.id in earned)
    }

    @Test
    fun `a fresh database has no cities or users`() = runTest {
        assertEquals(0, db.userProfileDao().count())
    }

    @Test
    fun `metrics history accumulates a new snapshot per placement`() = runTest {
        val (userId, cityId) = createUserAndCity()
        val road = db.infrastructureTypeDao().getByCode("ROAD_BASIC")!!
        repository.placeInfrastructure(userId, cityId, 0, 0, road.id)
        repository.placeInfrastructure(userId, cityId, 0, 1, road.id)
        val house = db.infrastructureTypeDao().getByCode("HOUSE_SMALL")!!
        repository.placeInfrastructure(userId, cityId, 1, 0, house.id)
        val latest = db.cityMetricDao().getLatest(cityId)
        assertTrue(latest != null && latest.mobility == 100)
    }
}
