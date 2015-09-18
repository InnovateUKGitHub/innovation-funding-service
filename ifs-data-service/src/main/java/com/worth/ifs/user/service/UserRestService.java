package com.worth.ifs.user.service;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import java.util.List;

/**
 * UserRestService is a utility to use client-side to retrieve User data from the data-service controllers.
 */

public interface UserRestService {
    public User retrieveUserByToken(String token);
    public User retrieveUserByEmailAndPassword(String email, String password);
    public User retrieveUserById(Long id);

    public List<User> findAll();
    public ProcessRole findProcessRole(Long userId, Long applicationId);
    public List<ProcessRole> findProcessRole(Long applicationId);
    public List<User> findAssignableUsers(Long applicationId);
    public List<ProcessRole> findAssignableProcessRoles(Long applicationId);
    public List<User> findRelatedUsers(Long applicationId);
}
