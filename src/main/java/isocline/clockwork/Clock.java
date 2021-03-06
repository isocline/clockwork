/*
 * Copyright 2018 The Isocline Project
 *
 * The Isocline Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package isocline.clockwork;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility Class with time setting related functions
 *
 * @author Richard Dean Kim
 * @see WorkSchedule
 */
public class Clock {

    /**
     * right now
     */
    public final static long AGAIN_RIGHT_WAY = 0;


    /**
     * second
     */
    public final static long SECOND = 1000;


    /**
     * minute
     */
    public final static long MINUTE = SECOND * 60;

    /**
     * hour
     */
    public final static long HOUR = MINUTE * 60;


    /**
     * day
     */
    public final static long DAY = HOUR * 24;


    /**
     * Obtains a time to the current system millisecond time using ISO 8601 in the specified time zone.
     *
     * @param isoDateTime this date-time as a String, such as 2019-06-16T10:15:30Z or 2019-06-16T10:15:30+01:00[Europe/Paris].
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     * @throws java.text.ParseException If isoDateTime format is invalid
     */
    public static long milliseconds(String isoDateTime) throws java.text.ParseException {


        Date date = toDate(isoDateTime);

        long gap = date.getTime() - System.currentTimeMillis() - 1;

        if (gap < 1) {
            return Work.TERMINATE;
        }

        return gap;


    }


    /**
     * Obtains a millisecond
     *
     * @param hour hour
     * @param minute minute
     * @param second second
     * @return the number of milliseconds.
     */
    public static long milliseconds(long hour, long minute, long second) {
        return hour * HOUR + minute * MINUTE + second * SECOND;
    }


    /**
     * Returns a fireEvent second from now
     *
     * @param maximumWait the number of milliseconds for waiting
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     */
    public static long nextSecond(long maximumWait) {
        long s = System.currentTimeMillis();
        long s1 = s % SECOND;

        if (s1 > maximumWait) {
            return (s - s1) + SECOND * 2;
        } else {
            return (s - s1) + SECOND;
        }
    }


    /**
     * Returns the exact next 1 second unit time from the current time.
     *
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     */
    public static long nextSecond() {
        return nextSecond(990);
    }


    /**
     * Returns the exact next 1-minute unit time from the current time.
     *
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     */
    public static long nextMinutes() {


        long s = System.currentTimeMillis();
        long s1 = s % MINUTE;

        if (s1 > (MINUTE-20)) {
            return (s - s1) + MINUTE * 2;
        } else {
            return (s - s1) + MINUTE;
        }
    }

    /**
     * Obtains a DateTime set to the current system millisecond time using ISO 8601 in the specified time zone.
     *
     * @param isoDateTime this date-time as a String, such as 2019-06-16T10:15:30Z or 2019-06-16T10:15:30+01:00[Europe/Paris].
     * @return Date java.util.Date
     * @throws java.text.ParseException If input datetime format is error
     */
    public static Date toDate(String isoDateTime) throws java.text.ParseException {

        final String isoDateTimeTxt = isoDateTime.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");

        SimpleDateFormat form;

        if(isoDateTime.length()==25) {
            form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        }else {
            form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }


        Date date;
        try {
            date = form.parse(isoDateTimeTxt);
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
            form = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
            date = form.parse(isoDateTimeTxt);
        }
        return date;

    }

    /**
     * Convert a formatted string to the numeric type time information.
     *
     * @param isoDateTime ISO 8601 style datetime,this date-time as a String, such as 2019-06-16T10:15:30Z or 2019-06-16T10:15:30+01:00[Europe/Paris].
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     * @throws java.text.ParseException If input data format is error
     */
    public static long toMilliSeconds(String isoDateTime) throws java.text.ParseException {
        return toDate(isoDateTime).getTime();
    }


    /**
     * Convert the numeric type time information to a formatted string. [yyyy-MM-dd HH:mm:ss.SSS]
     *
     * @param time the milliseconds since January 1, 1970, 00:00:00 GMT.
     * @return datetime text format[yyyy-MM-dd HH:mm:ss.SSS]
     */
    public static String toDateFormat(long time) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date(time);

        return dt.format(date);
    }
}
