package com.app.security;

import com.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final Logger logger = Logger.getLogger("jwt-filter");
    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("request intercepted");
        if(request.getCookies() == null){
            logger.info("cookies are null");
            filterChain.doFilter(request, response);
            return;
        }
        Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("token")).findFirst();

        if(cookie.isEmpty()){
            logger.warning("cookie was not found");
            filterChain.doFilter(request, response);
            return;
        }

        // the cookie is present and we can access its value
        String jwt = cookie.get().getValue();
        // validated the token
        if(!jwtUtil.validate(jwt)){
            logger.warning("jwt is invalid");
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtUtil.getUsername(jwt);
        // the current authenticated user
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(username, null, List.of());
        userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // give the user to spring security context
        SecurityContextHolder.getContext().setAuthentication(userToken);
        logger.info("authenticated");
        filterChain.doFilter(request, response);



    }
}
