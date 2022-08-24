package com.app.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getUsers(){
        return repository.findAll();
    }

    public User getProfile(String username){
        return repository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User was not found"));
    }
}
