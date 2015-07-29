package com.worth.ifs.service;

import com.worth.ifs.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wouter on 28/07/15.
 */
@Service
public class UserService {
    public User retrieveUserByToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject("http://localhost:8090/user/token/"+token, User.class);
        return user;
    }

    public User retrieveUserById(Integer id) {
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject("http://localhost:8090/user/id/"+id, User.class);
        return user;
    }

    public List<User> getAll() {
        RestTemplate restTemplate = new RestTemplate();
        //User user = restTemplate.getForObject("http://localhost:8090/user/", User.class);
        User[] forNow = restTemplate.getForObject("http://localhost:8090/user/", User[].class);
        List<User> users = Arrays.asList(forNow);
        return users;
    }
}
