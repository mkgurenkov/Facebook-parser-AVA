package sunBrowser.exceptions;

public class AjaxException extends SunBrowserException {
    public AjaxException() {
        super();
    }

    public AjaxException(String message) {
        super(message);
    }

    public AjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public AjaxException(Throwable cause) {
        super(cause);
    }
}