package adsPower.exceptions;

public class APIResponseException extends RuntimeException {
    public APIResponseException() {
        super();
    }

    public APIResponseException(String message) {
        super(message);
    }

    public APIResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIResponseException(Throwable cause) {
        super(cause);
    }
}
