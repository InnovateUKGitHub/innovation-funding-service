package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * Testing the security annotations on the AssessorCountSummaryService interface
 */
public class AssessorCountSummaryServiceSecurityTest extends BaseServiceSecurityTest<AssessorCountSummaryService> {

    @Test
    public void testGetAssessorCountSummariesByCompetitionId() {
        setLoggedInUser(
                newUserResource()
                        .withRolesGlobal(
                                newRoleResource()
                                        .withType(UserRoleType.COMP_ADMIN)
                                        .build(1))
                        .build()
        );
        classUnderTest.getAssessorCountSummariesByCompetitionId(1L, 0, 0);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetAssessorCountSummariesByCompetitionId_notCompadmin() {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getAssessorCountSummariesByCompetitionId(1L, 0, 0);
    }

    @Override
    protected Class<? extends AssessorCountSummaryService> getClassUnderTest() {
        return TestAssessorCountSummaryService.class;
    }

    public static class TestAssessorCountSummaryService implements AssessorCountSummaryService {

        @Override
        public ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, int pageIndex, int pageSize) {
            return null;
        }
    }
}
