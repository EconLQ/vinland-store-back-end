package com.vinland.store.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinland.store.security.auth.service.JwtService;
import com.vinland.store.utils.MessageResponse;
import com.vinland.store.web.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> accessTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> "accessToken".equals(cookie.getName()))
                    .findFirst();
            if (accessTokenCookie.isPresent()) {
                log.debug("Found access token cookie: {}", accessTokenCookie.get().getValue());
                String token = accessTokenCookie.get().getValue();
                try {
                    final String email = jwtService.extractEmail(token);
                    log.info("Extracted email from cookie: {}", email);

                    if (!email.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userService.loadUserByUsername(email);
                        if (jwtService.isTokenValid(token, userDetails)) {
                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            userDetails.getPassword(),
                                            userDetails.getAuthorities()
                                    );
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            context.setAuthentication(authenticationToken);
                            SecurityContextHolder.setContext(context);
                        }
                    }
                    filterChain.doFilter(request, response);
                } catch (ExpiredJwtException e) {
                    sendErrorResponse(response, new MessageResponse("Token has expired"), HttpStatus.UNAUTHORIZED);
                    log.warn(e.toString());
                } catch (JwtException e) {
                    sendErrorResponse(response, new MessageResponse("Invalid JWT token"), HttpStatus.FORBIDDEN);
                    log.warn(e.toString());
                }
            }
        }
        filterChain.doFilter(request, response);
    }


    private void sendErrorResponse(@NonNull HttpServletResponse response, MessageResponse message, HttpStatus status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(message));
        response.getWriter().flush();
        response.getWriter().close();

    }
}
