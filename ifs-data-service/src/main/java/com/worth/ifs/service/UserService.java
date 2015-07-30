package com.worth.ifs.service;

import com.worth.ifs.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wouter on 28/07/15.
 */
@Service
public class UserService extends BaseServiceProvider {

    public User retrieveUserByToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(dataRestServiceURL + "/user/token/"+token, User.class);
        return user;
    }

    public User retrieveUserById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(dataRestServiceURL + "/user/id/"+id, User.class);
        return user;
    }

    public List<User> findAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + "/user/findAll/", User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

}
