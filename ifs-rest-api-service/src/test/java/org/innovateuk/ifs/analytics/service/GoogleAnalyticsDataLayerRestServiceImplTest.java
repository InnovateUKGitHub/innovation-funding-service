package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.roleListType;
import static org.junit.Assert.assertEquals;

public class GoogleAnalyticsDataLayerRestServiceImplTest extends BaseRestServiceUnitTest<GoogleAnalyticsDataLayerRestServiceImpl> {

    private static final String restUrl = "/analytics";

    @Override
    protected GoogleAnalyticsDataLayerRestServiceImpl registerRestServiceUnderTest() {
        return new GoogleAnalyticsDataLayerRestServiceImpl();
    }

    @Test
    public void getCompetitionNameForApplication() {
        long applicationId = 5L;
        String expected = "competition name";

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/competition-name", restUrl, "application", applicationId),
                String.class,
                expected
        );

        String actual = service.getCompetitionNameForApplication(applicationId).getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionName() {
        long competitionId = 7L;
        String expected = "competition name";

        setupGetWithRestResultAnonymousExpectations(
                format("%s/%s/%d/competition-name", restUrl, "competition", competitionId),
                String.class,
                expected
        );

        String actual = service.getCompetitionName(competitionId).getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionNameForProject() {
        long projectId = 11L;
        String expected = "competition name";

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/competition-name", restUrl, "project", projectId),
                String.class,
                expected
        );

        String actual = service.getCompetitionNameForProject(projectId).getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionNameForAssessment() {
        long assessmentId = 13L;
        String expected = "competition name";

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/competition-name", restUrl, "assessment", assessmentId),
                String.class,
                expected
        );

        String actual = service.getCompetitionNameForAssessment(assessmentId).getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getApplicationRolesById() {
        long applicationId = 123L;
        List<Role> expectedRoles = singletonList(Role.COLLABORATOR);

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/user-roles", restUrl, "application", applicationId),
                roleListType(),
                expectedRoles
        );

        List<Role> roles = service.getRolesByApplicationId(applicationId).getSuccess();

        assertEquals(expectedRoles, roles);
    }

    @Test
    public void getProjectRolesById() {
        long projectId = 999L;
        List<Role> expectedRoles = asList(Role.PARTNER, Role.PROJECT_MANAGER);
        setupGetWithRestResultExpectations(
                format("%s/%s/%d/user-roles", restUrl, "project", projectId),
                roleListType(),
                expectedRoles
        );

        List<Role> roles = service.getRolesByProjectId(projectId).getSuccess();

        assertEquals(expectedRoles, roles);
    }

    @Test
    public void getApplicationIdForProject() {
        long projectId = 987L;
        long applicationId = 654L;
        setupGetWithRestResultExpectations(
                format("%s/%s/%d/application-id", restUrl, "project", projectId),
                Long.class,
                applicationId
        );

        long expectedApplicationId = service.getApplicationIdForProject(projectId).getSuccess();

        assertEquals(expectedApplicationId, applicationId);
    }
}
