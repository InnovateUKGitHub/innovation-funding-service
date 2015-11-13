package com.worth.ifs.user.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dwatson on 05/10/15.
 */
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

        long userId = 3L;
        long applicationId = 4L;
        String roleName = UserRoleType.ASSESSOR.getName();

        Role role = roleRepository.findByName(roleName).stream().findFirst().get();
        List<ProcessRole> assessorProcessRoles = repository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId);

        assertEquals(1, assessorProcessRoles.size());

        ProcessRole assessorProcessRole = assessorProcessRoles.stream().findFirst().get();
        assertEquals(roleName, assessorProcessRole.getRole().getName());
        assertEquals(Long.valueOf(applicationId), assessorProcessRole.getApplication().getId());
        assertEquals(Long.valueOf(userId), assessorProcessRole.getUser().getId());
    }
}
