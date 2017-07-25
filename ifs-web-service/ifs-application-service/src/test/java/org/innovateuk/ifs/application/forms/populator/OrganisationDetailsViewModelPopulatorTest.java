package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.forms.populator.OrganisationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionOrganisationDetailsViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationDetailsViewModelPopulatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private OrganisationDetailsViewModelPopulator populator;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private ProcessRoleService processRoleService;

    private Long applicationId;

    private List<ProcessRoleResource> userApplicationRoles;

    @Before
    public void setUp() {
        super.setUp();

        applicationId = 23L;
        userApplicationRoles = newProcessRoleResource()
            .withApplication(applicationId)
            .withRole(newRoleResource().withName(UserApplicationRole.LEAD_APPLICANT.getRoleName()).build(),
                    newRoleResource().withName(UserApplicationRole.COLLABORATOR.getRoleName()).build(),
                    newRoleResource().withName(UserApplicationRole.LEAD_APPLICANT.getRoleName()).build())
            .withOrganisation(3L, 4L, 5L)
            .build(3);
    }

    @Test
    public void testPopulateModelWithValidObjects() throws Exception {
        setupSuccess();
        List<ApplicantResource> applicantResources = userApplicationRoles.stream().map(processRoleResource -> newApplicantResource().withProcessRole(processRoleResource).withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).withId(processRoleResource.getOrganisationId()).build()).build()).collect(Collectors.toList());
        ApplicantQuestionResource question = newApplicantQuestionResource().withApplication(newApplicationResource().build()).withApplicants(applicantResources).build();
        question.getApplication().setId(applicationId);


        QuestionOrganisationDetailsViewModel viewModel = populator.populateModel(question);
        assertNotEquals(null, viewModel);

        assertEquals(Long.valueOf(3L), viewModel.getAcademicOrganisations().first().getId());
        assertEquals(Long.valueOf(3L), viewModel.getApplicationOrganisations().first().getId());
        assertEquals(Long.valueOf(3L), viewModel.getLeadOrganisation().getId());
        assertEquals(asList("OrgNameConfirmed"), viewModel.getPendingOrganisationNames());
    }

    @Test(expected = RuntimeException.class)
    public void testPopulateModelWithInvalidObjects() throws Exception {
        userApplicationRoles.forEach(
                processRoleResource -> setupOrganisationServicesFailure(processRoleResource.getOrganisationId()));

        setupInviteServicesFailure(applicationId);
        ApplicantQuestionResource question = ApplicantQuestionResourceBuilder.newApplicantQuestionResource().build();

        QuestionOrganisationDetailsViewModel viewModel = populator.populateModel(question);
    }

    @Test
    public void testPopulateModelOnlyLong() throws Exception {
        setupSuccess();
        when(processRoleService.findProcessRolesByApplicationId(applicationId)).thenReturn(userApplicationRoles);
        List<ApplicantResource> applicantResources = userApplicationRoles.stream().map(processRoleResource -> newApplicantResource().withProcessRole(processRoleResource).withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).withId(processRoleResource.getOrganisationId()).build()).build()).collect(Collectors.toList());
        ApplicantQuestionResource question = newApplicantQuestionResource().withApplication(newApplicationResource().build()).withApplicants(applicantResources).build();
        question.getApplication().setId(applicationId);

        QuestionOrganisationDetailsViewModel viewModel = populator.populateModel(question);

        assertNotEquals(null, viewModel);

        assertEquals(Long.valueOf(3L), viewModel.getAcademicOrganisations().first().getId());
        assertEquals(Long.valueOf(3L), viewModel.getApplicationOrganisations().first().getId());
        assertEquals(Long.valueOf(3L), viewModel.getLeadOrganisation().getId());
        assertEquals(asList("OrgNameConfirmed"), viewModel.getPendingOrganisationNames());
    }

    private void setupSuccess(){
        userApplicationRoles.forEach(
                processRoleResource -> setupOrganisationServicesSuccess(processRoleResource.getOrganisationId(), newOrganisationResource()
                        .withId(processRoleResource.getOrganisationId())
                        .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId())
                        .build()));

        List<InviteOrganisationResource> pendingInvites = newInviteOrganisationResource()
                .withOrganisation(1L)
                .withInviteResources(newApplicationInviteResource()
                        .with((applicationInviteResource) -> {
                            applicationInviteResource.setStatus(InviteStatus.SENT);
                            applicationInviteResource.setInviteOrganisationNameConfirmed("OrgNameConfirmed");
                        }).build(1))
                .build(1);
        setupInviteServicesSuccess(applicationId, pendingInvites);
    }

    private void setupOrganisationServicesSuccess(Long organisationId, OrganisationResource organisation) {
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(RestResult.restSuccess(organisation));
    }

    private void setupInviteServicesSuccess(Long applicationId, List<InviteOrganisationResource> pendingInvites) {
        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(RestResult.restSuccess(pendingInvites));
    }

    private void setupOrganisationServicesFailure(Long organisationId) {
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(RestResult.restFailure(new Error("", HttpStatus.NOT_FOUND)));
    }

    private void setupInviteServicesFailure(Long applicationId) {
        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(RestResult.restFailure(new Error("", HttpStatus.NOT_FOUND)));
    }

}