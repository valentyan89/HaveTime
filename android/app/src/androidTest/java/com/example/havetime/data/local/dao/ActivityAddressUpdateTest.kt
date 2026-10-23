package com.example.havetime.data.local.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.local.entity.ActivityEntity
import com.example.havetime.data.model.activity.LocationDto
import com.example.havetime.data.model.activity.TimeIntervalDto
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityAddressUpdateTest {
    private lateinit var database: ActivityDataBase
    private lateinit var dao: ActivityDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, ActivityDataBase::class.java).build()
        dao = database.activityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun lateGeocodingResultPreservesMovedTimeAndOtherEdits() = runBlocking {
        val activity = originalActivity()
        val id = dao.insert(activity).toInt()
        val moved = activity.copy(
            id = id,
            title = "Edited title",
            timeInterval = TimeIntervalDto(7_200_000L, 10_800_000L)
        )
        dao.update(moved)

        assertEquals(1, dao.updateGeocodedAddress(id, 55.75, 37.61, "Address", 123L))

        val saved = dao.getActivityById(id)!!
        assertEquals(moved.timeInterval, saved.timeInterval)
        assertEquals(moved.title, saved.title)
        assertEquals("Address", saved.location?.geocodedAddress)
    }

    @Test
    fun geocodingResultForOldCoordinatesIsIgnored() = runBlocking {
        val id = dao.insert(originalActivity()).toInt()

        assertEquals(0, dao.updateGeocodedAddress(id, 50.0, 30.0, "Stale address", 123L))
        assertEquals(null, dao.getActivityById(id)!!.location?.geocodedAddress)
    }

    private fun originalActivity() = ActivityEntity(
        userId = 1,
        title = "Activity",
        color = 0,
        timeInterval = TimeIntervalDto(3_600_000L, 7_200_000L),
        location = LocationDto(55.75, 37.61)
    )
}
