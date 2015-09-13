package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    List<User> getAssignable(Long applicationId);
}
