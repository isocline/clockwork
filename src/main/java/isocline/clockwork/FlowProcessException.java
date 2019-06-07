package isocline.clockwork;

public class FlowProcessException extends RuntimeException {


    public FlowProcessException() {
    }

    public FlowProcessException(String message) {
        super(message);
    }

    public FlowProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowProcessException(Throwable cause) {
        super(cause);
    }

    public FlowProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
