package com.worth.ifs.controller;

import com.worth.ifs.domain.User;
import com.worth.ifs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UserController exposes User data through a REST API.
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository repository;

    @RequestMapping("/token/{token}")
     public User getUserByToken(@PathVariable("token") final String token) {
        List<User> users = repository.findByToken(token);
        if (users.size() > 0){
            return users.get(0);
        }else{
            return null;
        }
    }

    @RequestMapping("/email/{email}/password/{password}")
    public User getUserByToken(@PathVariable("email") final String email, @PathVariable("password") final String password) {
        List<User> users = repository.findByEmailAndPassword(email, password);
        if (users.size() > 0){
            return users.get(0);
        }else{
            return null;
        }
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
    @RequestMapping("/findAll/")
    public List<User> findAll() {
        List<User> users = repository.findAll();
        return users;
    }
}
