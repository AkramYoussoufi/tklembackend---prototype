package com.project.tklembackend.filter;

import com.project.tklembackend.model.Roles;
import com.project.tklembackend.model.UserEntity;
import com.project.tklembackend.repository.UserEntityRepository;
import com.project.tklembackend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserEntityRepository userEntityRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Allow public endpoints (auth, socket, swagger)
        if (uri.contains("/api/auth/")
                || uri.contains("/socket")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // Retrieve Authorization header
        String authHeader = request.getHeader("Authorization");

        // If no Authorization header → skip authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token after "Bearer "
        String token = authHeader.substring(7);
        String extractedToken = jwtService.extractJWT(token);

        // Load user
        UserEntity user = userEntityRepository.findByEmail(extractedToken)
                .orElseThrow(() -> new NoSuchElementException("User not found for email: " + extractedToken));

        Roles userRole = user.getRole().getRoleName();

        // Authorization logic for admin-only endpoints
        if (uri.contains("admin") && !userRole.equals(Roles.ADMIN)) {
            throw new AuthorizationServiceException("This user is not authorized to perform this request");
        }

        // If user is enabled, authenticate
        if (user.isEnabled()) {
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new DisabledException("Account is disabled");
        }

        filterChain.doFilter(request, response);
    }
}
