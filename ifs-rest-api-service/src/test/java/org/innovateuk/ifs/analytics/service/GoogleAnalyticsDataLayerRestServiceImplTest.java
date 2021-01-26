package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.processRoleTypeListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectParticipantRoleListType;
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
        List<ProcessRoleType> expectedRoles = singletonList(ProcessRoleType.LEADAPPLICANT);

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/user-roles", restUrl, "application", applicationId),
                processRoleTypeListType(),
                expectedRoles
        );

        List<ProcessRoleType> roles = service.getRolesByApplicationId(applicationId).getSuccess();

        assertEquals(expectedRoles, roles);
    }

    @Test
    public void getProjectRolesById() {
        long projectId = 999L;
        List<ProjectParticipantRole> expectedRoles = asList(ProjectParticipantRole.PROJECT_PARTNER);
        setupGetWithRestResultExpectations(
                format("%s/%s/%d/user-roles", restUrl, "project", projectId),
                projectParticipantRoleListType(),
                expectedRoles
        );

        List<ProjectParticipantRole> roles = service.getRolesByProjectId(projectId).getSuccess();

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
