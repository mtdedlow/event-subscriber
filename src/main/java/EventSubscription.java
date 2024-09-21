/**
 * Represents an event subscription
 *
 * Note: currently only includes the parameters needed for requests
 */
public class EventSubscription {
    public String id;
    public String description;
    public String filter;
    public boolean is_durable;
}
