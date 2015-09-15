package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.stereotype.Service;

public interface ProcessRoleService {
    ProcessRole findProcessRole(Long userId, Long applicationId);
}
