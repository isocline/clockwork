package isocline.clockwork.object;

import java.util.function.Consumer;


/**
 *
 *
 */
public class FunctionExecutor {



    private boolean isAsync;

    private boolean isLastExecutor = false;

    private String fireEventName;

    private String recvEventName;



    private Runnable runnable;

    private Consumer cusumer;


    public FunctionExecutor(Runnable runnable, boolean isAsync) {
        this.runnable = runnable;
        this.isAsync = isAsync;
    }

    public FunctionExecutor(Consumer cusumer, boolean isAsync) {
        this.cusumer = cusumer;
        this.isAsync = isAsync;
    }

    public void setLastExecutor(boolean isEnd) {
        this.isLastExecutor = isEnd;
    }

    public boolean isLastExecutor() {
        return isLastExecutor;
    }

    public boolean isAsync() {
        return this.isAsync;
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

    public void execute() {

        if (runnable != null) {
            runnable.run();
        }

        if (cusumer != null) {
            cusumer.accept("");
        }
    }
}
