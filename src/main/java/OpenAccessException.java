/**
 * Represents an error from the OpenAccess service.
 */
public class OpenAccessException extends Exception {
    private String code;

    /**
     * Creates a new <code>OpenAccessException</code> instance with the given error code and
     * message.
     *
     * @param code the OpenAccess error code
     * @param message the OpenAccess error message
     */
    public OpenAccessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Gets the OpenAccess error code.
     *
     * @return the OpenAccess error code
     */
    public String getCode() { return code; }
}
