package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.User;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserService {
    List<User> getAssignable(Long applicationId);
    Boolean isLeadApplicant(Long userId, Application application);
    public Set<User> getAssignableUsers(Application application);
    public Set<User> getApplicationUsers(Application application);
}
