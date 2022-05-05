package com.xtransformers.utils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Java8 日期和时间 API
 * 对应帖子 https://foggy-airbus-610.notion.site/Java8-API-71e484547208463395b5b269d51b81cc
 *
 * @author daniel
 * @date 2022-05-04
 */
public class Java8DateTimeStudy {

    public static void main(String[] args) {
        // System.setProperty("user.timezone", "UTC");
        // 获取当前时刻
        Instant now = Instant.now();
        now = Instant.ofEpochMilli(System.currentTimeMillis());
        System.out.println("now = " + now);

        // 获取系统默认时区的当前日期和时间
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("ldt = " + ldt);
        // 获取年月日时分秒 星期等信息
        System.out.println("ldt.getYear() = " + ldt.getYear());
        System.out.println("ldt.getMonthValue() = " + ldt.getMonthValue());
        System.out.println("ldt.getDayOfMonth() = " + ldt.getDayOfMonth());
        System.out.println("ldt.getHour() = " + ldt.getHour());
        System.out.println("ldt.getMinute() = " + ldt.getMinute());
        System.out.println("ldt.getSecond() = " + ldt.getSecond());
        System.out.println("ldt.getDayOfWeek() = " + ldt.getDayOfWeek());
        // 直接使用年月日等信息构建 LocalDateTime
        LocalDateTime ldt2 = LocalDateTime.of(2022, 5, 4, 16, 56, 36);

        ZoneId systemDefault = ZoneId.systemDefault();
        ZoneId beijingZone = ZoneId.of("GMT+08:00");

        // 表示 2022年5月4日
        LocalDate ld = LocalDate.of(2022, 5, 4);
        // 当前时刻按系统默认时区解读的日期
        LocalDate date = LocalDate.now();
        // 表示 18:09:34
        LocalTime lt = LocalTime.of(18, 9, 34);
        // 当前时刻按系统默认时区解读的时间
        LocalTime time = LocalTime.now();

        LocalDateTime ldt3 = date.atTime(18, 12, 23);
        LocalDateTime ldt4 = time.atDate(LocalDate.of(2022, 5, 4));

        // 获取系统默认时区的当前日期和时间
        ZonedDateTime zdt = ZonedDateTime.now();
        System.out.println("zdt = " + zdt);

        Instant instant = zdt.toInstant();

        java.time.format.DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.of(2022, 5, 4, 18, 37, 49);
        System.out.println("formatter.format(localDateTime) = " + formatter.format(localDateTime));

        String dateStr = "2022-05-01 18:37:49";
        LocalDateTime parse = LocalDateTime.parse(dateStr, formatter);
        System.out.println("parse = " + parse);

        // 调整时间为 15:20:01 2种方式
        LocalDateTime ldt1 = LocalDateTime.now();
        ldt1 = ldt1.withHour(15)
                .withMinute(20)
                .withSecond(1)
                .withNano(0);
        ldt1 = ldt1.toLocalDate().atTime(15, 20, 1);
        // 3小时5分后
        ldt1 = ldt1.plusHours(3).plusMinutes(5);

        // 今天 0点
        ldt1 = ldt1.with(ChronoField.MILLI_OF_DAY, 0);
        // LocalTime.MIN 表示 "00:00"
        ldt1 = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        ldt1 = LocalDate.now().atTime(0, 0);

        // 下周二 上午10点整
        ldt1 = ldt1.plusWeeks(1)
                .with(ChronoField.DAY_OF_WEEK, 2)
                .with(ChronoField.MILLI_OF_DAY, 0)
                .withHour(10);
        // 下一个周二 上午10点整（取决于当天是周几）
        LocalDate ld1 = LocalDate.now();
        if (!DayOfWeek.MONDAY.equals(ld1.getDayOfWeek())) {
            ld1 = ld1.plusWeeks(1);
        }
        ldt1 = ld1.with(ChronoField.DAY_OF_WEEK, 2)
                .atTime(10, 0);

        // 使用 TemporalAdjusters
        ldt1 = ld1.with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
                .atTime(10, 0);

        // 明天最后一刻
        ldt1 = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX);
        ldt1 = LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(LocalTime.MAX);
        // 本月最后一天最后一刻
        long maxDayOfMonth = LocalDate.now().range(ChronoField.DAY_OF_MONTH).getMaximum();
        ldt1 = LocalDate.now().withDayOfMonth((int) maxDayOfMonth)
                .atTime(LocalTime.MAX);
        // 下个月第一个周一的下午5点整
        ldt1 = LocalDate.now()
                .plusMonths(1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
                .atTime(17, 0);

        // 计算两个日期之间的差
        LocalDate localDate1 = LocalDate.of(2019, 1, 2);
        LocalDate localDate2 = LocalDate.of(2022, 5, 24);
        Period period = Period.between(localDate1, localDate2);
        String str = "间隔为 %d年%d月%d天".formatted(period.getYears(), period.getMonths(), period.getDays());
        System.out.println(str);

        // 根据生日计算年龄
        LocalDate born = LocalDate.of(2001, 8, 3);
        System.out.println(Period.between(born, LocalDate.now()).getYears());

        // 统计迟到的分钟数，9点上班
        long lateMinutes = Duration.between(LocalTime.of(9, 0), LocalTime.now()).toMinutes();
        System.out.println("lateMinutes = " + lateMinutes);

    }

    // Instant 和 Date 可以通过纪元时相互转换
    public static Instant toInstant(Date date) {
        return Instant.ofEpochMilli(date.getTime());
    }

    public static Date toDate(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    // 转换为北京时刻
    public static Instant toBeijingInstant(LocalDateTime ldt) {
        return ldt.toInstant(ZoneOffset.of("+08:00"));
    }

    // 将 LocalDateTime 按默认时区转换为 Date
    public static Date toDate(LocalDateTime ldt) {
        return new Date(
                ldt.atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
        );
    }

    // 将 Date 按默认时区抓换为 LocalDateTime
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault()
        );
    }

    // 将 ZonedDateTime 转换为 Calendar
    public static Calendar toCalendar(ZonedDateTime zdt) {
        TimeZone tz = TimeZone.getTimeZone(zdt.getZone());
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTimeInMillis(zdt.toInstant().toEpochMilli());
        return calendar;
    }

    // 将 Calendar 转换为 ZonedDateTime
    public static ZonedDateTime toZonedDateTime(Calendar calendar) {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(calendar.getTimeInMillis()),
                calendar.getTimeZone().toZoneId()
        );
    }
}
