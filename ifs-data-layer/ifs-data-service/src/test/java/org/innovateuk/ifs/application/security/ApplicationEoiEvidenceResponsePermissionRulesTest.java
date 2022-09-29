package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationEoiEvidenceResponsePermissionRulesTest extends BasePermissionRulesTest<ApplicationEoiEvidenceResponsePermissionRules> {

    private ApplicationResource applicationResource;
    private ProcessRole processRole;
    private final long organisationId = 1L;
    private UserResource userResource;

    @Override
    protected ApplicationEoiEvidenceResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationEoiEvidenceResponsePermissionRules();
    }

    @Before
    public void setup() {
        applicationResource = newApplicationResource().withLeadOrganisationId(organisationId).build();
        userResource = newUserResource().withRoleGlobal(APPLICANT).build();
        processRole = newProcessRole()
                .withRole(LEADAPPLICANT)
                .withApplication(newApplication()
                        .withId(applicationResource.getId())
                        .build())
                .withOrganisationId(organisationId)
                .withUser(newUser()
                        .withId(userResource.getId())
                        .build())
                .build();
    }

    @Test
    public void leadOrganisationMemberCanSendApplicationSubmittedNotification() {
        when(processRoleRepository.findByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(Collections.singletonList(processRole));
        assertTrue(rules.isLeadOrganisationMemberCanSendApplicationSubmittedNotification(applicationResource, userResource));
    }

    @Test
    public void applicantCanCreateFileEntryAndEoiEvidence() {
        when(processRoleRepository.findByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(Collections.singletonList(processRole));
        assertTrue(rules.applicantCanCreateFileEntryAndEoiEvidence(applicationResource, userResource));
    }

    @Test
    public void applicantCanCreateEoiEvidence() {
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(userResource.getId(), applicationResource.getId(), LEADAPPLICANT)).thenReturn(true);
        assertTrue(rules.applicantCanCreateEoiEvidence(applicationResource, userResource));
    }

    @Test
    public void applicantCanRemoveEoiEvidence() {
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(userResource.getId(), applicationResource.getId(), LEADAPPLICANT)).thenReturn(true);
        assertTrue(rules.applicantCanRemoveEoiEvidence(applicationResource, userResource));
    }
}