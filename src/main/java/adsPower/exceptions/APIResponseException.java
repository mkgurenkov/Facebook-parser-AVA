package adsPower.exceptions;

public class APIResponseException extends AdsPowerException {
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
