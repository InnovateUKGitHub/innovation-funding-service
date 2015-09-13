package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRestService userRestService;

    @Override
    public List<User> getAssignable(Long applicationId) {
        return userRestService.findAssignableUsers(applicationId);
    }
}
