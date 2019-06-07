# Clockwork
### Multi purpose work processing engine for JVM and Android


[![Build Status](https://travis-ci.org/isocline/clockwork.svg?branch=develop)](https://travis-ci.org/isocline/clockwork)



**Clockwork** 는 다양한 작업처리 방식을 하나로 통합시킨 강력한 통합 작업 처리 엔진입니다 당신이 개발한 프로세스는
실시간 처리가 필요한 경우도 있도 어떤 이벤트 처리 이후에 실행이 되어야 하는 경우도 있습니다. 또는 스케쥴러 처럼 특정 시간에
실행되어야 하는 경우도 많이 있습니다
다른 작업 처리 방식이 서로 결합되는 경우도 존재합니다. 예를 들어 스케쥴러 기반으로 매 10분마다 작업 확인을 한 후 
특정 조건을 만족하면 다른 작업 수행 시작을 알리는 이벤트를 발생시킬 수 있습니다
반대로 특정 이벤트 발생 이후에 새롭게 시작되는 스케쥴러를 시작할 수 도 있습니다

이러한 각각의 경우에 맞는 라이브러리를 선택하여 매번 다른 API 를 이용하여 코딩하는 것은 매우 비효율적인 작업입니다
Clockwork Work Processor 는 이러한 문제를 매우 손쉽게 해결해 줄 수 있습니다

## Advantages

- **Optimized Dynamic Work Processor**: Clockwork는 어떠한 상황에서도 작업 실행 조건을 만족시키는 다재다능한 Job 실행도구 입니다.
- **Self control process**: 작업 실행중 자신의 스케쥴 상태를 동적으로 변경이 가능하여, 다양한 Edge computing 환경과 같은 dynimic control환경에 최적화 
- **기존 스케쥴러 대체**: Unix 의   crontab 설정 방식과 유사하게 스케쥴링이 가능하며 확장 API를 통해 다양한 설정 기능을 제공합니다
- **매우 정밀한 실행**: 1 ms단위로 실행을 정밀하게 조정할 수 있습니다 semi real time 수준을 지향합니다
- **간편한 코딩**: 매우 간결하고 이해하기 쉬윈 방식으로 코딩을 할 수 있으며, 코드가 매우 심플합니다
- **아주 작은 크기**: 다른 라이브러리 종석성없이 매우 작은 크기의 라이브러리를 제공합니다.

Download
--------

Download [https://github.com/isocline/mvn-repo/raw/master/isocline/clockwork/1.0/clockwork-1.0.jar] or depend via Maven:
```xml
<dependency>
  <groupId>isocline</groupId>
  <artifactId>clockwork</artifactId>
  <version>1.0</version>
</dependency>

<repositories>
    <repository>
       <id>isocline</id>
       <url>https://raw.github.com/isocline/mvn-repo</url>
    </repository>
</repositories>
```
 
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

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        processor.createSchedule((WorkEvent event) -> {
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

        logger.debug(activate + seq++);

        return 10; // 10 milli seconds
    }

    @Test
    public void startMethod() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

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
