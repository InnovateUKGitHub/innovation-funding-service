package com.worth.ifs.controller;

import com.worth.ifs.domain.User;
import com.worth.ifs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wouter on 28/07/15.
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository repository;

    @RequestMapping("/token/{token}")
    public User getUserByToken(@PathVariable("token") final String token) {
        User user = repository.findByToken(token).get(0);
        return user;
    }

    @RequestMapping("/id/{id}")
    public User getUserById(@PathVariable("id") final Long id) {
        User user = repository.findById(id).get(0);
        return user;
    }

    @RequestMapping("/name/{name}")
    public List<User> getUserByName(@PathVariable("name") final String name) {
        List<User> users = repository.findByName(name);
        return users;
    }
}
