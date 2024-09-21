import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * An implementation of <code>IEventHandler</code> that writes to a stream.
 */
class StreamOutputEventHandler implements IEventHandler {
    private PrintStream out;
    
    /**
     * Creates a new <code>StreamOutputEventHandler</code> instance with the given output stream.
     *
     * @param out the output stream
     */
    StreamOutputEventHandler(OutputStream out) {
        this.out = new PrintStream(out);
    }
    
    @Override
    public void onBusinessEvent(Map<String, Object> businessEvent) {
        out.println("===========================================");
        for (String key : businessEvent.keySet())
        {
            Object value = businessEvent.get(key);
            out.format("%s: %s%n", key, propertyValueToString(value));
        }
    }
    
    @Override
    public void onExceptionRaised(String serviceException) {
        out.format("Error: %s%n", serviceException);
    }
    
    @Override
    public void onManagementEvent(String message) {
        out.format("ManagementEvent: %s%n", message);
    }
    
    @Override
    public void onConnectionToMessageBusEstablished() {
        out.println("Connection to message bus established.");
    }
    
    @Override
    public void onConnectionToMessageBusLost() {
        out.println("Connection to message bus lost.");
    }

    /**
     * Converts an even property value to an appropriate string representation.
     *
     * @param value the event property value
     * @return the string representation of the event property value
     */
    private static String propertyValueToString(Object value) {
        if (value == null)
            return "null";
        else if (value instanceof Number) {
            Number numberValue = (Number)value;
            return canBeRepresentedAsLong(numberValue) ?
                String.valueOf(numberValue.longValue()) :
                String.valueOf(numberValue.doubleValue());
        }
        else
            return value.toString();
    }

    /**
     * Determines if a <code>Number</code> can be represented as a <code>Long</code>.
     *
     * @param value a <code>Number</code> value
     * @return true if the given <code>Number</code> can be represented as a <code>Long</code>
     */
    private static boolean canBeRepresentedAsLong(Number value) {
        return value.doubleValue() == value.longValue();
    }
}
