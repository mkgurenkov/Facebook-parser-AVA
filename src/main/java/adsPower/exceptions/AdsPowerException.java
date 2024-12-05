package adsPower.exceptions;

public class AdsPowerException extends RuntimeException {
    public AdsPowerException() {
        super();
    }

    public AdsPowerException(String message) {
        super(message);
    }

    public AdsPowerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdsPowerException(Throwable cause) {
        super(cause);
    }
}

