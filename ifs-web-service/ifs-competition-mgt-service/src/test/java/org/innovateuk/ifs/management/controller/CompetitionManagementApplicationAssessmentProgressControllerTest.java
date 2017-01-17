package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationAvailableAssessorsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressAssignedRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsViewModel;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementApplicationAssessmentProgressControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationAssessmentProgressController> {

    @Spy
    @InjectMocks
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationAvailableAssessorsModelPopulator applicationAvailableAssessorsModelPopulator;

    @Override
    protected CompetitionManagementApplicationAssessmentProgressController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationAssessmentProgressController();
    }

    @Test
    public void applicationProgress() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = newApplicationAssessmentSummaryResource()
                .withId(applicationId)
                .withName("Progressive Machines")
                .withCompetitionId(competitionId)
                .withCompetitionName("Connected digital additive manufacturing")
                .withPartnerOrganisations(asList("Acme Ltd.", "IO Systems"))
                .build();

        List<ApplicationAssessorResource> assigned = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(6, 4, 5, 7, 6, 3)
                .withAssignedCount(6, 3, 1, 5, 2, 1)
                .withSubmittedCount(4, 1, 0, 2, 1, 0)
                .build(6);

        List<ApplicationAssessorResource> rejected = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(6, 7, 1)
                .withAssignedCount(6, 4, 1)
                .withSubmittedCount(2, 3, 0)
                .build(3);

        List<ApplicationAssessorResource> withdrawn = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(24, 2, 5)
                .withAssignedCount(6, 1, 3)
                .withSubmittedCount(2, 0, 3)
                .build(3);

        List<ApplicationAssessorResource> available = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(9, 4, 3)
                .withAssignedCount(5, 1, 1)
                .withSubmittedCount(2, 1, 0)
                .build(3);

        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));
        when(applicationAssessmentSummaryRestService.getAssessors(applicationId)).thenReturn(restSuccess(combineLists(assigned, rejected, withdrawn, available)));

        List<ApplicationAssessmentProgressAssignedRowViewModel> expectedAssignedRows = asList(
                new ApplicationAssessmentProgressAssignedRowViewModel("William Adamson", 6, 6, BUSINESS,
                        asList("Infrastructure systems", "Earth Observation"), false, false, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Richard Bown", 4, 3, ACADEMIC,
                        asList("Internet of Things", "Open"), true, false, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Rachel Carr", 5, 1, BUSINESS,
                        asList("Creative Economy", "Bioscience"), true, true, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Samantha Peacock", 7, 5, ACADEMIC,
                        asList("Enhanced Food Quality", "Cyber Security"), true, true, true, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Valerie Lloyd", 6, 2, BUSINESS,
                        asList("User Experience", "Resource efficiency"), true, true, true, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Gareth Morris", 3, 1, ACADEMIC,
                        asList("Technical feasibility", "Medicines Technology"), true, true, true, true));

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems"),
                expectedAssignedRows
        );

        List<ApplicationAvailableAssessorsRowViewModel> assessors = asList(
                new ApplicationAvailableAssessorsRowViewModel("John Smith", "John's skills", 10, 4, 3),
                new ApplicationAvailableAssessorsRowViewModel("Phil Jones", "Phil's skills", 6, 2, 1));

        ApplicationAvailableAssessorsViewModel expectedAssessors = new ApplicationAvailableAssessorsViewModel(assessors);

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeSortField", "title"))
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("available", expectedAssessors))
                .andExpect(view().name("competition/application-progress"));

        InOrder inOrder = Mockito.inOrder(applicationAssessmentSummaryRestService);
        inOrder.verify(applicationAssessmentSummaryRestService).getApplicationAssessmentSummary(applicationId);
        inOrder.verify(applicationAssessmentSummaryRestService).getAssessors(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void availableAssessorsSortedByTotalApplications() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = newApplicationAssessmentSummaryResource()
                .withId(applicationId)
                .withName("Progressive Machines")
                .withCompetitionId(competitionId)
                .withCompetitionName("Connected digital additive manufacturing")
                .withPartnerOrganisations(asList("Acme Ltd.", "IO Systems"))
                .build();

        List<ApplicationAssessorResource> assigned = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(6, 4, 5, 7, 6, 3)
                .withAssignedCount(6, 3, 1, 5, 2, 1)
                .withSubmittedCount(4, 1, 0, 2, 1, 0)
                .build(6);

        List<ApplicationAssessorResource> rejected = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(6, 7, 1)
                .withAssignedCount(6, 4, 1)
                .withSubmittedCount(2, 3, 0)
                .build(3);

        List<ApplicationAssessorResource> withdrawn = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(24, 2, 5)
                .withAssignedCount(6, 1, 3)
                .withSubmittedCount(2, 0, 3)
                .build(3);

        List<ApplicationAssessorResource> available = newApplicationAssessorResource()
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
                .withTotalApplicationsCount(9, 4, 3)
                .withAssignedCount(5, 1, 1)
                .withSubmittedCount(2, 1, 0)
                .build(3);

        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));
        when(applicationAssessmentSummaryRestService.getAssessors(applicationId)).thenReturn(restSuccess(combineLists(assigned, rejected, withdrawn, available)));

        List<ApplicationAssessmentProgressAssignedRowViewModel> expectedAssignedRows = asList(
                new ApplicationAssessmentProgressAssignedRowViewModel("William Adamson", 6, 6, BUSINESS,
                        asList("Infrastructure systems", "Earth Observation"), false, false, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Richard Bown", 4, 3, ACADEMIC,
                        asList("Internet of Things", "Open"), true, false, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Rachel Carr", 5, 1, BUSINESS,
                        asList("Creative Economy", "Bioscience"), true, true, false, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Samantha Peacock", 7, 5, ACADEMIC,
                        asList("Enhanced Food Quality", "Cyber Security"), true, true, true, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Valerie Lloyd", 6, 2, BUSINESS,
                        asList("User Experience", "Resource efficiency"), true, true, true, false),
                new ApplicationAssessmentProgressAssignedRowViewModel("Gareth Morris", 3, 1, ACADEMIC,
                        asList("Technical feasibility", "Medicines Technology"), true, true, true, true));

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems"),
                expectedAssignedRows
        );


        List<ApplicationAvailableAssessorsRowViewModel> assessors = asList(
                new ApplicationAvailableAssessorsRowViewModel("John Smith", "John's skills", 10, 4, 3),
                new ApplicationAvailableAssessorsRowViewModel("Phil Jones", "Phil's skills", 6, 2, 1));

        Collections.sort(assessors, Comparator.comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplications));
        ApplicationAvailableAssessorsViewModel expectedAssessors = new ApplicationAvailableAssessorsViewModel(assessors);

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors?sort=totalApplications", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeSortField", "totalApplications"))
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("available", expectedAssessors))
                .andExpect(view().name("competition/application-progress"));

        InOrder inOrder = Mockito.inOrder(applicationAssessmentSummaryRestService);
        inOrder.verify(applicationAssessmentSummaryRestService).getApplicationAssessmentSummary(applicationId);
        inOrder.verify(applicationAssessmentSummaryRestService).getAssessors(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

}