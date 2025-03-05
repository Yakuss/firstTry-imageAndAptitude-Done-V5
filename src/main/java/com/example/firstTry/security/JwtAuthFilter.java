package com.example.firstTry.security;

import com.example.firstTry.model.User;
import com.example.firstTry.security.jwt.JwtService;
import com.example.firstTry.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Special case: WebSocket requests may not have the Authorization header
//        if (email == null && request.getParameter("token") != null) {
//            token = request.getParameter("token");
//            email = jwtService.extractUsername(token);
//        }

        String token = authHeader.substring(7);
        try {
            String email = jwtService.extractUsername(token);
            //User user = userService.loadUserByUsername(email);
                    //.orElseThrow(() -> new UsernameNotFoundException("User not found"));
            UserDetails userDetails = userService.loadUserByUsername(email);
            User user = (User) userDetails; // Explicit cast

            if (jwtService.isTokenValid(token, user) && user.isEnabled()) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or disabled account");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
