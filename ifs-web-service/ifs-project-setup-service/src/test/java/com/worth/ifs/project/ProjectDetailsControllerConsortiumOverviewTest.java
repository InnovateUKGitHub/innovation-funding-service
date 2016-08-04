package com.worth.ifs.project;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus;
import com.worth.ifs.project.consortiumoverview.viewmodel.LeadPartnerModel;
import com.worth.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import com.worth.ifs.project.consortiumoverview.viewmodel.RegularPartnerModel;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus.ACTION_REQUIRED;
import static com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus.COMPLETE;
import static com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus.NOT_STARTED;
import static com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus.PENDING;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailsControllerConsortiumOverviewTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Before
    public void setUp() {
        super.setUp();
        setupInvites();
        loginDefaultUser();
        loggedInUser.setOrganisations(Collections.singletonList(8L));
    }

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }

    @Test
    public void testViewProjectTeamStatus() throws Exception {
        Long projectId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        OrganisationResource leadOrganisation = newOrganisationResource().withId(1L).build();
        List<OrganisationResource> otherOrganisations = newOrganisationResource().withId(2L,3L).build(2);
        BankDetailsResource bankDetails = newBankDetailsResource().build();
        Optional<MonitoringOfficerResource> monitoringOfficer = Optional.of(newMonitoringOfficerResource().build());

        LeadPartnerModel leadPartnerModel = createLeadPartnerModel(project, leadOrganisation, bankDetails, monitoringOfficer);

        List<RegularPartnerModel> otherPartners = simpleMap(otherOrganisations, partner -> createPartnerModel(project, partner, bankDetails));

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisation);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(otherOrganisations);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficer);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(anyLong(), anyLong())).thenReturn(restSuccess(bankDetails));


        ProjectConsortiumStatusViewModel expected = new ProjectConsortiumStatusViewModel(projectId, leadPartnerModel, otherPartners);

        mockMvc.perform(get("/project/{id}/team-status", projectId))
            .andExpect(view().name("project/consortium-status"))
            .andExpect(model().attribute("model", expected));
    }

    private LeadPartnerModel createLeadPartnerModel(final ProjectResource project, final OrganisationResource leadOrganisation, final BankDetailsResource bankDetails, final Optional<MonitoringOfficerResource> monitoringOfficer) {
        Optional<BankDetailsResource> bankDetailsOptional = Optional.of(bankDetails);

        String name = leadOrganisation.getName();
        ConsortiumPartnerStatus projectDetailsStatus = createProjectDetailsStatus(project);
        ConsortiumPartnerStatus monitoringOfficerStatus = createMonitoringOfficerStatus(monitoringOfficer, projectDetailsStatus);
        ConsortiumPartnerStatus bankDetailsStatus = createBankDetailStatus(bankDetailsOptional);
        ConsortiumPartnerStatus financeChecksStatus = createFinanceCheckStatus(bankDetailsOptional, bankDetailsStatus);
        ConsortiumPartnerStatus spendProfileStatus = createSpendProfileStatus(financeChecksStatus);
        ConsortiumPartnerStatus otherDocumentStatus = createOtherDocumentStatus(project);
        ConsortiumPartnerStatus grantOfferLetterStatus = createGrantOfferLetterStatus();

        return new LeadPartnerModel(
            name,
            projectDetailsStatus,
            monitoringOfficerStatus,
            bankDetailsStatus,
            financeChecksStatus,
            spendProfileStatus,
            otherDocumentStatus,
            grantOfferLetterStatus
        );
    }

    private RegularPartnerModel createPartnerModel(final ProjectResource project, final OrganisationResource partner, final BankDetailsResource bankDetails) {
        final Optional<BankDetailsResource> bankDetailsOptional = Optional.of(bankDetails);

        final String name = partner.getName();
        final ConsortiumPartnerStatus projectDetailsStatus = createProjectDetailsStatus(project);
        final ConsortiumPartnerStatus bankDetailsStatus = createBankDetailStatus(bankDetailsOptional);
        final ConsortiumPartnerStatus financeChecksStatus = createFinanceCheckStatus(bankDetailsOptional, bankDetailsStatus);
        final ConsortiumPartnerStatus spendProfileStatus = createSpendProfileStatus(financeChecksStatus);

        return new RegularPartnerModel(
            name,
            projectDetailsStatus,
            bankDetailsStatus,
            financeChecksStatus,
            spendProfileStatus
        );
    }

    private ConsortiumPartnerStatus createProjectDetailsStatus(final ProjectResource project) {
        return project.isProjectDetailsSubmitted()?COMPLETE:ACTION_REQUIRED;
    }

    private ConsortiumPartnerStatus createMonitoringOfficerStatus(final Optional<MonitoringOfficerResource> monitoringOfficer, final ConsortiumPartnerStatus leadProjectDetailsSubmitted) {
        if(leadProjectDetailsSubmitted.equals(COMPLETE)){
            return monitoringOfficer.isPresent()? COMPLETE : PENDING;
        }else{
            return NOT_STARTED;
        }

    }

    private ConsortiumPartnerStatus createBankDetailStatus(final Optional<BankDetailsResource> bankDetails) {
        if(bankDetails.isPresent()){
            return bankDetails.get().isApproved()?COMPLETE:PENDING;
        }else{
            return ACTION_REQUIRED;
        }
    }

    private ConsortiumPartnerStatus createFinanceCheckStatus(final Optional<BankDetailsResource> bankDetails, final ConsortiumPartnerStatus bankDetailsStatus) {
        if(bankDetailsStatus.equals(COMPLETE)){
            //TODO update logic when Finance checks are implemented
            return COMPLETE;
        }
        else{
            return NOT_STARTED;
        }
    }

    private ConsortiumPartnerStatus createSpendProfileStatus(final ConsortiumPartnerStatus financeChecksStatus) {
        if(financeChecksStatus.equals(COMPLETE)){
            //TODO update logic when spend profile is implemented
            return COMPLETE;
        }else{
            return NOT_STARTED;
        }
    }

    private ConsortiumPartnerStatus createOtherDocumentStatus(final ProjectResource project) {
        if(project.getCollaborationAgreement()!= null && project.getExploitationPlan()!= null){
            return COMPLETE;
        }else{
            return ACTION_REQUIRED;
        }
    }

    private ConsortiumPartnerStatus createGrantOfferLetterStatus() {
        //TODO update logic when GrantOfferLetter is implemented
        return NOT_STARTED;
    }
}
