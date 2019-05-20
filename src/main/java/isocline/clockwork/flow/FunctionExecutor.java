package isocline.clockwork.flow;

import isocline.clockwork.WorkEvent;

import java.util.UUID;
import java.util.function.Consumer;


/**
 *
 *
 */
public class FunctionExecutor {


    private boolean isLastExecutor = false;

    private String fireEventName;

    private String recvEventName;

    private String eventUUID;


    private Runnable runnable;

    private Consumer cusumer;


    private long delayTimeFireEvent = 0;


    public FunctionExecutor(Object obj) {

        if (obj != null) {
            if (obj instanceof Runnable) {
                this.runnable = (Runnable) obj;
            } else if (obj instanceof Consumer) {
                this.cusumer = (Consumer) obj;
            } else {
                throw new IllegalArgumentException("Not Support type");
            }
        }


        this.eventUUID = UUID.randomUUID().toString();

        //System.out.println(this.eventUUID + "   regist ===== " + obj);
    }


    public String getEventUUID() {
        return this.eventUUID;
    }

    public void setLastExecutor(boolean isEnd) {
        this.isLastExecutor = isEnd;
    }

    public boolean isLastExecutor() {
        return isLastExecutor;
    }


    public void setFireEventName(String eventName) {
        this.fireEventName = eventName;
    }

    public void setRecvEventName(String eventName) {
        this.recvEventName = eventName;
    }

    public String getFireEventName() {
        return this.fireEventName;
    }

    public String getRecvEventName() {
        return this.recvEventName;
    }

    public long getDelayTimeFireEvent() {
        return delayTimeFireEvent;
    }

    public void setDelayTimeFireEvent(long delayTimeFireEvent) {
        this.delayTimeFireEvent = delayTimeFireEvent;
    }

    public void execute(WorkEvent event) {

        if (runnable != null) {

            runnable.run();
            return;
        }

        if (cusumer != null) {

            cusumer.accept(event);
        }
    }
}
