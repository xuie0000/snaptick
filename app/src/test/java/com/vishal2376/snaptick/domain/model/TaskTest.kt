package com.vishal2376.snaptick.domain.model

import com.vishal2376.snaptick.util.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class TaskTest {

	private fun task(
		start: LocalTime = LocalTime.of(9, 0),
		end: LocalTime = LocalTime.of(10, 0),
		repeatWeekdays: String = "",
		date: LocalDate = LocalDate.now(),
	) = Task(
		id = 1,
		uuid = "u",
		title = "t",
		startTime = start,
		endTime = end,
		repeatWeekdays = repeatWeekdays,
		date = date
	)

	@Test
	fun `duration returns seconds between start and end`() {
		val d = task(LocalTime.of(9, 0), LocalTime.of(10, 30)).getDuration()
		assertEquals(90 * 60L, d)
	}

	@Test
	fun `midnight crossing returns correct duration`() {
		val d = task(LocalTime.of(23, 0), LocalTime.of(1, 0)).getDuration()
		assertEquals(7200L, d)
	}

	@Test
	fun `non crossing duration unchanged`() {
		val d = task(LocalTime.of(9, 0), LocalTime.of(10, 30)).getDuration()
		assertEquals(5400L, d)
	}

	@Test
	fun `isAllDayTaskEnabled true when start equals end`() {
		val t = task(LocalTime.of(8, 0), LocalTime.of(8, 0))
		assertTrue(t.isAllDayTaskEnabled())
	}

	@Test
	fun `isAllDayTaskEnabled false when start differs from end`() {
		assertFalse(task().isAllDayTaskEnabled())
	}

	@Test
	fun `getRepeatWeekList returns empty when repeatWeekdays blank`() {
		assertEquals(emptyList<Int>(), task(repeatWeekdays = "").getRepeatWeekList())
	}

	@Test
	fun `getRepeatWeekList parses comma separated indices`() {
		assertEquals(listOf(0, 2, 4), task(repeatWeekdays = "0,2,4").getRepeatWeekList())
	}

	@Test
	fun `isValidPomodoroSession requires at least MIN_VALID seconds elapsed`() {
		val t = task(LocalTime.of(9, 0), LocalTime.of(9, 30)) // 1800s total
		val minSessionSec = Constants.MIN_VALID_POMODORO_SESSION * 60
		// timeLeft such that elapsed (total - timeLeft) == minSessionSec → valid
		val validTimeLeft = 1800L - minSessionSec
		assertTrue(t.isValidPomodoroSession(validTimeLeft))
		// elapsed just below min → invalid
		assertFalse(t.isValidPomodoroSession(validTimeLeft + 1))
	}

	@Test
	fun `shouldOccurOn one-off matches only its date`() {
		val day = LocalDate.of(2026, 4, 27)
		val t = task(date = day)
		assertTrue(t.shouldOccurOn(day))
		assertFalse(t.shouldOccurOn(day.plusDays(1)))
		assertFalse(t.shouldOccurOn(day.minusDays(1)))
	}

	@Test
	fun `shouldOccurOn repeat matches weekdays in set on or after creation date`() {
		// Mon, Wed, Fri = 0, 2, 4
		val createdMon = LocalDate.of(2026, 4, 27) // Monday
		val t = Task(
			id = 1, uuid = "u", title = "t",
			startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
			isRepeated = true, repeatWeekdays = "0,2,4", date = createdMon
		)
		assertTrue(t.shouldOccurOn(createdMon))                        // Mon
		assertTrue(t.shouldOccurOn(createdMon.plusDays(2)))            // Wed
		assertTrue(t.shouldOccurOn(createdMon.plusDays(4)))            // Fri
		assertTrue(t.shouldOccurOn(createdMon.plusWeeks(2)))           // Mon two weeks later
		assertFalse(t.shouldOccurOn(createdMon.plusDays(1)))           // Tue
		assertFalse(t.shouldOccurOn(createdMon.minusDays(1)))          // before creation
	}
}
