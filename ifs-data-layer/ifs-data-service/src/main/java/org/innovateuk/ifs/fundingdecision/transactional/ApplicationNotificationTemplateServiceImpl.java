package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.DEFAULT_NOTIFICATION_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

@Service
public class ApplicationNotificationTemplateServiceImpl extends BaseTransactionalService implements ApplicationNotificationTemplateService {

    private static final DateTimeFormatter formatter = ofPattern("d MMMM yyyy");

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<ApplicationNotificationTemplateResource> getSuccessfulNotificationTemplate(long competitionId) {
        return renderTemplate(competitionId, "successful_funding_decision.html", (competition) -> {
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("competitionName", competition.getName());
            arguments.put("dashboardUrl", webBaseUrl);
            arguments.put("feedbackDate", toUkTimeZone(competition.getReleaseFeedbackDate()).format(formatter));
            return arguments;
        });
    }

    @Override
    public ServiceResult<ApplicationNotificationTemplateResource> getUnsuccessfulNotificationTemplate(long competitionId) {
        return renderTemplate(competitionId, "unsuccessful_funding_decision.html", (competition) -> {
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("competitionName", competition.getName());
            arguments.put("dashboardUrl", webBaseUrl);
            arguments.put("feedbackDate", toUkTimeZone(competition.getReleaseFeedbackDate()).format(formatter));
            return arguments;
        });
    }

    @Override
    public ServiceResult<ApplicationNotificationTemplateResource> getIneligibleNotificationTemplate(long competitionId) {
        return renderTemplate(competitionId, "ineligible_application.html", (competition) -> {
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("competitionName", competition.getName());
            return arguments;
        });
    }

    private ServiceResult<ApplicationNotificationTemplateResource> renderTemplate(long competitionId, String template, Function<Competition, Map<String, Object>> argumentFunction) {
        return getCompetition(competitionId).andOnSuccess(competition -> {

            NotificationTarget notificationTarget = new UserNotificationTarget("", "");
            Map<String, Object> arguments = argumentFunction.apply(competition);

            return renderer.renderTemplate(systemNotificationSource, notificationTarget,
                    DEFAULT_NOTIFICATION_TEMPLATES_PATH + template, arguments);
        })
        .andOnSuccessReturn(this::replaceNewline)
        .andOnSuccessReturn(this::toResource);
    }

    private String replaceNewline(String html) {
        return html.replaceAll("\n", "");
    }

    private ApplicationNotificationTemplateResource toResource(String content) {
        return new ApplicationNotificationTemplateResource(content);
    }
}