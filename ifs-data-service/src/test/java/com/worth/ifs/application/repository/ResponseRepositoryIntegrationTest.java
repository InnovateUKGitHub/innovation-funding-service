package com.worth.ifs.application.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by dwatson on 05/10/15.
 */
public class ResponseRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ResponseRepository> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Autowired
    protected void setRepository(ResponseRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_getSingleResponse() {

        long userId = 3L;
        long applicationId = 4L;
        String roleName = UserRoleType.ASSESSOR.getName();

        Role role = roleRepository.findByName(roleName).stream().findFirst().get();
        Response response = repository.findOne(1L);

        assertEquals(Long.valueOf(1), response.getId());
    }
}
