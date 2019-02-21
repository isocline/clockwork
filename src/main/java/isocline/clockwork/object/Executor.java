package isocline.clockwork.object;

public class Executor {


    private Runnable runnable;

    private String fireEventName;

    private String recvEventName;

    private boolean isAsync;

    private boolean isEnd = false;

    public Executor(Runnable runnable, boolean isAsync) {
        this.runnable = runnable;
        this.isAsync = isAsync;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public boolean isEndExecutor() {
        return isEnd;
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
        if(runnable!=null) {
            runnable.run();
        }
    }
}
