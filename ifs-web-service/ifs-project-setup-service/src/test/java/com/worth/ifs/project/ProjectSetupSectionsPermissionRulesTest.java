package com.worth.ifs.project;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.Optional;

import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectSetupSectionsPermissionRulesTest extends BasePermissionRulesTest<ProjectSetupSectionsPermissionRules> {

    private UserResource user = newUserResource().build();

    @Test
    public void testPartnerCanAccessProjectDetailsSection() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(456L).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withOrganisationId(789L).
                        build(1)).
                build();

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        assertTrue(rules.partnerCanAccessProjectDetailsSection(123L, user));

        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    @Test
    public void testPartnerCanAccessProjectDetailsSectionButNotOnThisProject() {
        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenThrow(new ForbiddenActionException());
        assertFalse(rules.partnerCanAccessProjectDetailsSection(123L, user));
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    @Test
    public void testPartnerCanAccessProjectDetailsSectionAndIsLeadPartner() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(456L).
                        build()).
                build();

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        assertTrue(rules.partnerCanAccessProjectDetailsSection(123L, user));

        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    @Override
    protected ProjectSetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectSetupSectionsPermissionRules();
    }
}
