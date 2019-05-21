# Clockwork
### Multi purpose work processing engine for JVM and Android


[![Build Status](https://travis-ci.org/isocline/clockwork.svg?branch=develop)](https://travis-ci.org/isocline/clockwork)


**Clockwork** is a powerful integrated workflow engine that combines various workflow methods into one.
In some cases, real-time processing may be required, but in some cases it must be executed after some event processing. Or at a certain time, like a scheduler
There are many things that need to be done
There are cases where other work processing methods are combined with each other. For example, after checking the job every 10 minutes based on the scheduler
If certain conditions are met, an event can be triggered to signal the start of another task
Conversely, you can start a newly started scheduler after a specific event occurs

It is a very inefficient task to select a library for each of these cases and code each time using a different API
The Clockwork Work Processor can solve this problem very easily

## Advantages

- **Optimized Dynamic Work Proccessor**: Clockwork is a versatile job execution tool that satisfies job execution conditions under any circumstances.
- **Self control process**: Optimized for dynimic control environments such as various edge computing environments by dynamically changing its schedule status during job execution.
- **Easy coding**: Very simple, easy to understand coding method, code is very simple
- **Extensive Scalability**: Supports various crontab scheduling definitions, json, xml, and more. It can be extended to any type according to the user
- **Extremely precise execution**: precise execution can be adjusted in 1 ms steps to avoid semi real time level
- **Replace existing scheduler**: Scheduling is similar to Unix crontab setting method and provides various setting functions through extended API
- **Small footprint library**: Provides a very small library size without compromising other libraries
 
 
## Example

**Work flow**

![alt tag](https://raw.github.com/isocline/clockwork/master/docs/img/sample_flow.png)
<br/><br/>
 
```java

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
     * design work flow
     *
    **/
    public void defineWorkFlow(WorkFlow flow) {

        WorkFlow p1 = flow.run(this::checkMemory).next(this::checkStorage);

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
 
