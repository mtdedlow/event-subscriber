import java.util.Map;

/**
 * Defines the interface for an OpenAccess event handler.
 */
interface IEventHandler {
    /**
     * Called when an event is received from the bridge.
     *
     * @param businessEvent the event properties
     */
    public abstract void onBusinessEvent(Map<String, Object> businessEvent);

    /**
     * Called when an exception is received from the bridge.
     *
     * @param serviceException the exception message
     */
    public abstract void onExceptionRaised(String serviceException);

    /**
     * Called when a management event is received from the bridge.
     *
     * @param message the management message
     */
    public abstract void onManagementEvent(String message);

    /**
     * Called when the bridge establishes the connection to the message broker.
     */
    public abstract void onConnectionToMessageBusEstablished();

    /**
     * Called when the bridge loses the connection to the message broker.
     */
    public abstract void onConnectionToMessageBusLost();
}
