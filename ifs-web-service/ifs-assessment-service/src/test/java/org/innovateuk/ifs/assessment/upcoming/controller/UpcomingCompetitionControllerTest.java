package org.innovateuk.ifs.assessment.upcoming.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.upcoming.populator.UpcomingCompetitionModelPopulator;
import org.innovateuk.ifs.assessment.upcoming.viewmodel.UpcomingCompetitionViewModel;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = { "classpath:application.yml", "classpath:/application-web-core.properties"} )
public class UpcomingCompetitionControllerTest extends BaseControllerMockMVCTest<UpcomingCompetitionController> {

    @Spy
    @InjectMocks
    private UpcomingCompetitionModelPopulator upcomingCompetitionModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    private static final String restUrl = "/competition";

    @Override
    protected UpcomingCompetitionController supplyControllerUnderTest() {
        return new UpcomingCompetitionController();
    }

    @Test
    public void viewSummary_loggedIn() throws Exception {
        ZonedDateTime dateTime = ZonedDateTime.now();

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(1L)
                .withName("name")
                .withAssessorAcceptsDate(dateTime)
                .withAssessorDeadlineDate(dateTime)
                .build();

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
                .withIncludeAverageAssessorScoreInNotifications(false)
                .withAssessorCount(5)
                .withAssessorPay(BigDecimal.valueOf(100))
                .withHasAssessmentPanel(false)
                .withHasInterviewStage(false)
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .build();

        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        String hash = publicContentItem.getPublicContentResource().getHash();

        UpcomingCompetitionViewModel expectedViewModel = new UpcomingCompetitionViewModel(competitionResource, competitionAssessmentConfigResource, hash);

        when(competitionRestService.getCompetitionById(1L)).thenReturn(restSuccess(competitionResource));
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(competitionAssessmentConfigResource));
        when(publicContentItemRestService.getItemByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(publicContentItem));

        mockMvc.perform(get(restUrl + "/{competitionId}/upcoming", "1"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(status().isOk());

        verify(competitionRestService).getCompetitionById(1L);
    }

    @Test
    public void viewSummary_competitionNotExists() throws Exception {
        when(competitionRestService.getCompetitionById(1L)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get(restUrl + "/{competitionId}/upcoming", "1"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(competitionRestService).getCompetitionById(1L);
    }
}
