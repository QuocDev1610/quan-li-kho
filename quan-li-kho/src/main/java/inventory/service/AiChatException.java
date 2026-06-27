package inventory.service;

public class AiChatException extends RuntimeException {
    private final int httpStatus;
    private final boolean retryable;

    public AiChatException(int httpStatus, String message) {
        this(httpStatus, message, false);
    }

    public AiChatException(int httpStatus, String message, boolean retryable) {
        super(message);
        this.httpStatus = httpStatus;
        this.retryable = retryable;
    }

    public AiChatException(int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.retryable = false;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
