package com.worth.ifs.service;

import com.worth.ifs.domain.User;
import com.worth.ifs.domain.UserApplicationRole;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * UserService is a utility to use client-side to retrieve User data from the data-service controllers.
 */

@Service
public class UserService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.user}")
    String userRestURL;

    @Value("${ifs.data.service.rest.userapplicationrole}")
    String userApplicationRoleRestURL;

    public User retrieveUserByToken(String token) {
        if(StringUtils.isEmpty(token))
            return null;

        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(dataRestServiceURL + userRestURL +"/token/"+token, User.class);
        return user;
    }

    public User retrieveUserByEmailAndPassword(String email, String password) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            return null;

        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(dataRestServiceURL + userRestURL + "/email/"+email+"/password/"+password, User.class);
        return user;
    }

    public User retrieveUserById(Long id) {
        if(id == null || id.equals(0L))
            return null;

        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(dataRestServiceURL + userRestURL + "/id/" + id, User.class);
        return user;
    }

    public List<User> findAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + userRestURL + "/findAll/", User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

    public UserApplicationRole findUserApplicationRole(Long userId, Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        UserApplicationRole userApplicationRole = restTemplate.getForObject(dataRestServiceURL + userApplicationRoleRestURL + "/findByUserApplication/" + userId + "/" + applicationId , UserApplicationRole.class);
        return userApplicationRole;
    }
}
