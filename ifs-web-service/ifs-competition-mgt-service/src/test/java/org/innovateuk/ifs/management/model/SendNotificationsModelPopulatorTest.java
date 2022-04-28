package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionRestService;
import org.innovateuk.ifs.application.service.ApplicationNotificationTemplateRestService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.funding.form.NotificationEmailsForm;
import org.innovateuk.ifs.management.notification.populator.SendNotificationsModelPopulator;
import org.innovateuk.ifs.management.notification.viewmodel.SendNotificationsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.application.resource.FundingDecision.ON_HOLD;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SendNotificationsModelPopulatorTest {

    public static final Long COMPETITION_ID = 7L;
    public static final String COMPETITION_NAME = "name";

    @InjectMocks
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;

    @Mock
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Mock
    private ApplicationNotificationTemplateRestService applicationNotificationTemplateRestService;

    @Test
    public void populateModel() {

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().withId(COMPETITION_ID).withName(COMPETITION_NAME).build();

        FundingDecisionToSendApplicationResource application1
                = new FundingDecisionToSendApplicationResource(1L, "", "", ON_HOLD);
        FundingDecisionToSendApplicationResource application3
                = new FundingDecisionToSendApplicationResource(3L, "", "", UNFUNDED);

        List<FundingDecisionToSendApplicationResource> applicationResults = Arrays.asList(application1, application3);
        CompetitionAssessmentConfigResource assessmentConfig = newCompetitionAssessmentConfigResource().withIncludeAverageAssessorScoreInNotifications(Boolean.FALSE).build();

        List<Long> requestedIds = Arrays.asList(application1.getId(), application3.getId());

        when(applicationFundingDecisionRestService.getNotificationResourceForApplications(requestedIds)).thenReturn(restSuccess(applicationResults));
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(COMPETITION_ID)).thenReturn(restSuccess(assessmentConfig));

        List<FundingDecisionToSendApplicationResource> expectedApplications = Arrays.asList(application1, application3);
        Map<Long, FundingDecision> expectedDecisions = asMap(application1.getId(), ON_HOLD, application3.getId(), UNFUNDED);

        SendNotificationsViewModel viewModel = sendNotificationsModelPopulator.populate(COMPETITION_ID, requestedIds, new NotificationEmailsForm());

        assertThat(viewModel.getCompetitionId(), is(equalTo(COMPETITION_ID)));
        assertThat(viewModel.getCompetitionName(), is(equalTo(COMPETITION_NAME)));
        assertThat(viewModel.getApplications(), is(equalTo(expectedApplications)));
        assertThat(viewModel.getSuccessfulRecipientsCount(), is(equalTo(0L)));
        assertThat(viewModel.getUnsuccessfulRecipientsCount(), is(equalTo(1L)));
        assertThat(viewModel.getOnHoldRecipientsCount(), is(equalTo(1L)));
        assertThat(viewModel.getFundingDecisions(), is(equalTo(expectedDecisions)));
        assertFalse(viewModel.isHorizonEurope());
    }

    @Test
    public void populateModel_horizonEurope() {

        NotificationEmailsForm notificationEmailsForm = new NotificationEmailsForm();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(COMPETITION_NAME)
                .withCompetitionTypeEnum(CompetitionTypeEnum.HORIZON_EUROPE_GUARANTEE)
                .build();

        ApplicationNotificationTemplateResource notificationTemplateResource = new ApplicationNotificationTemplateResource("horizonEurope_unsuccessful_template.html");

        FundingDecisionToSendApplicationResource application
                = new FundingDecisionToSendApplicationResource(3L, "", "", UNFUNDED);

        List<FundingDecisionToSendApplicationResource> applicationResults = Collections.singletonList(application);
        CompetitionAssessmentConfigResource assessmentConfig = newCompetitionAssessmentConfigResource().withIncludeAverageAssessorScoreInNotifications(Boolean.FALSE).build();

        List<Long> requestedIds =  Collections.singletonList(application.getId());

        when(applicationFundingDecisionRestService.getNotificationResourceForApplications(requestedIds)).thenReturn(restSuccess(applicationResults));
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(COMPETITION_ID)).thenReturn(restSuccess(assessmentConfig));
        when(applicationNotificationTemplateRestService.getUnsuccessfulNotificationTemplate(COMPETITION_ID)).thenReturn(restSuccess(notificationTemplateResource));

        SendNotificationsViewModel viewModel = sendNotificationsModelPopulator.populate(COMPETITION_ID, requestedIds, notificationEmailsForm);

        assertTrue(viewModel.isHorizonEurope());
        assertEquals("horizonEurope_unsuccessful_template.html", notificationEmailsForm.getMessage());
    }
}