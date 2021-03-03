package org.innovateuk.ifs.profile.transactional;

import com.newrelic.api.agent.Trace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.AffiliationRepository;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Service
public class DoiExpiryServiceImpl extends BaseTransactionalService implements DoiExpiryService {
    private static final DateTimeFormatter formatter = ofPattern("d MMMM yyyy");
    private static final Log LOG = LogFactory.getLog(DoiExpiryServiceImpl.class);

    private final AffiliationRepository affiliationRepository;

    private final ProfileRepository profileRepository;

    private final NotificationService notificationService;

    private final SystemNotificationSource systemNotificationSource;

    private final String webBaseUrl;

    public DoiExpiryServiceImpl(AffiliationRepository affiliationRepository,
                                ProfileRepository profileRepository,
                                NotificationService notificationService,
                                SystemNotificationSource systemNotificationSource,
                                @Value("${ifs.web.baseURL}") String webBaseUrl) {
        this.affiliationRepository = affiliationRepository;
        this.profileRepository = profileRepository;
        this.notificationService = notificationService;
        this.systemNotificationSource = systemNotificationSource;
        this.webBaseUrl = webBaseUrl;
    }

    @Override
    @Transactional
    @Trace(dispatcher = true)
    public ServiceResult<ScheduleResponse> notifyExpiredDoi() {
        ZonedDateTime expiry = Profile.startOfCurrentFinancialYear(ZonedDateTime.now()).atStartOfDay(ZoneId.systemDefault());

        Page<User> expired = affiliationRepository.findUserToBeNotifiedOfExpiry(expiry, PageRequest.of(0, 1));

        return ServiceResult.serviceSuccess(expired.getContent().stream().findFirst().map(this::notifyExpiry).orElse(ScheduleResponse.noWorkNeeded()));

    }

    private ScheduleResponse notifyExpiry(User user) {
        LOG.info(String.format("Notifying user of DOI expiry %d", user.getId()));
        NotificationTarget recipient = new UserNotificationTarget(
                user.getName(),
                user.getEmail()
        );

        Notification notification = new Notification(
                systemNotificationSource,
                recipient,
                Notifications.ASSESSOR_AFFILIATION_EXPIRED,
                asMap("webBaseUrl", webBaseUrl,
                        "affiliationModifiedDate", user.getAffiliations().stream().findFirst().get().getModifiedOn().format(formatter))
        );

        notificationService.sendNotificationWithFlush(notification, EMAIL).getSuccess();

        profileRepository.findById(user.getProfileId()).get().setDoiNotifiedOn(ZonedDateTime.now());
        return new ScheduleResponse("Notified users DOI " + user.getId());
    }

    enum Notifications {
        ASSESSOR_AFFILIATION_EXPIRED
    }
}