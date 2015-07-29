package com.worth.ifs.service;

import com.worth.ifs.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public User[] findAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity("http://localhost:8090/user/findAll/", User[].class);
        User[] users =responseEntity.getBody();

        return users;
    }
}
