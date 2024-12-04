package sunBrowser.exceptions;

public class SunBrowserException extends RuntimeException {
    public SunBrowserException() {
        super();
    }

    public SunBrowserException(String message) {
        super(message);
    }

    public SunBrowserException(String message, Throwable cause) {
        super(message, cause);
    }

    public SunBrowserException(Throwable cause) {
        super(cause);
    }
}
