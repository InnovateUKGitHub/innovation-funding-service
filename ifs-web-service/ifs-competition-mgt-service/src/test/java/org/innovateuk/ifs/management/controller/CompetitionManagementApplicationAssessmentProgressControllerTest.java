package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.viewmodel.*;
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
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
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

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems"),
                setupExpectedAssignedRows(),
                setupExpectedRejectedRows(),
                setupExpectedPreviouslyAssignedRows(),
                setupExpectedAvailableAssessors()
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

        List<ApplicationAvailableAssessorsRowViewModel> expectedAvailableAssessors = setupExpectedAvailableAssessors();
        sort(expectedAvailableAssessors, Comparator.comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplicationsCount));

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems"),
                setupExpectedAssignedRows(),
                setupExpectedRejectedRows(),
                setupExpectedPreviouslyAssignedRows(),
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
    public void withdrawAssessment() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessmentId = 3L;

        when(assessmentRestService.withdrawAssessment(assessmentId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/assessors", competitionId, applicationId)
                .param("withdraw", String.valueOf(assessmentId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/application/%s/assessors", competitionId, applicationId)));

        InOrder inOrder = inOrder(assessmentRestService);
        inOrder.verify(assessmentRestService).withdrawAssessment(assessmentId);
        inOrder.verifyNoMoreInteractions();
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
                .withMostRecentAssessmentId(100L, 101L, 102L, 103L, 104L, 105L)
                .withMostRecentAssessmentState(CREATED, PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED)
                .withTotalApplicationsCount(6L, 4L, 5L, 7L, 6L, 3L)
                .withAssignedCount(6L, 3L, 1L, 5L, 2L, 1L)
                .withSubmittedCount(4L, 1L, 0L, 2L, 1L, 0L)
                .build(6);
    }

    private List<ApplicationAssessorResource> setupRejectedApplicationAssessorResources() {
        return newApplicationAssessorResource()
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
                new ApplicationAssessmentProgressAssignedRowViewModel("William Adamson", 6, 6, BUSINESS,
                        asList("Infrastructure systems", "Earth Observation"), false, false, false, false, 100L),
                new ApplicationAssessmentProgressAssignedRowViewModel("Richard Bown", 4, 3, ACADEMIC,
                        asList("Internet of Things", "Open"), true, false, false, false, 101L),
                new ApplicationAssessmentProgressAssignedRowViewModel("Rachel Carr", 5, 1, BUSINESS,
                        asList("Creative Economy", "Bioscience"), true, true, false, false, 102L),
                new ApplicationAssessmentProgressAssignedRowViewModel("Samantha Peacock", 7, 5, ACADEMIC,
                        asList("Enhanced Food Quality", "Cyber Security"), true, true, true, false, 103L),
                new ApplicationAssessmentProgressAssignedRowViewModel("Valerie Lloyd", 6, 2, BUSINESS,
                        asList("User Experience", "Resource efficiency"), true, true, true, false, 104L),
                new ApplicationAssessmentProgressAssignedRowViewModel("Gareth Morris", 3, 1, ACADEMIC,
                        asList("Technical feasibility", "Medicines Technology"), true, true, true, true, 105L));
    }

    private List<ApplicationAssessmentProgressRejectedRowViewModel> setupExpectedRejectedRows() {
        return asList(
                new ApplicationAssessmentProgressRejectedRowViewModel("Angela Casey", 6, 6, ACADEMIC,
                        asList("Infrastructure systems", "Earth Observation"), "Conflict of interest", "Member of board of directors"),
                new ApplicationAssessmentProgressRejectedRowViewModel("Anne Chadwick", 7, 4, BUSINESS,
                        asList("Internet of Things", "Open"), "Not available", "I do like reviewing the applications to your competitions but please do not assign so many to me."),
                new ApplicationAssessmentProgressRejectedRowViewModel("David Cherrie", 1, 1, ACADEMIC,
                        asList("Creative Economy", "Bioscience"), "Not my area of expertise", "No prior experience"));
    }

    private List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> setupExpectedPreviouslyAssignedRows() {
        return asList(
                new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel("Paul Cousins", 24, 6, BUSINESS,
                        asList("Data", "Cyber Security")),
                new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel("Graeme Crawford", 2, 1, ACADEMIC,
                        asList("User Experience", "Precision Medicine")),
                new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel("Lawrence Currie", 5, 3, BUSINESS,
                        asList("Advanced Materials", "Nuclear"))
        );
    }

    private List<ApplicationAvailableAssessorsRowViewModel> setupExpectedAvailableAssessors() {
        return asList(
                new ApplicationAvailableAssessorsRowViewModel("Christopher Dockerty", "Solar Power, Genetics, Recycling", 9, 5, 2),
                new ApplicationAvailableAssessorsRowViewModel("Jayne Gill", "Human computer interaction, Wearables, IoT", 4, 1, 1),
                new ApplicationAvailableAssessorsRowViewModel("Narinder Goddard", "Electronic/photonic components", 3, 1, 0));
    }
}