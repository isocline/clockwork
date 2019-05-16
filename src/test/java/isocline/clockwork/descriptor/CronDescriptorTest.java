package isocline.clockwork.descriptor;

import isocline.clockwork.Clock;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * *　　　　　　*　　　　　　*　　　　　　*　　　　　　*
 * 분(0-59)　　시간(0-23)　　일(1-31)　　월(1-12)　　　요일(0-7)
 */
public class CronDescriptorTest {


    @Test
    public void testMin() throws Exception {

        CronDescriptor descriptor = new CronDescriptor("* * * * *");

        CronDescriptor.CrontabChecker chk = descriptor.getChecker();


        boolean result = false;

        result = chk.check(Clock.toMilliSeconds("2019-05-14T11:59:59Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:30:00Z"));
        assertEquals(true, result);


        descriptor = new CronDescriptor("30 * * * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T11:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:40:00Z"));
        assertEquals(false, result);


        descriptor = new CronDescriptor("30,40,48 * * * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T11:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:40:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:48:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:49:00Z"));
        assertEquals(false, result);



        ///
        descriptor = new CronDescriptor("30,40,48-53 * * * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T11:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:40:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:53:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:54:00Z"));
        assertEquals(false, result);


        ///
        descriptor = new CronDescriptor("*/5,48-53 * * * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T11:15:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:20:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:56:00Z"));
        assertEquals(false, result);
    }


    @Test
    public void testHour() throws Exception {

        boolean result = false;


        CronDescriptor descriptor = new CronDescriptor("* 2 * * *");

        CronDescriptor.CrontabChecker chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T02:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T03:40:00Z"));
        assertEquals(false, result);




        ///
        descriptor = new CronDescriptor("* 3,4,8-10 * * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T03:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T04:40:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T10:53:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:54:00Z"));
        assertEquals(false, result);


        ///
        descriptor = new CronDescriptor("* */5,8-10 * * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T05:15:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T10:20:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T11:56:00Z"));
        assertEquals(false, result);
    }


    @Test
    public void testDay() throws Exception {

        boolean result = false;


        CronDescriptor descriptor = new CronDescriptor("* * 5 * *");

        CronDescriptor.CrontabChecker chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-05T02:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-06T03:40:00Z"));
        assertEquals(false, result);




        ///
        descriptor = new CronDescriptor("* * 3,4,8-10 * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-03-03T03:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-04-04T04:40:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-08-09T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-10-10T10:53:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-11-14T11:54:00Z"));
        assertEquals(false, result);


        ///
        descriptor = new CronDescriptor("* * */3,8-10 * *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-03-03T05:15:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-06-06T10:20:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-08-09T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-11-14T11:56:00Z"));
        assertEquals(false, result);
    }


    @Test
    public void testMonth() throws Exception {

        boolean result = false;


        CronDescriptor descriptor = new CronDescriptor("* * * 5 *");

        CronDescriptor.CrontabChecker chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-14T02:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-01-14T03:40:00Z"));
        assertEquals(false, result);




        ///
        descriptor = new CronDescriptor("* * * 3,4,8-10 *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-03-14T03:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-04-14T04:40:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-08-14T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-10-14T10:53:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-11-14T11:54:00Z"));
        assertEquals(false, result);


        ///
        descriptor = new CronDescriptor("* * * */3,8-10 *");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-03-14T05:15:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-06-14T10:20:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-08-14T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-11-14T11:56:00Z"));
        assertEquals(false, result);
    }


    @Test
    public void testWeek() throws Exception {

        boolean result = false;


        CronDescriptor descriptor = new CronDescriptor("* * * * 0");

        CronDescriptor.CrontabChecker chk = descriptor.getChecker();

        long x2 = Clock.toMilliSeconds("2019-05-05T02:30:00Z");
        long x = Clock.toMilliSeconds("2019-05-05T02:30:00Z");
        long x3 = Clock.toMilliSeconds("2019-05-05T02:30:00Z");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date(x));
        int min = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfMon = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        System.out.println("x ="+x);
        System.out.println("x2 ="+x2);
        System.out.println("min ="+min);
        System.out.println("hour ="+hour);
        System.out.println("dayOfMon ="+dayOfMon);
        System.out.println("month ="+month);
        System.out.println("dayOfWeek ="+dayOfWeek);

        System.out.println("min ="+min);System.out.println("min ="+min);



        result = chk.check(Clock.toMilliSeconds("2019-05-05T02:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-05-06T03:40:00Z"));
        assertEquals(false, result);




        ///
        descriptor = new CronDescriptor("* * * * 1-3");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-06T03:30:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-05-07T04:40:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-05-08T09:51:00Z"));
        assertEquals(true, result);


        result = chk.check(Clock.toMilliSeconds("2019-05-09T11:54:00Z"));
        assertEquals(false, result);


        ///
        descriptor = new CronDescriptor("* * * * 1,3,5");
        chk = descriptor.getChecker();


        result = chk.check(Clock.toMilliSeconds("2019-05-06T05:15:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-05-08T10:20:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-05-10T09:51:00Z"));
        assertEquals(true, result);

        result = chk.check(Clock.toMilliSeconds("2019-05-12T11:56:00Z"));
        assertEquals(false, result);
    }
}
