package com.worth.ifs.user.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProcessRoleRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProcessRoleRepository> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Autowired
    protected void setRepository(ProcessRoleRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findByUserIdAndRoleAndApplicationId() {

        long userId = 2L;
        long applicationId = 1L;
        String roleName = UserRoleType.COLLABORATOR.getName();

        Role role = roleRepository.findByName(roleName).stream().findFirst().get();
        List<ProcessRole> processRoles = repository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId);

        assertEquals(1, processRoles.size());

        ProcessRole processRole = processRoles.stream().findFirst().get();
        assertEquals(roleName, processRole.getRole().getName());
        assertEquals(Long.valueOf(applicationId), processRole.getApplication().getId());
        assertEquals(Long.valueOf(userId), processRole.getUser().getId());
    }
}
