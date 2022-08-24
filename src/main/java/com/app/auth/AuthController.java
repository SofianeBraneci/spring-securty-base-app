package com.app.auth;

import com.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final Logger logger = Logger.getLogger("auth-controller");
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody  AuthRequest request){
        logger.info("Received a login request from the user  %s ".formatted(request.getUsername()));
        Objects.requireNonNull(request, "Auth request must not be null");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        manager.authenticate(token);
        String jwt = jwtUtil.generate(request.getUsername());

        ResponseCookie jwtCookie = ResponseCookie.from("token", jwt)
                                                .httpOnly(true)
                                                .path("/")
                                                .domain("localhost")
                                                .maxAge(Duration.ofMinutes(30))
                                                .build();
        logger.info("User %s authenticated".formatted(request.getUsername()));
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
    }

    public ResponseEntity logout(@CookieValue(name = "token") String token){
        // remove the user from the security context
        SecurityContextHolder.getContext().setAuthentication(null);
        ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .domain("localhost")
                .maxAge(0)
                .build();

        logger.info("a user logged out");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();

    }
}
