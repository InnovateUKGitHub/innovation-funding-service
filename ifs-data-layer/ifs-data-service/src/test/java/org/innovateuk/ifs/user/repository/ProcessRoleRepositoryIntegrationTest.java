package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ProcessRoleRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProcessRoleRepository> {

    @Override
    @Autowired
    protected void setRepository(ProcessRoleRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByUserIdAndRoleAndApplicationId() {

        long userId = 2L;
        long applicationId = 1L;

        Role role = Role.COLLABORATOR;
        ProcessRole processRole = repository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId);

        assertEquals(role, processRole.getRole());
        assertEquals(Long.valueOf(applicationId), processRole.getApplicationId());
        assertEquals(Long.valueOf(userId), processRole.getUser().getId());
    }
}
