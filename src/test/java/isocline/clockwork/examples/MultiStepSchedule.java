package isocline.clockwork.examples;

import isocline.clockwork.*;
import isocline.clockwork.event.WorkEventFactory;
import org.apache.log4j.Logger;


/**
 * This program is an indicator program which checks the water level periodically.
 * The water level grows up then Program check more often,
 * but it was a level down then check sometimes.
 */
public class MultiStepSchedule   {

    private static Logger logger = Logger.getLogger(MultiStepSchedule.class.getName());

    public static void main(String[] args) throws Exception {
        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        WorkSchedule schedule = processor.createSchedule(Step1Schedule.class).setStrictMode();
        schedule.activate();

        schedule = processor.createSchedule(Step2Schedule.class).bindEvent("fireEvent");
        schedule.activate();


        processor.shutdown(20 * Clock.SECOND);
    }

    public static class Step1Schedule implements Work {

        private int count = 0;

        @Override
        public long execute(WorkEvent event) throws InterruptedException {
            count++;

            logger.debug("count="+count);

            if (count > 5) {

                event.getWorkSchedule().getWorkProcessor().raiseEvent(WorkEventFactory.create("fireEvent"));

                return TERMINATE;
            } else {
                return 1 * Clock.SECOND;
            }

        }
    }

    public static class Step2Schedule implements Work {


        @Override
        public long execute(WorkEvent event) throws InterruptedException {

            logger.debug(event.getEventName() + " XX");

            if("fireEvent".equals(event.getEventName())) {
                return TERMINATE;
            }


            return 3*Clock.SECOND;


        }
    }
}
