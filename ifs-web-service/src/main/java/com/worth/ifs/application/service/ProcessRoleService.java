package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.UserApplicationRole;
import org.springframework.stereotype.Service;

public interface ProcessRoleService {
    UserApplicationRole findUserApplicationRole(Long userId, Long applicationId);
}
