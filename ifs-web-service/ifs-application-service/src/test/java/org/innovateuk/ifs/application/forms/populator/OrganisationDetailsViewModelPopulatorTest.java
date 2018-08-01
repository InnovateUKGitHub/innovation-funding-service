package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionOrganisationDetailsViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
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
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationDetailsViewModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private OrganisationDetailsViewModelPopulator populator;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private InviteService inviteService;

    @Mock
    private UserRestService userRestService;

    private Long applicationId;

    private List<ProcessRoleResource> userApplicationRoles;

    @Before
    public void setUp() {
        super.setup();

        applicationId = 23L;
        userApplicationRoles = newProcessRoleResource()
            .withApplication(applicationId)
            .withRole(Role.LEADAPPLICANT, Role.COLLABORATOR, Role.LEADAPPLICANT)
            .withOrganisation(3L, 4L, 5L)
            .build(3);
    }

    @Test
    public void populateModelWithValidObjects() throws Exception {
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
    public void populateModelWithInvalidObjects() throws Exception {
        userApplicationRoles.forEach(
                processRoleResource -> setupOrganisationServicesFailure(processRoleResource.getOrganisationId()));

        setupInviteServicesFailure(applicationId);
        ApplicantQuestionResource question = ApplicantQuestionResourceBuilder.newApplicantQuestionResource().build();

        QuestionOrganisationDetailsViewModel viewModel = populator.populateModel(question);
    }

    @Test
    public void populateModelOnlyLong() throws Exception {
        setupSuccess();
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(userApplicationRoles));
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
        setupInviteServiceSuccess(applicationId, pendingInvites);
    }

    private void setupOrganisationServicesSuccess(Long organisationId, OrganisationResource organisation) {
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));
    }

    private void setupOrganisationServicesFailure(Long organisationId) {
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(RestResult.restFailure(new Error("", HttpStatus.NOT_FOUND)));
    }

    private void setupInviteServicesFailure(Long applicationId) {
        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(RestResult.restFailure(new Error("", HttpStatus.NOT_FOUND)));
    }

    private void setupInviteServiceSuccess(long applicationId, List<InviteOrganisationResource> pendingInvites) {
        when(inviteService.getPendingInvitationsByApplicationId(applicationId)).thenReturn(pendingInvites.get(0).getInviteResources());
    }

}