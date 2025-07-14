    package org.example.config;

    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.example.service.JwtService;
    import org.example.service.UserService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

        @Autowired
        private JwtService jwtService;

        @Autowired
        private UserDetailsService userDetailsService;

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {
            
            logger.info("DEBUG: JWT Filter processing request: " + request.getMethod() + " " + request.getRequestURI());
            
            // Skip JWT filter for auth endpoints
            if (request.getRequestURI().startsWith("/api/auth/")) {
                logger.info("DEBUG: Skipping JWT filter for auth endpoint: " + request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.info("DEBUG: No Authorization header, continuing filter chain");
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    } 