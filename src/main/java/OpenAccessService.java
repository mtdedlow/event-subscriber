import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;  // Updated import
import com.google.api.client.util.Key;

import java.io.IOException;

/**
 * A wrapper for the OpenAccess service. Hides all the HTTP REST details.
 */
public class OpenAccessService {
    /**
     * Used to create OpenAccess requests.
     */
    private static class RequestInitializer implements HttpRequestInitializer {
        private HttpHeaders headers;

        public RequestInitializer(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public void initialize(HttpRequest request) {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
            request.setThrowExceptionOnExecuteError(false);
            request.setHeaders(headers);
        }
    }

    /**
     * Represents an OpenAccess add authentication request
     */
    public static class AddAuthenticationRequest {
        @Key("user_name")
        public String username;
        @Key
        public String password;
        @Key("directory_id")
        public String directoryId;
    }

    /**
     * Represents an OpenAccess add authentication response
     */
    public static class AddAuthenticationResponse {
        @Key("session_token")
        public String sessionToken;
        @Key("token_expiration_time")
        public String tokenExpiration;
    }

    /**
     * Represents an OpenAccess error
     */
    public static class Error {
        @Key
        public String code;
        @Key
        public String message;
    }

    /**
     * Represents an OpenAccess error response
     */
    public static class ErrorResponse {
        @Key
        public Error error;
    }

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();  // Updated factory

    private static final String AUTHENTICATION_RESOURCE = "authentication";
    private static final String API_VERSION_PATH = "?version=1.0";
    private static final String INTERNAL_DIRECTORY_ID = "id-1";

    private HttpRequestFactory requestFactory;

    private final String serviceUrl;
    private final String applicationId;
    private String sessionToken;
    private HttpHeaders requestHeaders;

    /**
     * Creates a new <code>OpenAccessService</code> instance.
     *
     * @param url the OpenAccess service URL
     * @param applicationId the application id to use with the OpenAccess service
     */
    public OpenAccessService(String url, String applicationId) {
        this.serviceUrl = url;
        this.applicationId = applicationId;
        requestHeaders = new HttpHeaders();
        requestHeaders.set("application-id", applicationId);

        requestFactory = HTTP_TRANSPORT.createRequestFactory(new RequestInitializer(requestHeaders));
    }

    /**
     * Login to OpenAccess, given the username and password for an internal user. All subsequent
     * requests will use the authenticated session token acquired from this call.
     *
     * @param username the username of an internal user
     * @param password the password of an internal user
     * @return An authenticated session token
     * @exception IOException if there was an error handling the response
     * @exception OpenAccessException if there was an OpenAccess error
     */
    public String login(String username, String password) throws IOException, OpenAccessException {
        return login(username, password, INTERNAL_DIRECTORY_ID);
    }

    /**
     * Login to OpenAccess, given user credentials. All subsequent requests will use the
     * authenticated session token acquired from this call.
     *
     * @param username the username of a user
     * @param password the password of a user
     * @param directoryId the authentication directory id of a user
     * @return An authenticated session token
     * @exception IOException if there was an error handling the response
     * @exception OpenAccessException if there was an OpenAccess error
     */
    public String login(String username, String password, String directoryId) throws IOException, OpenAccessException {
        AddAuthenticationRequest requestBody = new AddAuthenticationRequest();
        requestBody.username = username;
        requestBody.password = password;
        requestBody.directoryId = directoryId;
        JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, requestBody);

        HttpRequest request = requestFactory.buildPostRequest(createUrl(AUTHENTICATION_RESOURCE), content);
        HttpResponse response = request.execute();
        validateSuccessResponse(response);

        AddAuthenticationResponse successResponse = response.parseAs(AddAuthenticationResponse.class);
        sessionToken = successResponse.sessionToken;
        requestHeaders.set("session-token", sessionToken);

        return sessionToken;
    }

    /**
     * Logout of OpenAccess, invalidating and clearing the session token.
     *
     * @exception IOException if there was an error handling the response
     * @exception OpenAccessException if there was an OpenAccess error
     */
    public void logout() throws IOException, OpenAccessException {
        HttpRequest request = requestFactory.buildDeleteRequest(createUrl(AUTHENTICATION_RESOURCE));
        HttpResponse response = request.execute();
        validateSuccessResponse(response);

        sessionToken = null;
    }

    /**
     * Get the application id used to interact with the OpenAccess service.
     *
     * @return the application id
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Get the authenticated session token used for each request against the OpenAccess service.
     *
     * @return the authenticated session token
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Create a resource URL, given a path relative to the base URL.
     *
     * @param path the relative path
     * @return the resource URL
     */
    private GenericUrl createUrl(String path) {
        return new GenericUrl(serviceUrl + path + API_VERSION_PATH);
    }

    /**
     * Validates that an HTTP response was successful, throwing exceptions if not.
     * Note that the content of 401 errors cannot be displayed
     *
     * @param response the HTTP response to validate
     * @exception IOException if there was an error handling the response
     * @exception OpenAccessException if there was an OpenAccess error
     */
    private static void validateSuccessResponse(HttpResponse response) throws IOException, OpenAccessException {
        if (response.isSuccessStatusCode())
            return;

        if (response.getContent() == null)
            throw new HttpResponseException(response);

        ErrorResponse errorResponse = response.parseAs(ErrorResponse.class);
        throw new OpenAccessException(errorResponse.error.code, errorResponse.error.message);
    }
}

