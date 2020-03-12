package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * Testing the security annotations on the ApplicationCountSummaryService interface
 */
public class ApplicationCountSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationCountSummaryService> {

    @Test
    public void testGetApplicationCountSummariesByCompetitionId() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.getApplicationCountSummariesByCompetitionId(1L, 0, 0, empty());
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetApplicationCountSummariesByCompetitionId_notCompadmin() {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getApplicationCountSummariesByCompetitionId(1L, 0, 0, empty());
    }

    @Test
    public void testGetApplicationCountSummariesByCompetitionIdAndInnovationArea() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.getApplicationCountSummariesByCompetitionIdAndAssessorId(1L, 2L,0, 0, Sort.APPLICATION_NUMBER, "");
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetApplicationCountSummariesByCompetitionIdAndInnovationArea_notCompadmin() {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getApplicationCountSummariesByCompetitionIdAndAssessorId(1L, 2L,0, 0, Sort.APPLICATION_NUMBER, "");
    }

    @Override
    protected Class<? extends ApplicationCountSummaryService> getClassUnderTest() {
        return ApplicationCountSummaryServiceImpl.class;
    }
}
