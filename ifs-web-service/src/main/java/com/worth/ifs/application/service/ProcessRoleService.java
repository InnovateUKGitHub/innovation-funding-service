package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProcessRoleService {
    ProcessRole findProcessRole(Long userId, Long applicationId);
    List<ProcessRole> findAssignableProcessRoles(Long applicationId);
}
