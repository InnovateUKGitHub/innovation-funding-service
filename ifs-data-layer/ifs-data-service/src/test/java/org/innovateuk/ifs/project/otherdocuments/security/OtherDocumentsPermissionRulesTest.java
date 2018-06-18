package org.innovateuk.ifs.project.otherdocuments.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OtherDocumentsPermissionRulesTest extends BasePermissionRulesTest<OtherDocumentsPermissionRules> {
    private ProjectProcess projectProcess;

    @Mock
    private ProjectProcessRepository projectProcessRepositoryMock;

    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(ProjectState.SETUP).build();
    }

    @Override
    protected OtherDocumentsPermissionRules supplyPermissionRulesUnderTest() {
        return new OtherDocumentsPermissionRules();
    }

    @Test
    public void internalUsersCanAcceptOrRejectDocuments() {

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanAcceptOrRejectOtherDocuments(project, user));
            } else {
                assertFalse(rules.internalUserCanAcceptOrRejectOtherDocuments(project, user));
            }
        });
    }

    @Test
    public void leadPartnersCanCreateOtherDocuments() {

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.leadPartnersCanUploadOtherDocuments(project, user));
    }

    @Test
    public void nonLeadPartnersCannotCreateOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnersCanUploadOtherDocuments(project, user));
    }

    @Test
    public void partnersCanViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void nonPartnersCannotViewOtherDocumentsDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanViewOtherDocumentsDetails(project, user));
    }

    @Test
    public void internalUserCanViewOtherDocumentsDetails() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewOtherDocumentsDetails(project, user));
            } else {
                assertFalse(rules.internalUserCanViewOtherDocumentsDetails(project, user));
            }
        });
    }

    @Test
    public void partnersCanDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void nonPartnersCannotDownloadOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadOtherDocuments(project, user));
    }

    @Test
    public void internalUserCanDownloadOtherDocuments() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanDownloadOtherDocuments(project, user));
            } else {
                assertFalse(rules.internalUserCanDownloadOtherDocuments(project, user));
            }
        });
    }

    @Test
    public void leadPartnersCanDeleteOtherDocuments() {

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        UserResource user = newUserResource().build();
        setupUserAsLeadPartner(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.leadPartnersCanDeleteOtherDocuments(project, user));
    }

    @Test
    public void nonLeadPartnersCannotDeleteOtherDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnersCanDeleteOtherDocuments(project, user));
    }

    @Test
    public void onlyProjectManagerCanSubmitDocuments() {
        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.onlyProjectManagerCanMarkDocumentsAsSubmit(project, user));
    }
}
