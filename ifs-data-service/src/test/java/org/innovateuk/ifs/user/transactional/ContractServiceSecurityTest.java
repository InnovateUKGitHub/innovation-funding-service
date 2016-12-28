package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Collections.singletonList;

public class ContractServiceSecurityTest extends BaseServiceSecurityTest<ContractService> {

    @Override
    protected Class<? extends ContractService> getClassUnderTest() {
        return TestContractService.class;
    }

    @Test
    public void getCurrent() throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(ASSESSOR).build())).build());
        classUnderTest.getCurrent();
    }

    @Test(expected = AccessDeniedException.class)
    public void getCurrent_notAnAssessor() throws Exception {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getCurrent();
    }

    public static class TestContractService implements ContractService {

        @Override
        public ServiceResult<ContractResource> getCurrent() {
            return null;
        }

    }
}
