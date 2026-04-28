package com.vishal2376.snaptick.presentation.common

import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * Locks the post-Sprint-2 behavior: repeating tasks must surface on every
 * future weekday in their set, not only on creation date. Reproduces the
 * v3.3-era user complaint as a regression test.
 */
class FilterTasksUtilsTest {

	private val createdMon = LocalDate.of(2026, 4, 27) // Monday

	private fun repeatTask(
		uuid: String = "u1",
		repeatWeekdays: String,
		date: LocalDate = createdMon,
	) = Task(
		id = 1, uuid = uuid, title = "T",
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		isRepeated = true, repeatWeekdays = repeatWeekdays, date = date,
	)

	private fun oneOffTask(
		date: LocalDate,
		uuid: String = "o1",
	) = Task(
		id = 2, uuid = uuid, title = "T",
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		isRepeated = false, date = date,
	)

	@Test fun `filterTasksByDate includes future repeats whose weekday matches`() {
		val tasks = listOf(repeatTask(repeatWeekdays = "0,2,4")) // Mon Wed Fri
		val nextWed = createdMon.plusDays(2)
		val nextFri = createdMon.plusDays(4)
		val nextMon = createdMon.plusWeeks(1)
		assertEquals(1, filterTasksByDate(tasks, nextWed).size)
		assertEquals(1, filterTasksByDate(tasks, nextFri).size)
		assertEquals(1, filterTasksByDate(tasks, nextMon).size)
	}

	@Test fun `filterTasksByDate excludes future repeats whose weekday does not match`() {
		val tasks = listOf(repeatTask(repeatWeekdays = "0,2,4")) // Mon Wed Fri
		val nextTue = createdMon.plusDays(1)
		val nextSun = createdMon.plusDays(6)
		assertTrue(filterTasksByDate(tasks, nextTue).isEmpty())
		assertTrue(filterTasksByDate(tasks, nextSun).isEmpty())
	}

	@Test fun `filterTasksByDate excludes one-off task on different date`() {
		val tasks = listOf(oneOffTask(date = createdMon))
		assertEquals(1, filterTasksByDate(tasks, createdMon).size)
		assertTrue(filterTasksByDate(tasks, createdMon.plusDays(1)).isEmpty())
	}

	@Test fun `filterTasksByDate excludes repeats before creation date`() {
		val tasks = listOf(repeatTask(repeatWeekdays = "0")) // Mon, created 2026-04-27
		val priorMon = createdMon.minusWeeks(1)
		assertFalse(filterTasksByDate(tasks, priorMon).any { it.uuid == "u1" })
	}
}
