package com.app.security;

import com.app.user.User;
import com.app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserFinder implements UserDetailsService {

    private Logger logger = Logger.getLogger("user-finder");
    @Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username).orElseThrow(()->{
            String message = "User with username %s was not found".formatted(username);
            logger.warning(message);
           throw  new UsernameNotFoundException(message);
        });
        logger.info("User found");
        org.springframework.security.core.userdetails.User u = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), List.of());
        return u;
    }
}
