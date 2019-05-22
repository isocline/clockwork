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

- **Optimized Dynamic Work Proccessor**: Clockwork는 어떠한 상황에서도 작업 실행 조건을 만족시키는 다재다능한 Job 실행도구 입니다.
- **Self control process**: 작업 실행중 자신의 스케쥴 상태를 동적으로 변경이 가능하여, 다양한 Edge computing 환경과 같은 dynimic control환경에 최적화 
- **간편한 코딩**: 매우 간결하고 이해하기 쉬윈 방식으로 코딩을 할 수 있으며, 코드가 매우 심플합니다
- **다양한 확장성**: 기존 crontab 스타일의 스케쥴링 정의나 json, xml등 다양한 형태의 설정 방식을 지원합니다. 사용자에 따라 원하는 형태로 확장이 가능합니다
- **매우 정밀한 실행**: 1 ms단위로 실행을 정밀하게 조정할 수 있습니다 semi real time 수준을 지양합니다
- **기존 스케쥴러 대체**: Unix 의   crontab 설정 방식과 유사하게 스케쥴링이 가능하며 확장 API를 통해 다양한 설정 기능을 제공합니다
- **아주 작은 크기**: 다른 라이브러리 종석성없이 매우 작은 크기의 라이브러리를 제공합니다.

 
 
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
 