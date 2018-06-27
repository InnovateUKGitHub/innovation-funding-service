package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.DEFAULT_NOTIFICATION_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ApplicationNotificationTemplateServiceImplMockTest extends BaseServiceUnitTest<ApplicationNotificationTemplateServiceImpl> {

    private static final String webBaseUrl = "http://ifs-local-dev";
    private static final DateTimeFormatter formatter = ofPattern("d MMMM yyyy");

    @Mock
    private NotificationTemplateRenderer renderer;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private CompetitionRepository competitionRepository;

    @Override
    protected ApplicationNotificationTemplateServiceImpl supplyServiceUnderTest() {
        ApplicationNotificationTemplateServiceImpl service = new ApplicationNotificationTemplateServiceImpl();
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
        return service;
    }

    @Test
    public void getSuccessfulNotificationTemplate() {
        long competitionId = 1L;
        ZonedDateTime feedbackDate = ZonedDateTime.now();
        Competition competition = newCompetition().withName("Competition").withReleaseFeedbackDate(feedbackDate).build();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("competitionName", competition.getName());
        arguments.put("dashboardUrl", webBaseUrl);
        arguments.put("feedbackDate", toUkTimeZone(competition.getReleaseFeedbackDate()).format(formatter));

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(renderer.renderTemplate(eq(systemNotificationSource), any(),
                eq(DEFAULT_NOTIFICATION_TEMPLATES_PATH + "successful_funding_decision.html"), eq(arguments)))
                .thenReturn(serviceSuccess("MessageBody"));

        ServiceResult<ApplicationNotificationTemplateResource> result = service.getSuccessfulNotificationTemplate(competitionId);

        assertTrue(result.isSuccess());
        assertEquals("MessageBody", result.getSuccess().getMessageBody());
    }

    @Test
    public void getUnsuccessfulNotificationTemplate() {
        long competitionId = 1L;
        ZonedDateTime feedbackDate = ZonedDateTime.now();
        Competition competition = newCompetition().withName("Competition").withReleaseFeedbackDate(feedbackDate).build();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("competitionName", competition.getName());
        arguments.put("dashboardUrl", webBaseUrl);
        arguments.put("feedbackDate", toUkTimeZone(competition.getReleaseFeedbackDate()).format(formatter));

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(renderer.renderTemplate(eq(systemNotificationSource), any(),
                eq(DEFAULT_NOTIFICATION_TEMPLATES_PATH + "unsuccessful_funding_decision.html"), eq(arguments)))
                .thenReturn(serviceSuccess("MessageBody"));

        ServiceResult<ApplicationNotificationTemplateResource> result = service.getUnsuccessfulNotificationTemplate(competitionId);

        assertTrue(result.isSuccess());
        assertEquals("MessageBody", result.getSuccess().getMessageBody());
    }

    @Test
    public void getIneligibleNotificationTemplate() {
        long competitionId = 1L;
        Competition competition = newCompetition().withName("Competition").build();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("competitionName", competition.getName());

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(renderer.renderTemplate(eq(systemNotificationSource), any(),
                eq(DEFAULT_NOTIFICATION_TEMPLATES_PATH + "ineligible_application.html"), eq(arguments)))
                .thenReturn(serviceSuccess("MessageBody"));

        ServiceResult<ApplicationNotificationTemplateResource> result = service.getIneligibleNotificationTemplate(competitionId);

        assertTrue(result.isSuccess());
        assertEquals("MessageBody", result.getSuccess().getMessageBody());
    }

}