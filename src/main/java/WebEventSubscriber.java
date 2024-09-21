import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import java.util.Map;
import microsoft.aspnet.signalr.client.transport.LongPollingTransport;
import java.io.Closeable;

/**
 * A wrapper for the OpenAccess event bridge. Supports subscribing for and receiving events.
 */
public class WebEventSubscriber implements Closeable {
    /**
     * Represents the connection info passed to the event bridge.
     */
    private class ConnectionInfo {
        public ConnectionInfo(String sessionToken, String applicationId) {
            this.SessionToken = sessionToken;
            this.ApplicationId = applicationId;
        }

        public String SessionToken;
        public String ApplicationId;
    }

    private String bridgeUrl;
    private HubConnection connection;
    private HubProxy proxy;
    private ConnectionInfo connectionInfo;
    private EventSubscription subscription;
    private IEventHandler handler;

    /**
     * Creates a new <code>WebEventSubscriber</code> instance.
     *
     * @param url the OpenAccess event bridge URL
     * @param sessionToken an authenticated OpenAccess session token
     * @param applicationId the OpenAccess application id
     * @param subscription the subscription details
     * @param handler the event handler that will process events from the bridge
     */
    public WebEventSubscriber(String url, String sessionToken, String applicationId, EventSubscription subscription, IEventHandler handler) {
        this.bridgeUrl = url;
        connectionInfo = new ConnectionInfo(sessionToken, applicationId);
        this.subscription = subscription;
        this.handler = handler;
    }

    /**
     * Gets the event bridge proxy.
     *
     * @return The event bridge proxy
     */
    public HubProxy getEventBridgeProxy() throws Exception {
        if (proxy == null) {
            connection = createHubConnectionWithoutLogging();

            proxy = connection.createHubProxy("Outbound");

            proxy.subscribe(new Object() {
                @SuppressWarnings("unused")
                public void OnBusinessEventReceived(Map<String, Object> businessEvent) {
                    handler.onBusinessEvent(businessEvent);
                }

                public void OnExceptionRaised(String serviceException) {
                    handler.onExceptionRaised(serviceException);
                }

                public void OnManagementEvent(String message) {
                    handler.onManagementEvent(message);
                }

                public void OnConnectionToMessageBusEstablished() {
                    handler.onConnectionToMessageBusEstablished();
                }

                public void OnConnectionToMessageBusLost() {
                    handler.onConnectionToMessageBusLost();
                }
            });

            // Start the connection
            connection.start(new LongPollingTransport(connection.getLogger())).get();
        }

        return proxy;
    }

    /**
     * Starts receiving events from the event bridge.
     */
    public void startReceiving() throws Exception {
        Object result = getEventBridgeProxy().invoke("CreateSubscription", connectionInfo, subscription).get();
    }

    /**
     * Stops receiving events from the event bridge.
     */
    public void stopReceiving() throws Exception {
        getEventBridgeProxy().invoke("StopSubscription").get();
        close();
    }

    /**
     * Closes the event bridge connection.
     */
    @Override
    public void close() {
        if (connection != null) {
            connection.stop();
            connection = null;
            proxy = null;
        }
    }

    /**
     * Creates a <code>HubConnection</code> without logging enabled.
     *
     * @return a new <code>HubConnection</code> for the event bridge
     */
    private HubConnection createHubConnectionWithoutLogging() {
        return new HubConnection(bridgeUrl, false);
    }

    /**
     * Creates a <code>HubConnection</code> with logging enabled.
     *
     * @return a new <code>HubConnection</code> for the event bridge
     */
    private HubConnection createHubConnectionWithLogging() {
        Logger logger = (message, level) -> System.out.println(message);
        return new HubConnection(bridgeUrl, null, false, logger);
    }
}
