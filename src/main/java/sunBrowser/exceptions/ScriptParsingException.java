package sunBrowser.exceptions;

public class ScriptParsingException extends RuntimeException {
    public ScriptParsingException() {
        super();
    }

    public ScriptParsingException(String message) {
        super(message);
    }

    public ScriptParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptParsingException(Throwable cause) {
        super(cause);
    }
}