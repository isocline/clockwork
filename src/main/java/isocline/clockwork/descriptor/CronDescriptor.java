package isocline.clockwork.descriptor;

import isocline.clockwork.Clock;
import isocline.clockwork.ExecuteChecker;
import isocline.clockwork.ScheduleDescriptor;
import isocline.clockwork.WorkSchedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CronDescriptor implements ScheduleDescriptor {

    private CrontabChecker checker;

    private String className;

    public CronDescriptor(String descript) throws IllegalArgumentException {

        this.checker = new CrontabChecker();

        className = this.checker.parse(descript);
    }


    @Override
    public void build(WorkSchedule workSchedule) {

        long t1 = Clock.nextMinutes();

        //System.err.println("--- "+Clock.toDateFormat(t1));

        workSchedule.setStartDateTime(t1);
        workSchedule.setRepeatInterval(Clock.MINUTE);
        workSchedule.setExecuteChecker(this.checker);


    }


    CrontabChecker getChecker() {
        return this.checker;
    }

    /**
     *
     */
    public static class CrontabChecker implements ExecuteChecker {
        private Checker minChk;

        private Checker hourChk;

        private Checker dayChk;

        private Checker monChk;

        private Checker weekChk;


        CrontabChecker() {

        }

        String parse(String text) throws IllegalArgumentException {
            String[] items = text.split(" ");

            if (items.length < 4) {
                throw new IllegalArgumentException("Invalid data");
            }
            this.minChk = new Checker(items[0], 0, 59);
            this.hourChk = new Checker(items[1], 0, 23);
            this.dayChk = new Checker(items[2], 1, 31);
            this.monChk = new Checker(items[3], 1, 12);
            this.weekChk = new Checker(items[4], 0, 7);

            if (items.length > 5) {
                return items[5];
            }

            return null;
        }

        public boolean check(long time) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new java.util.Date(time));
            int min = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int dayOfMon = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;


            //System.out.println( dayOfWeek + " "+weekChk.check(dayOfWeek));

            return weekChk.check(dayOfWeek) && monChk.check(month) && dayChk.check(dayOfMon) && hourChk.check(hour)
                    && minChk.check(min);

        }
    }


    private static class Checker {

        private Integer[] values = null;

        private int min = 0;

        private int max = 59;

        boolean check(int value) {
            if (values == null) {
                return true;
            }

            for (int val : values) {
                if (val == value) {
                    return true;
                }
            }

            return false;
        }

        Checker(String text, int min, int max) throws IllegalArgumentException {
            this.min = min;

            this.max = max;

            if ("*".equals(text)) {
                return;
            }

            List<Integer> list = new ArrayList<Integer>();

            String[] items = text.split(",");

            try {
                for (String item : items) {

                    int p = item.indexOf("-");
                    if (p > 0) {
                        String from = item.substring(0, p);
                        String to = item.substring(p + 1);

                        int intFrom = Integer.parseInt(from);
                        int intTo = Integer.parseInt(to);

                        for (int i = intFrom; i <= intTo; i++) {
                            list.add(i);
                        }
                        continue;

                    }


                    p = item.indexOf("/");
                    if (p > 0) {

                        String div = item.substring(p + 1);
                        int intDiv = Integer.parseInt(div);

                        for (int i = min; i < this.max; i++) {

                            if (i % intDiv == 0) {
                                list.add(i);
                            }

                        }

                        continue;
                    }

                    int num = Integer.parseInt(item);
                    list.add(num);

                }

            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("invaid data");
            }

            if (list.size() > 0) {
                values = list.toArray(new Integer[list.size()]);
            }

        }

    }


}
