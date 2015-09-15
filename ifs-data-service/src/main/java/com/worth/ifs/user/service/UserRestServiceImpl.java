package com.worth.ifs.user.service;

import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UserRestServiceImpl extends BaseRestServiceProvider implements UserRestService {
    @Value("${ifs.data.service.rest.user}")
    String userRestURL;

    @Value("${ifs.data.service.rest.processrole}")
    String processRoleRestURL;

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

    public ProcessRole findProcessRole(Long userId, Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ProcessRole processRole = restTemplate.getForObject(dataRestServiceURL + processRoleRestURL + "/findByUserApplication/" + userId + "/" + applicationId, ProcessRole.class);
        return processRole;
    }
    public List<ProcessRole> findProcessRole(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ProcessRole[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + processRoleRestURL + "/findByUserApplication/"+ applicationId, ProcessRole[].class);
        ProcessRole[] processRole = responseEntity.getBody();
        return Arrays.asList(processRole);
    }
    public List<User> findAssignableUsers(Long applicationId){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + userRestURL + "/findAssignableUsers/"+applicationId, User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }
    public List<User> findRelatedUsers(Long applicationId){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + userRestURL + "/findRelatedUsers/"+applicationId, User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }
}
