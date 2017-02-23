package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * Testing the security annotations on the ApplicationCountSummaryService interface
 */
public class ApplicationCountSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationCountSummaryService> {

    @Test
    public void testGetApplicationCountSummariesByCompetitionId() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build());
        classUnderTest.getApplicationCountSummariesByCompetitionId(1L);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetApplicationCountSummariesByCompetitionId_notCompadmin() {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getApplicationCountSummariesByCompetitionId(1L);
    }

    @Override
    protected Class<? extends ApplicationCountSummaryService> getClassUnderTest() {
        return TestApplicationCountSummaryService.class;
    }

    public static class TestApplicationCountSummaryService implements ApplicationCountSummaryService {

        @Override
        public ServiceResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(Long competitionId) {
            return null;
        }
    }
}
