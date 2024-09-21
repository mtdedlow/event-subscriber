import com.google.api.client.http.HttpTransport;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.Scanner;

public class Program {
    // OpenAccess connection details
    static final String OPENACCESS_URL = System.getenv("OPENACCESS_URL");
    static final String WEB_EVENT_BRIDGE_URL = OPENACCESS_URL + "eventbridge/";
    static final String OPENACCESS_APPLICATION_ID = System.getenv("OPENACCESS_APPLICATION_ID");
    static final String OPENACCESS_USERNAME = System.getenv("OPENACCESS_USERNAME");
    static final String OPENACCESS_PASSWORD = System.getenv("OPENACCESS_PASSWORD");
    static final String OPENACCESS_DIRECTORY_ID = "id-1";

    // Subscription details
    static final String SUBSCRIPTION_DESCRIPTION = "Java event gateway";
    static final String SUBSCRIPTION_FILTER = "business_event_class eq 'hardware_event'";
    static final boolean SUBSCRIPTION_IS_DURABLE = false;
    
    static Scanner inputScanner;
    
    public static void main(String[] args) throws Exception {
        // Uncomment to enable HTTP transport logging for the Google HTTP Client Library
        //enableLogging();
        
        inputScanner = new Scanner(System.in);
        try {
            System.out.format("Connecting to the OpenAccess service at %s with application id %s...%n",
                OPENACCESS_URL, OPENACCESS_APPLICATION_ID);

            OpenAccessService service = new OpenAccessService(OPENACCESS_URL, OPENACCESS_APPLICATION_ID);
            String sessionToken = service.login(OPENACCESS_USERNAME, OPENACCESS_PASSWORD, OPENACCESS_DIRECTORY_ID);

            System.out.println("Successfully connected to the OpenAccess service.");

            EventSubscription subscription = new EventSubscription();
            subscription.description = SUBSCRIPTION_DESCRIPTION;
            subscription.filter = SUBSCRIPTION_FILTER;
            subscription.is_durable = SUBSCRIPTION_IS_DURABLE;

            receiveEvents(WEB_EVENT_BRIDGE_URL, sessionToken, OPENACCESS_APPLICATION_ID, subscription);

            service.logout();
        }
        catch (OpenAccessException e) {
            System.out.format("Error communicating with the OpenAccess API: %s - %s%n", e.getCode(), e.getMessage());
            System.out.println("Press enter to exit...");
            inputScanner.nextLine();
        }
        catch (Exception e) {
            System.out.format("Error: %s%n", e);
            System.out.println("Press enter to exit...");
            inputScanner.nextLine();
        }
    }

    /**
     * Receive events until an ENTER key is input.
     *
     * @param bridgeUrl the URL of the web event bridge
     * @param sessionToken the authenticated session token to use with the web event bridge
     * @param applicationId the application id
     * @param subscription the details of the event subscription
     */
    public static void receiveEvents(String bridgeUrl, String sessionToken, String applicationId, EventSubscription subscription) throws Exception {
        WebEventSubscriber subscriber = null;
        try {
            StreamOutputEventHandler consoleHandler = new StreamOutputEventHandler(System.out);

            System.out.format("Connecting to the Web Event Bridge at %s...%n", bridgeUrl);
            subscriber = new WebEventSubscriber(bridgeUrl, sessionToken, applicationId, subscription, consoleHandler);

            subscriber.startReceiving();
            inputScanner.nextLine();
            subscriber.stopReceiving();
        }
        finally {
            if (subscriber != null)
                subscriber.close();
        }
    }

    /**
     * Log HTTP requests/responses from the Google HTTP Client Library.
     */
    public static void enableLogging() {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.CONFIG);
        logger.addHandler(new Handler() {
                @Override
                public void close() {}

                @Override
                public void flush() {}

                @Override
                public void publish(LogRecord record) {
                    System.out.println(record.getMessage());
                }
            });
    }
}
