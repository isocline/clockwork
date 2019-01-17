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
 * @author webplugger
 */
public class Clock {

    /**
     *
     *
     */
    public final static long AGAIN_RIGHT_WAY = 0;


    public final static long SLEEP = -1;

    /**
     * finish job
     */
    public final static long FINISH = -99;


    public final static long LOOP = 0;


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
     * @param isoDateTime
     * @return
     */
    public static long at(String isoDateTime) throws java.text.ParseException {


        Date date = getDate(isoDateTime);

        long gap = date.getTime() - System.currentTimeMillis()-1;

        if (gap < 1) {
            return FINISH;
        }

        return gap;


    }


    /**
     *
     *
     * @return
     */
    public static long nextSecond() {
        long s = System.currentTimeMillis();
        long next = (((long) s / 1000) * 1000) + 1000;

        long g= next-s;

        return g;
    }


    /**
     * @param isoDateTime
     * @return
     * @throws java.text.ParseException
     */
    public static Date getDate(String isoDateTime) throws java.text.ParseException {

        String isoDateTimeTxt = isoDateTime.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date;
        try {
            date = form.parse(isoDateTimeTxt);
        } catch (java.text.ParseException pe) {
            form = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
            date = form.parse(isoDateTimeTxt);
        }
        return date;

    }
}
