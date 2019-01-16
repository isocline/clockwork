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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
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
     *
     */
    public final static long FINISH = -99;




    public final static long LOOP = 0;


    /**
     * second
     *
     *
     */
    public final static long SECOND = 1000;


    /**
     * minute
     *
     */
    public final static long MINUTE = SECOND * 60;

    /**
     * hour
     *
     */
    public final static long HOUR = MINUTE * 60;


    /**
     *  day
     *
     *
     */
    public final static long DAY = HOUR * 24;



    /**
     *
     *
     * @param yyyyMMdd_hhmmss
     * @return
     */
    public static long at(String yyyyMMdd_hhmmss) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmmss");

        try {
            Date date = sdf.parse(yyyyMMdd_hhmmss);

            long gap = date.getTime() - System.currentTimeMillis();

            if (gap < 1) {
                return FINISH;
            }

            return gap;

        } catch (ParseException pe) {
            return FINISH;
        }


    }

}
