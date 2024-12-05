package sunBrowser.exceptions;

public class AccessFailureException extends SunBrowserException {
    public AccessFailureException() {
        super();
    }

    public AccessFailureException(String message) {
        super(message);
    }

    public AccessFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessFailureException(Throwable cause) {
        super(cause);
    }
}
