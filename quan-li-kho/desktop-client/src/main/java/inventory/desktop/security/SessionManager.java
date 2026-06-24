package inventory.desktop.security;

import java.util.Arrays;
import java.util.Optional;

/**
 * Giữ JWT trong bộ nhớ RAM của phiên đăng nhập hiện tại.
 * Không ghi token ra file để giảm rủi ro lộ thông tin đăng nhập.
 */
public final class SessionManager {
    private char[] accessToken;
    private String userName;

    public synchronized void startSession(String token, String userName) {
        clear();
        this.accessToken = token == null ? null : token.toCharArray();
        this.userName = userName;
    }

    public synchronized Optional<String> authorizationHeader() {
        if (accessToken == null || accessToken.length == 0) {
            return Optional.empty();
        }
        return Optional.of("Bearer " + new String(accessToken));
    }

    public synchronized Optional<String> getUserName() {
        return Optional.ofNullable(userName);
    }

    public synchronized boolean isLoggedIn() {
        return accessToken != null && accessToken.length > 0;
    }

    public synchronized void clear() {
        if (accessToken != null) {
            Arrays.fill(accessToken, '\0');
        }
        accessToken = null;
        userName = null;
    }
}
