package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * Testing the security annotations on the ApplicationCountSummaryService interface
 */
public class ApplicationCountSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationCountSummaryService> {

    @Test
    public void testGetApplicationCountSummariesByCompetitionId() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build());
        classUnderTest.getApplicationCountSummariesByCompetitionId(1L, 0, 0, empty());
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetApplicationCountSummariesByCompetitionId_notCompadmin() {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getApplicationCountSummariesByCompetitionId(1L, 0, 0, empty());
    }

    @Test
    public void testGetApplicationCountSummariesByCompetitionIdAndInnovationArea() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build());
        classUnderTest.getApplicationCountSummariesByCompetitionIdAndInnovationArea(1L, 0, 0, empty(), "");
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetApplicationCountSummariesByCompetitionIdAndInnovationArea_notCompadmin() {
        setLoggedInUser(newUserResource().build());
        classUnderTest.getApplicationCountSummariesByCompetitionIdAndInnovationArea(1L, 0, 0, empty(), "");
    }


    @Override
    protected Class<? extends ApplicationCountSummaryService> getClassUnderTest() {
        return TestApplicationCountSummaryService.class;
    }

    public static class TestApplicationCountSummaryService implements ApplicationCountSummaryService {

        @Override
        public ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(Long competitionId, int pageIndex, int pageSize, Optional<String> filter) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndInnovationArea(Long competitionId, int pageIndex, int pageSize, Optional<Long> innovationArea, String sortField) {
            return null;
        }
    }
}
