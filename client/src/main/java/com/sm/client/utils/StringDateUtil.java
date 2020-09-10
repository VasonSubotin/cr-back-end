package com.sm.client.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class StringDateUtil {
    public static final long DAY_IN_MILLS = 24L * 3600000L;

    public static Integer getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    final static String dateFormat = "yyyy-MM-dd'T'hh:mm:ss'Z'";

    public static Date parseDate(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        dateString = dateString.trim();
        if (dateString.length() <= "yyyy-MM-dd".length()) {
            SimpleDateFormat sdfT = new SimpleDateFormat(dateFormat.substring(0, dateString.length()));
            sdfT.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdfT.parse(dateString);
        }
        if (dateString.length() <= "yyyy-MM-ddThh:mm:ss".length()) {
            SimpleDateFormat sdfT = new SimpleDateFormat(dateFormat.substring(0, dateString.length() + 2));
            sdfT.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdfT.parse(dateString);
        }

        return new SimpleDateFormat(dateFormat).parse(dateString);
    }

    public static Date getBeginningOfDay() {
        return new Date(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static Date getEndOfDay() {
        return new Date(LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static Date setDateFrom(Date daysFromDate, Date daysToDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(daysFromDate.getTime());

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTimeInMillis(daysToDate.getTime());

        calendarTo.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        return new Date(calendarTo.getTimeInMillis());
    }

    public static Date setTimeFromMinutesOfDay(Date daysFromDate, long minutesFromTheBeginingOfDay) {
        return setDateFrom(daysFromDate, new Date(minutesFromTheBeginingOfDay * 60000-TimeZone.getDefault().getRawOffset()));
    }
}

