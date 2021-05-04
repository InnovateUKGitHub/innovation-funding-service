package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.assessment.service.AssessmentPeriodService;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.assessor.populator.ManageAssessorsModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.ManageAssessorsRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.ManageAssessorsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryPageResourceBuilder.newAssessorCountSummaryPageResource;
import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ManageAssessorsModelPopulatorTest extends BaseUnitTest {

    @Spy
    @InjectMocks
    private ManageAssessorsModelPopulator manageAssessorsModelPopulator;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private AssessmentPeriodService assessmentPeriodService;

    @Test
    public void populateModel() {
        final CompetitionResource competition = newCompetitionResource().build();
        final long assessmentPeriodId = 3L;
        final long totalElements = 23;
        final int totalPages = 3;
        final int pageSize = 11;
        final int pageNumber = 1;
        final List<InnovationSectorResource> innovationSectorResources = newInnovationSectorResource().build(2);

        final List<AssessorCountSummaryResource> assessorCountSummaryResources = newAssessorCountSummaryResource().build((int) totalElements);

        final AssessorCountSummaryPageResource assessorCountSummaryPageResource = newAssessorCountSummaryPageResource()
                .withTotalElements(totalElements)
                .withTotalPages(totalPages)
                .withSize(pageSize)
                .withNumber(pageNumber)
                .withContent(assessorCountSummaryResources)
                .build();

        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(innovationSectorResources));

        when(assessmentPeriodService.assessmentPeriodName(assessmentPeriodId, competition.getId())).thenReturn("name");

       ManageAssessorsViewModel manageAssessmentsViewModel =
               manageAssessorsModelPopulator.populateModel(competition, assessorCountSummaryPageResource, assessmentPeriodId);

        ManageAssessorsViewModel expectedViewModel = new ManageAssessorsViewModel(
                competition.getId(),
                competition.getName(),
                assessmentPeriodId,
                "name",
                simpleMap(assessorCountSummaryResources, ManageAssessorsRowViewModel::new),
                competition.getCompetitionStatus() == CompetitionStatus.IN_ASSESSMENT,
                false,
                innovationSectorResources,
                new Pagination(assessorCountSummaryPageResource)
        );

        assertEquals(expectedViewModel, manageAssessmentsViewModel);
    }
}