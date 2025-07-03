package kedokato.myhoubackend.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class WeekDates(
    val monday: LocalDate,
    val sunday: LocalDate
) {
    fun toApiFormat(): String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        return "${monday.format(formatter)}-${sunday.format(formatter)}"
    }
}

data class MonthData(
    val year: Int,
    val month: Int,
    val weeks: List<WeekDates>
) {
    fun getAllWeekApiFormats(): List<String> {
        return weeks.map { it.toApiFormat() }
    }

    fun getDisplayName(): String {
        return String.format("%02d/%d", month, year)
    }
}

class DateUtils {
    private val vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh")

    // Lấy tuần với offset (0 = hiện tại, 1 = tuần sau, -1 = tuần trước)
    fun getWeek(weekOffset: Int = 0): WeekDates {
        val today = LocalDate.now(vietnamZone)
        val targetMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .plusWeeks(weekOffset.toLong())
        val targetSunday = targetMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return WeekDates(targetMonday, targetSunday)
    }

    fun getCurrentWeek(): WeekDates = getWeek(0)

    fun getPreviousWeek(): WeekDates = getWeek(-1)

    fun getNextWeek(): WeekDates = getWeek(1)

    // Lấy tháng với offset (0 = hiện tại, 1 = tháng sau, -1 = tháng trước, 2 = 2 tháng sau...)
    fun getMonth(monthOffset: Int = 0): MonthData {
        val today = LocalDate.now(vietnamZone)
        val targetMonth = today.plusMonths(monthOffset.toLong())
        return getMonthData(targetMonth.year, targetMonth.monthValue)
    }

    fun getCurrentMonth(): MonthData = getMonth(0)

    fun getPreviousMonth(): MonthData = getMonth(-1)

    fun getNextMonth(): MonthData = getMonth(1)

    // Hàm helper để lấy tất cả các tuần trong một tháng cụ thể
    private fun getMonthData(year: Int, month: Int): MonthData {
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())

        val weeks = mutableListOf<WeekDates>()

        // Tìm tuần đầu tiên chứa ngày đầu tháng
        var currentMonday = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        while (currentMonday <= lastDayOfMonth) {
            val currentSunday = currentMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            // Chỉ thêm tuần nếu nó có ít nhất một ngày trong tháng
            if (currentSunday >= firstDayOfMonth && currentMonday <= lastDayOfMonth) {
                weeks.add(WeekDates(currentMonday, currentSunday))
            }

            currentMonday = currentMonday.plusWeeks(1)
        }

        return MonthData(year, month, weeks)
    }

    // Hàm tiện ích để lấy format API cho tuần cụ thể
    fun getWeekApiFormat(weekOffset: Int = 0): String {
        return getWeek(weekOffset).toApiFormat()
    }

    // Hàm tiện ích để lấy tất cả format API cho tháng cụ thể
    fun getMonthApiFormats(monthOffset: Int = 0): List<String> {
        return getMonth(monthOffset).getAllWeekApiFormats()
    }
}