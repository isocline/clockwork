# Clockwork
### Multi-purpose work processing engine for JVM and Android



[![Build Status](https://travis-ci.org/isocline/clockwork.svg?branch=develop)](https://travis-ci.org/isocline/clockwork)


**Clockwork** is a powerful integrated workflow engine that combines various workflow methods into one. 
In some cases, real-time processing may be required, but in some cases, 
it must be executed after some event processing. Or at a particular time, like a scheduler, Many things need to be done.
There are cases where other work processing methods are combined. 
For example, after checking the job every 10 minutes based on the scheduler 
If certain conditions are met, an event can be triggered to signal the start of another task Conversely, 
you can start a newly started scheduler after a specific event occurs.

It is a very inefficient task to select a library for each of these cases 
and code each time using a different API The Clockwork Work Processor can solve this problem very easily.

## Advantages

- **Optimized Dynamic Work Processor**: Clockwork is a versatile job execution tool that satisfies job execution conditions under any circumstances.
- **Self-control process**: Optimized for dynamic control environments such as various edge computing environments by dynamically changing its schedule status during job execution.
- **Elastic scheduler**:  Scheduling is similar to the Unix crontab setting method and provides various setting functions through extended API.
- **Accurate execution**: You can precisely adjust the execution in 1 ms increments aiming at the almost real-time level.
- **Easy coding**: Simple, easy to understand coding method, the code is straightforward.
- **Small footprint library**: Provides a tiny size library without compromising other libraries.
 
 
## Example

### Simple Repeater

Repeated tasks every 10 seconds

```java
import isocline.clockwork.*;

public class SimpleRepeater implements Work {


    public long execute(WorkEvent event) throws InterruptedException {

        // DO YOUR WORK

        return WAIT;
    }

    @Test
    public void startMethod() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        WorkSchedule schedule = processor.createSchedule(new SimpleRepeater())
            .setRepeatInterval(10 * Clock.SECOND)
            .activate();

        processor.shutdown(2000); // wait until 2000 milli seconds
    }

}
```
OR 
```java
import isocline.clockwork.*;

public class SimpleRepeater  {


    @Test
    public void startMethod() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();

        workProcessor.createSchedule((WorkEvent event) -> {
                     // DO YOUR WORK
                    return 10 * Clock.SECOND;
                }).activate();


        processor.shutdown(2000); // wait until 2000 milli seconds
    }

}
```

Real-time processing mode: Repeats exactly in milliseconds

```java

import isocline.clockwork.*;

public class PreciseRepeater implements Work {

    private static Logger logger = Logger.getLogger(PreciseRepeater.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("execute:" + seq++);

        return 10; // 10 milli seconds
    }

    @Test
    public void startMethod() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();

        WorkSchedule schedule = processor.createSchedule(new PreciseRepeater())
            .setStrictMode()
            .activate();

        processor.shutdown(2000); // wait until 2000 milli seconds
    }

}

```

##Output
<pre>
2019-06-16 16:00:00.000 DEBUG execute:0
2019-06-16 16:00:00.010 DEBUG execute:1
2019-06-16 16:00:00.020 DEBUG execute:2
2019-06-16 16:00:00.030 DEBUG execute:3
2019-06-16 16:00:00.040 DEBUG execute:4
2019-06-16 16:00:00.050 DEBUG execute:5
2019-06-16 16:00:00.060 DEBUG execute:6
2019-06-16 16:00:00.070 DEBUG execute:7

</pre>


### Scheduling

Repeated tasks every 1 hour

```java
import isocline.clockwork.*;

public class SimpleRepeater implements Work {


    public long execute(WorkEvent event) throws InterruptedException {

        // DO YOUR WORK

        return WAIT;
    }

    @Test
    public void startMethod() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        WorkSchedule schedule = processor.createSchedule(new ScheduledWork())
                        .setRepeatInterval(1 * Clock.HOUR)
                        .setStartDateTime("2020-04-24T09:00:00Z")
                        .setFinishDateTime("2020-06-16T16:00:00Z")
                        .activate();


        //processor.shutdown();
    }

}
```

Or crontab style

```java
import isocline.clockwork.*;
import isocline.clockwork.descriptor.CronDescriptor;

public class SimpleRepeater implements Work {


    public long execute(WorkEvent event) throws InterruptedException {

        // DO YOUR WORK

        return WAIT;
    }

    @Test
    public void startMethod() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();
        
        WorkSchedule schedule = processor
                        .createSchedule( new CronDescriptor("* 1,4-6 * * *"), new ScheduledWork())
                        .setStartDateTime("2020-04-24T09:00:00Z")
                        .setFinishDateTime("2020-06-16T16:00:00Z")
                        .activate();


        //processor.shutdown();
    }

}
```
### Execution by event




```java
import isocline.clockwork.*;

public class EventReceiver implements Work {


    public long execute(WorkEvent event) throws InterruptedException {

        // DO YOUR WORK

        return WAIT;
    }

    @Test
    public void startMethod() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();
        
        WorkSchedule schedule = processor.createSchedule(new EventReceiver())
                .bindEvent("example-event")
                .activate();
        
        
        // generate event
        processor.createSchedule((WorkEvent event) -> {
            
                        event.getWorkSchedule().getWorkProcessor()
                        .raiseEvent(event.createChild("example-event"));
                        
                        return 2 * Clock.SECOND; // every 2 seconds
                        
                        }).activate();


        //processor.shutdown();
    }

}
```

### Control flow

![alt tag](https://raw.github.com/isocline/clockwork/master/docs/img/sample_flow.png)
<br/><br/>
 
```java
import isocline.clockwork.*;

public class BasicWorkFlowTest implements FlowableWork {

    public void checkMemory() {
        log("check MEMORY");
    }

    public void checkStorage() {
        log("check STORAGE");
    }

    public void sendSignal() {
        log("send SIGNAL");
    }

    public void sendStatusMsg() {
        log("send STATUS MSG");
    }

    public void sendReportMsg() {
        log("send REPORT MSG");
    }

    public void report() {
        log("REPORT");
    }


    /**
     * control flow 
     *
    **/
    public void defineWorkFlow(WorkFlow flow) {

        WorkFlow p1 = flow.next(this::checkMemory).next(this::checkStorage);

        WorkFlow t1 = flow.wait(p1).next(this::sendSignal);
        WorkFlow t2 = flow.wait(p1).next(this::sendStatusMsg).next(this::sendReportMsg);

        flow.waitAll(t1, t2).next(this::report).finish();
    }


    @Test
    public void startMethod() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();
        processor.createSchedule(this).activate();       
        processor.awaitShutdown();

    }

}

```


 
