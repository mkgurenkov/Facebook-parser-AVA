package sunBrowser.exceptions;

public class FacebookAPIResponseException extends SunBrowserException {
    public FacebookAPIResponseException() {
        super();
    }

    public FacebookAPIResponseException(String message) {
        super(message);
    }

    public FacebookAPIResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FacebookAPIResponseException(Throwable cause) {
        super(cause);
    }
}

