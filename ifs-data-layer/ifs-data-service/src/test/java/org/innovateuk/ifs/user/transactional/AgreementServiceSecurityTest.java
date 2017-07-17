package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;

public class AgreementServiceSecurityTest extends BaseServiceSecurityTest<AgreementService> {

    @Override
    protected Class<? extends AgreementService> getClassUnderTest() {
        return TestAgreementService.class;
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

    public static class TestAgreementService implements AgreementService {

        @Override
        public ServiceResult<AgreementResource> getCurrent() {
            return null;
        }

    }
}
