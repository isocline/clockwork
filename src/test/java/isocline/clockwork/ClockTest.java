package isocline.clockwork;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;


public class ClockTest {

    private static Logger logger = Logger.getLogger(ClockTest.class.getName());


    @Before
    public void before() {

    }

    @After
    public void after() {

    }

    @Test
    public void testTime() throws Exception {

        long p = 2 * Clock.HOUR + 3 * Clock.MINUTE + 7 * Clock.SECOND;

        long q = Clock.milliseconds(2, 3, 7);

        assertEquals(p, q);

    }


    @Test
    public void testISOTime() throws Exception {

        String isoTime = "2029-05-14T11:59:59+09:00";
        Date d = Clock.toDate(isoTime);


        long p = Clock.milliseconds(isoTime);


        System.err.println(d);
        System.err.println(p);

        assertEquals(true, p > 0);

    }


    @Test
    public void nextSecond() throws Exception {

        System.out.println(Clock.toDateFormat(System.currentTimeMillis()));

        long t = Clock.nextSecond();

        System.out.println(Clock.toDateFormat(t));

    }


    @Test
    public void nextMinutes() throws Exception {

        String t1 = Clock.toDateFormat(System.currentTimeMillis());

        System.out.println(t1);

        long t = Clock.nextMinutes();

        String t2 = Clock.toDateFormat(t);

        System.out.println(t2);

        String[] items1 = t1.split(":");
        String[] items2 = t2.split(":");

        int it1 = Integer.parseInt(items1[1]);
        int it2 = Integer.parseInt(items2[1]);

        assertEquals(it1 + 1, it2);

        float f1 = Float.parseFloat(items2[2]);


        assertEquals(0.0, f1, 0);

    }


}
