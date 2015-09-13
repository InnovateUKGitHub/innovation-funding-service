package com.worth.ifs.user.service;

import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * UserRestService is a utility to use client-side to retrieve User data from the data-service controllers.
 */

public interface UserRestService {
    public User retrieveUserByToken(String token);
    public User retrieveUserByEmailAndPassword(String email, String password);
    public User retrieveUserById(Long id);

    public List<User> findAll();
    public UserApplicationRole findUserApplicationRole(Long userId, Long applicationId);
    public List<UserApplicationRole> findUserApplicationRole(Long applicationId);
    public List<User> findAssignableUsers(Long applicationId);
    public List<User> findRelatedUsers(Long applicationId);
}
