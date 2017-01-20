package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressAssignedRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementApplicationAssessmentProgressControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationAssessmentProgressController> {

    @Spy
    @InjectMocks
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Override
    protected CompetitionManagementApplicationAssessmentProgressController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationAssessmentProgressController();
    }

    @Test
    public void applicationProgress() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = setupApplicationAssessmentSummaryResource(competitionId, applicationId);

        List<ApplicationAssessorResource> assigned = setupAssignedApplicationAssessorResources();
        List<ApplicationAssessorResource> rejected = setupRejectedApplicationAssessorResources();
        List<ApplicationAssessorResource> withdrawn = setupWithdrawnApplicationAssessorResources();
        List<ApplicationAssessorResource> available = setupAvailableApplicationAssessorResources();

        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));
        when(applicationAssessmentSummaryRestService.getAssessors(applicationId)).thenReturn(restSuccess(combineLists(assigned, rejected, withdrawn, available)));

        List<ApplicationAssessmentProgressAssignedRowViewModel> expectedAssignedRows = setupExpectedAssignedRows();
        List<ApplicationAvailableAssessorsRowViewModel> expectedAvailableAssessors = setupExpectedAvailableAssessors();

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems"),
                expectedAssignedRows,
                expectedAvailableAssessors
        );

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("competition/application-progress"));

        InOrder inOrder = Mockito.inOrder(applicationAssessmentSummaryRestService);
        inOrder.verify(applicationAssessmentSummaryRestService).getApplicationAssessmentSummary(applicationId);
        inOrder.verify(applicationAssessmentSummaryRestService).getAssessors(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void applicationProgressWithAvailableAssessorsSortedByTotalApplications() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = setupApplicationAssessmentSummaryResource(competitionId, applicationId);

        List<ApplicationAssessorResource> assigned = setupAssignedApplicationAssessorResources();
        List<ApplicationAssessorResource> rejected = setupRejectedApplicationAssessorResources();
        List<ApplicationAssessorResource> withdrawn = setupWithdrawnApplicationAssessorResources();
        List<ApplicationAssessorResource> available = setupAvailableApplicationAssessorResources();

        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));
        when(applicationAssessmentSummaryRestService.getAssessors(applicationId)).thenReturn(restSuccess(combineLists(assigned, rejected, withdrawn, available)));

        List<ApplicationAssessmentProgressAssignedRowViewModel> expectedAssignedRows = setupExpectedAssignedRows();
        List<ApplicationAvailableAssessorsRowViewModel> expectedAvailableAssessors = setupExpectedAvailableAssessors();
        sort(expectedAvailableAssessors, Comparator.comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplicationsCount));

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems"),
                expectedAssignedRows,
                expectedAvailableAssessors
        );

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors?sortField=TOTAL_APPLICATIONS", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("competition/application-progress"));

        InOrder inOrder = Mockito.inOrder(applicationAssessmentSummaryRestService);
        inOrder.verify(applicationAssessmentSummaryRestService).getApplicationAssessmentSummary(applicationId);
        inOrder.verify(applicationAssessmentSummaryRestService).getAssessors(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void assignAssessor() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessorId = 3L;

        AssessmentCreateResource expectedAssessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(assessmentRestService.createAssessment(expectedAssessmentCreateResource)).thenReturn(restSuccess(expectedAssessmentResource));

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/assessors/assign/{assessorId}", competitionId, applicationId, assessorId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/application/%s/assessors?sortField=TITLE", competitionId, applicationId)));

        verify(assessmentRestService, only()).createAssessment(expectedAssessmentCreateResource);
        verifyNoMoreInteractions(assessmentRestService, applicationAssessmentSummaryRestService);
    }

    @Test
    public void assignAssessor_preservesQueryParams() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessorId = 3L;

        AssessmentCreateResource expectedAssessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(assessmentRestService.createAssessment(expectedAssessmentCreateResource)).thenReturn(restSuccess(expectedAssessmentResource));

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/assessors/assign/{assessorId}", competitionId, applicationId, assessorId)
                .param("sortField", "TOTAL_APPLICATIONS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/application/%s/assessors?sortField=TOTAL_APPLICATIONS", competitionId, applicationId)));

        verify(assessmentRestService, only()).createAssessment(expectedAssessmentCreateResource);
        verifyNoMoreInteractions(assessmentRestService, applicationAssessmentSummaryRestService);
    }

    private ApplicationAssessmentSummaryResource setupApplicationAssessmentSummaryResource(Long competitionId, Long applicationId) {
        return newApplicationAssessmentSummaryResource()
                .withId(applicationId)
                .withName("Progressive Machines")
                .withCompetitionId(competitionId)
                .withCompetitionName("Connected digital additive manufacturing")
                .withPartnerOrganisations(asList("Acme Ltd.", "IO Systems"))
                .build();
    }

    private List<ApplicationAssessorResource> setupAssignedApplicationAssessorResources() {
        return newApplicationAssessorResource()
                .withUserId(1L, 2L, 3L, 4L, 5L, 6L)
                .withFirstName("William", "Richard", "Rachel", "Samantha", "Valerie", "Gareth")
                .withLastName("Adamson", "Bown", "Carr", "Peacock", "Lloyd", "Morris")
                .withBusinessType(BUSINESS, ACADEMIC, BUSINESS, ACADEMIC, BUSINESS, ACADEMIC)
                .withInnovationAreas(newInnovationAreaResource()
                                .withName("Infrastructure systems", "Earth Observation")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Internet of Things", "Open")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Creative Economy", "Bioscience")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Enhanced Food Quality", "Cyber Security")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("User Experience", "Resource efficiency")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Technical feasibility", "Medicines Technology")
                                .build(2)
                )
                .withMostRecentAssessmentState(CREATED, PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED)
                .withTotalApplicationsCount(6L, 4L, 5L, 7L, 6L, 3L)
                .withAssignedCount(6L, 3L, 1L, 5L, 2L, 1L)
                .withSubmittedCount(4L, 1L, 0L, 2L, 1L, 0L)
                .build(6);
    }

    private List<ApplicationAssessorResource> setupRejectedApplicationAssessorResources() {
        return newApplicationAssessorResource()
                .withUserId(7L, 8L, 9L)
                .withFirstName("Angela", "Anne", "David")
                .withLastName("Casey", "Chadwick", "Cherrie")
                .withBusinessType(ACADEMIC, BUSINESS, ACADEMIC)
                .withInnovationAreas(newInnovationAreaResource()
                                .withName("Infrastructure systems", "Earth Observation")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Internet of Things", "Open")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Creative Economy", "Bioscience")
                                .build(2))
                .withRejectReason("Conflict of interest", "Not available", "Not my area of expertise")
                .withRejectComment("Member of board of directors", "I do like reviewing the applications to your competitions but please do not assign so many to me.", "No prior experience")
                .withMostRecentAssessmentState(REJECTED)
                .withTotalApplicationsCount(6L, 7L, 1L)
                .withAssignedCount(6L, 4L, 1L)
                .withSubmittedCount(2L, 3L, 0L)
                .build(3);
    }

    private List<ApplicationAssessorResource> setupWithdrawnApplicationAssessorResources() {
        return newApplicationAssessorResource()
                .withUserId(10L, 11L, 12L)
                .withFirstName("Paul", "Graeme", "Lawrence")
                .withLastName("Cousins", "Crawford", "Currie")
                .withBusinessType(BUSINESS, ACADEMIC, BUSINESS)
                .withInnovationAreas(newInnovationAreaResource()
                                .withName("Data", "Cyber Security")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("User Experience", "Precision Medicine")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Advanced Materials", "Nuclear")
                                .build(2))
                .withMostRecentAssessmentState(WITHDRAWN)
                .withTotalApplicationsCount(24L, 2L, 5L)
                .withAssignedCount(6L, 1L, 3L)
                .withSubmittedCount(2L, 0L, 3L)
                .build(3);
    }

    private List<ApplicationAssessorResource> setupAvailableApplicationAssessorResources() {
        return newApplicationAssessorResource()
                .withUserId(13L, 14L, 15L)
                .withFirstName("Christopher", "Jayne", "Narinder")
                .withLastName("Dockerty", "Gill", "Goddard")
                .withBusinessType(ACADEMIC, BUSINESS, ACADEMIC)
                .withInnovationAreas(newInnovationAreaResource()
                                .withName("Experimental development", "Infrastructure")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Electronics, Sensors and photonics", "Agri Productivity")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Manufacturing Readiness", "Offshore Renewable Energy")
                                .build(2))
                .withSkillAreas("Solar Power, Genetics, Recycling", "Human computer interaction, Wearables, IoT", "Electronic/photonic components")
                .withAvailable(true)
                .withTotalApplicationsCount(9L, 4L, 3L)
                .withAssignedCount(5L, 1L, 1L)
                .withSubmittedCount(2L, 1L, 0L)
                .build(3);
    }

    private List<ApplicationAssessmentProgressAssignedRowViewModel> setupExpectedAssignedRows() {
        return asList(
                new ApplicationAssessmentProgressAssignedRowViewModel(1L, "William Adamson", 6, 6, BUSINESS,
                        asList("Infrastructure systems", "Earth Observation"), false, false, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel(2L, "Richard Bown", 4, 3, ACADEMIC,
                        asList("Internet of Things", "Open"), true, false, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel(3L, "Rachel Carr", 5, 1, BUSINESS,
                        asList("Creative Economy", "Bioscience"), true, true, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel(4L, "Samantha Peacock", 7, 5, ACADEMIC,
                        asList("Enhanced Food Quality", "Cyber Security"), true, true, true, false),
                new ApplicationAssessmentProgressAssignedRowViewModel(5L, "Valerie Lloyd", 6, 2, BUSINESS,
                        asList("User Experience", "Resource efficiency"), true, true, true, false),
                new ApplicationAssessmentProgressAssignedRowViewModel(6L, "Gareth Morris", 3, 1, ACADEMIC,
                        asList("Technical feasibility", "Medicines Technology"), true, true, true, true));
    }

    private List<ApplicationAvailableAssessorsRowViewModel> setupExpectedAvailableAssessors() {
        return asList(
                new ApplicationAvailableAssessorsRowViewModel(13L, "Christopher Dockerty", "Solar Power, Genetics, Recycling", 9, 5, 2),
                new ApplicationAvailableAssessorsRowViewModel(14L, "Jayne Gill", "Human computer interaction, Wearables, IoT", 4, 1, 1),
                new ApplicationAvailableAssessorsRowViewModel(15L, "Narinder Goddard", "Electronic/photonic components", 3, 1, 0));
    }
}