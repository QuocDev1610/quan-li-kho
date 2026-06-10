package inventory.security;

import inventory.dao.entity.User;
import inventory.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ApiPermissionService apiPermissionService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserService userService, ApiPermissionService apiPermissionService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.apiPermissionService = apiPermissionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String userName = jwtTokenProvider.getUserName(token);
            List<User> users = userService.FindByProperty("userName", userName);
            if (users != null && !users.isEmpty() && Integer.valueOf(1).equals(users.get(0).getActiveFlag())) {
                String role = jwtTokenProvider.getRole(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        users.get(0),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + (role == null ? "USER" : role)))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                if (!apiPermissionService.hasPermission(userName, request.getServletPath())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền thực hiện chức năng này.\",\"data\":null}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
