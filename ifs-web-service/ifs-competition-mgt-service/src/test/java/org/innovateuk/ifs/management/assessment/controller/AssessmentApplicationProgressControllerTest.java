package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.service.ApplicationAssessmentSummaryRestService;
import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.management.assessment.populator.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.*;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.junit.Test;
import org.mockito.*;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.*;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType.TITLE;
import static org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType.TOTAL_APPLICATIONS;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentApplicationProgressControllerTest extends BaseControllerMockMVCTest<AssessmentApplicationProgressController> {

    @Spy
    @InjectMocks
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Mock
    private ApplicationAssessmentSummaryRestService applicationAssessmentSummaryRestService;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Override
    protected AssessmentApplicationProgressController supplyControllerUnderTest() {
        return new AssessmentApplicationProgressController();
    }

    @Test
    public void applicationProgress() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = setupApplicationAssessmentSummaryResource(competitionId, applicationId);

        List<ApplicationAssessorResource> assigned = setupAssignedApplicationAssessorResources();
        List<ApplicationAssessorResource> rejected = setupRejectedApplicationAssessorResources();
        List<ApplicationAssessorResource> withdrawn = setupWithdrawnApplicationAssessorResources();
        ApplicationAssessorPageResource available = setupAvailableApplicationAssessorResources();

        List<InnovationSectorResource> innovationSectors = setupInnovationSectors();


        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));
        when(applicationAssessmentSummaryRestService.getAssignedAssessors(applicationId)).thenReturn(restSuccess(combineLists(assigned, rejected, withdrawn)));
        when(applicationAssessmentSummaryRestService.getAvailableAssessors(applicationId, 0, 20, "")).thenReturn(restSuccess(available));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(innovationSectors));
        String assessorOrigin = "?page=0&assessorNameFilter=";

        Pagination expectedPaginationModel = new Pagination(available, assessorOrigin);

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                "Digital Manufacturing",
                competitionId,
                "Connected digital additive manufacturing",
                true,
                "Liquid Dynamics",
                asList("Acme Ltd.", "IO Systems"),
                setupExpectedAssignedRows(),
                setupExpectedRejectedRows(),
                setupExpectedPreviouslyAssignedRows(),
                setupExpectedAvailableAssessors(),
                innovationSectors,
                "",
                expectedPaginationModel
        );

        mockMvc.perform(get("/assessment/competition/{competitionId}/application/{applicationId}/assessors?page=0&assessorNameFilter=", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("competition/application-progress"));

        InOrder inOrder = Mockito.inOrder(applicationAssessmentSummaryRestService, categoryRestServiceMock);
        inOrder.verify(applicationAssessmentSummaryRestService).getApplicationAssessmentSummary(applicationId);
        inOrder.verify(applicationAssessmentSummaryRestService).getAssignedAssessors(applicationId);
        inOrder.verify(applicationAssessmentSummaryRestService).getAvailableAssessors(applicationId, 0, 20, "");
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
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

        mockMvc.perform(post("/assessment/competition/{competitionId}/application/{applicationId}/assessors/assign/{assessorId}", competitionId, applicationId, assessorId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/competition/%s/application/%s/assessors?sortField=%s", competitionId, applicationId, TITLE.name())));

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

        mockMvc.perform(post("/assessment/competition/{competitionId}/application/{applicationId}/assessors/assign/{assessorId}", competitionId, applicationId, assessorId)
                .param("sortField", TOTAL_APPLICATIONS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/competition/%s/application/%s/assessors?sortField=%s", competitionId, applicationId, TOTAL_APPLICATIONS.name())));

        verify(assessmentRestService, only()).createAssessment(expectedAssessmentCreateResource);
        verifyNoMoreInteractions(assessmentRestService, applicationAssessmentSummaryRestService);
    }

    @Test
    public void withdrawAssessment() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessmentId = 3L;

        when(assessmentRestService.withdrawAssessment(assessmentId)).thenReturn(restSuccess());

        mockMvc.perform(
                post("/assessment/competition/{competitionId}/application/{applicationId}/assessors/withdraw/{assessmentId}", competitionId, applicationId, assessmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/competition/%s/application/%s/assessors?sortField=%s", competitionId, applicationId, TITLE.name())));

        InOrder inOrder = inOrder(assessmentRestService);
        inOrder.verify(assessmentRestService).withdrawAssessment(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessment_preservesQueryParams() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessmentId = 3L;

        when(assessmentRestService.withdrawAssessment(assessmentId)).thenReturn(restSuccess());

        mockMvc.perform(
                post("/assessment/competition/{competitionId}/application/{applicationId}/assessors/withdraw/{assessmentId}", competitionId, applicationId, assessmentId)
                        .param("sortField", TOTAL_APPLICATIONS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/competition/%s/application/%s/assessors?sortField=%s", competitionId, applicationId, TOTAL_APPLICATIONS.name())));

        InOrder inOrder = inOrder(assessmentRestService);
        inOrder.verify(assessmentRestService).withdrawAssessment(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessmentConfirm() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessmentId = 3L;

        ApplicationAssessmentProgressRemoveViewModel expectedModel = new ApplicationAssessmentProgressRemoveViewModel(
                competitionId,
                applicationId,
                assessmentId,
                TITLE
        );

        mockMvc.perform(
                get("/assessment/competition/{competitionId}/application/{applicationId}/assessors/withdraw/{assessmentId}/confirm", competitionId, applicationId, assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/application-progress-remove-confirm"));
    }

    @Test
    public void withdrawAssessmentConfirm_preservesQueryParams() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long assessmentId = 3L;

        ApplicationAssessmentProgressRemoveViewModel expectedModel = new ApplicationAssessmentProgressRemoveViewModel(
                competitionId,
                applicationId,
                assessmentId,
                TOTAL_APPLICATIONS
        );

        mockMvc.perform(
                get("/assessment/competition/{competitionId}/application/{applicationId}/assessors/withdraw/{assessmentId}/confirm", competitionId, applicationId, assessmentId)
                        .param("sortField", TOTAL_APPLICATIONS.name()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/application-progress-remove-confirm"));
    }

    private ApplicationAssessmentSummaryResource setupApplicationAssessmentSummaryResource(Long competitionId, Long applicationId) {
        return newApplicationAssessmentSummaryResource()
                .withId(applicationId)
                .withName("Progressive Machines")
                .withInnovationArea("Digital Manufacturing")
                .withCompetitionId(competitionId)
                .withCompetitionName("Connected digital additive manufacturing")
                .withLeadOrganisation("Liquid Dynamics")
                .withPartnerOrganisations(asList("Acme Ltd.", "IO Systems"))
                .withCompetitionStatus(IN_ASSESSMENT)
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
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Internet of Things", "Open")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Creative Economy", "Biosciences")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Independent living and wellbeing", "Cyber Security")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("User Experience", "Resource efficiency")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Technical feasibility", "Diagnostics, medical technology and devices")
                                .buildSet(2)
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
                .withUserId(7L, 8L, 9L)
                .withFirstName("Angela", "Anne", "David")
                .withLastName("Casey", "Chadwick", "Cherrie")
                .withBusinessType(ACADEMIC, BUSINESS, ACADEMIC)
                .withInnovationAreas(newInnovationAreaResource()
                                .withName("Infrastructure systems", "Earth Observation")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Internet of Things", "Open")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Creative Economy", "Biosciences")
                                .buildSet(2))
                .withRejectReason(CONFLICT_OF_INTEREST, TOO_MANY_ASSESSMENTS, NOT_AREA_OF_EXPERTISE)
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
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("User Experience", "Precision medicine")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Advanced Materials", "Nuclear")
                                .buildSet(2))
                .withMostRecentAssessmentState(WITHDRAWN)
                .withTotalApplicationsCount(24L, 2L, 5L)
                .withAssignedCount(6L, 1L, 3L)
                .withSubmittedCount(2L, 0L, 3L)
                .build(3);
    }

    private ApplicationAssessorPageResource setupAvailableApplicationAssessorResources() {
        List<ApplicationAssessorResource> resources = newApplicationAssessorResource()
                .withUserId(13L, 14L, 15L)
                .withFirstName("Christopher", "Jayne", "Narinder")
                .withLastName("Dockerty", "Gill", "Goddard")
                .withBusinessType(ACADEMIC, BUSINESS, ACADEMIC)
                .withInnovationAreas(newInnovationAreaResource()
                                .withName("Experimental development", "Infrastructure")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Electronics, Sensors and photonics", "Digital health")
                                .buildSet(2),
                        newInnovationAreaResource()
                                .withName("Manufacturing Readiness", "Offshore Renewable Energy")
                                .buildSet(2))
                .withSkillAreas("Solar Power, Genetics, Recycling", "Human computer interaction, Wearables, IoT", "Electronic/photonic components")
                .withAvailable(true)
                .withTotalApplicationsCount(9L, 4L, 3L)
                .withAssignedCount(5L, 1L, 1L)
                .withSubmittedCount(2L, 1L, 0L)
                .build(3);
        return new ApplicationAssessorPageResource(3, 1, resources, 0, 20);
    }

    private List<ApplicationAssessmentProgressAssignedRowViewModel> setupExpectedAssignedRows() {
        return asList(
                new ApplicationAssessmentProgressAssignedRowViewModel(1L, "William Adamson", 6, 6, BUSINESS,
                        asList("Infrastructure systems", "Earth Observation"), false, false, false, false, 100L),
                new ApplicationAssessmentProgressAssignedRowViewModel(2L, "Richard Bown", 4, 3, ACADEMIC,
                        asList("Internet of Things", "Open"), true, false, false, false, 101L),
                new ApplicationAssessmentProgressAssignedRowViewModel(3L, "Rachel Carr", 5, 1, BUSINESS,
                        asList("Creative Economy", "Biosciences"), true, true, false, false, 102L),
                new ApplicationAssessmentProgressAssignedRowViewModel(4L, "Samantha Peacock", 7, 5, ACADEMIC,
                        asList("Independent living and wellbeing", "Cyber Security"), true, true, true, false, 103L),
                new ApplicationAssessmentProgressAssignedRowViewModel(5L, "Valerie Lloyd", 6, 2, BUSINESS,
                        asList("User Experience", "Resource efficiency"), true, true, true, false, 104L),
                new ApplicationAssessmentProgressAssignedRowViewModel(6L, "Gareth Morris", 3, 1, ACADEMIC,
                        asList("Technical feasibility", "Diagnostics, medical technology and devices"), true, true, true, true, 105L));
    }

    private List<ApplicationAssessmentProgressRejectedRowViewModel> setupExpectedRejectedRows() {
        return asList(
                new ApplicationAssessmentProgressRejectedRowViewModel(7L, "Angela Casey", 6, 6, ACADEMIC,
                        asList("Infrastructure systems", "Earth Observation"), CONFLICT_OF_INTEREST, "Member of board of directors"),
                new ApplicationAssessmentProgressRejectedRowViewModel(8L, "Anne Chadwick", 7, 4, BUSINESS,
                        asList("Internet of Things", "Open"), TOO_MANY_ASSESSMENTS, "I do like reviewing the applications to your competitions but please do not assign so many to me."),
                new ApplicationAssessmentProgressRejectedRowViewModel(9L, "David Cherrie", 1, 1, ACADEMIC,
                        asList("Creative Economy", "Biosciences"), NOT_AREA_OF_EXPERTISE, "No prior experience"));
    }

    private List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> setupExpectedPreviouslyAssignedRows() {
        return asList(
                new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel(10L, "Paul Cousins", 24, 6, BUSINESS,
                        asList("Data", "Cyber Security")),
                new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel(11L, "Graeme Crawford", 2, 1, ACADEMIC,
                        asList("User Experience", "Precision medicine")),
                new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel(12L, "Lawrence Currie", 5, 3, BUSINESS,
                        asList("Advanced Materials", "Nuclear"))
        );
    }

    private List<ApplicationAvailableAssessorsRowViewModel> setupExpectedAvailableAssessors() {
        return asList(
                new ApplicationAvailableAssessorsRowViewModel(13L, "Christopher Dockerty", "Solar Power, Genetics, Recycling", 9, 5, 2),
                new ApplicationAvailableAssessorsRowViewModel(14L, "Jayne Gill", "Human computer interaction, Wearables, IoT", 4, 1, 1),
                new ApplicationAvailableAssessorsRowViewModel(15L, "Narinder Goddard", "Electronic/photonic components", 3, 1, 0));
    }

    private List<InnovationSectorResource> setupInnovationSectors() {
        return newInnovationSectorResource()
                .withName("Materials and manufacturing", "Infrastructure systems")
                .withChildren(newInnovationAreaResource()
                                .withName("Experimental development", "Infrastructure")
                                .build(2),
                        newInnovationAreaResource()
                                .withName("Data", "Cyber Security")
                                .build(2))
                .build(2);
    }
}