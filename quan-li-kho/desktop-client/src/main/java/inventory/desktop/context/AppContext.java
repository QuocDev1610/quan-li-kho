package inventory.desktop.context;

import inventory.desktop.http.ApiClient;
import inventory.desktop.security.SessionManager;

public final class AppContext {
    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final ApiClient API_CLIENT = new ApiClient("http://localhost:8080", SESSION_MANAGER);

    private AppContext() {
    }

    public static ApiClient apiClient() {
        return API_CLIENT;
    }

    public static SessionManager sessionManager() {
        return SESSION_MANAGER;
    }
}
