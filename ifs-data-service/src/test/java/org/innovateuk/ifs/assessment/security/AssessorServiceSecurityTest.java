package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_EXEC;

public class AssessorServiceSecurityTest extends BaseServiceSecurityTest<AssessorService> {

    @Override
    protected Class<? extends AssessorService> getClassUnderTest() {
        return TestAssessorService.class;
    }

    @Test
    public void getAssessorProfile() {
        Long assessorId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAssessorProfile(assessorId), COMP_ADMIN, COMP_EXEC);
    }

    public static class TestAssessorService implements AssessorService {
        @Override
        public ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource) {
            return null;
        }

        @Override
        public ServiceResult<AssessorProfileResource> getAssessorProfile(Long assessorId) {
            return null;
        }
    }
}
