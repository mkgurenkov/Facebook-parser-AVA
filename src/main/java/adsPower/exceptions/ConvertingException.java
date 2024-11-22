package adsPower.exceptions;

public class ConvertingException extends RuntimeException {
    public ConvertingException() {
        super();
    }

    public ConvertingException(String message) {
        super(message);
    }

    public ConvertingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertingException(Throwable cause) {
        super(cause);
    }
}
