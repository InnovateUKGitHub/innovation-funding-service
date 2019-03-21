package org.innovateuk.ifs.profile.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.AffiliationRepository;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Service
public class DoiExpiryServiceImpl extends BaseTransactionalService implements DoiExpiryService {
    private static final DateTimeFormatter formatter = ofPattern("d MMMM yyyy");
    private static final Log LOG = LogFactory.getLog(DoiExpiryServiceImpl.class);

    @Autowired
    private AffiliationRepository affiliationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    @Transactional
    public void notifyExpiredDoi() {
        ZonedDateTime expiry = Profile.startOfCurrentFinancialYear(ZonedDateTime.now()).atStartOfDay(ZoneId.systemDefault());

        Page<User> expired = affiliationRepository.findUserToBeNotifiedOfExpiry(expiry, PageRequest.of(0, 1));

        expired.getContent().stream().findFirst().ifPresent(this::notifyExpiry);

    }

    private void notifyExpiry(User user) {
        LOG.info(String.format("Notifying user of DOI expiry %d", user.getId()));
        NotificationTarget recipient = new UserNotificationTarget(
                user.getName(),
                user.getEmail()
        );

        Notification notification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                Notifications.ASSESSOR_AFFILIATION_EXPIRED,
                asMap("webBaseUrl", webBaseUrl,
                        "affiliationModifiedDate", user.getAffiliations().stream().findFirst().get().getModifiedOn().format(formatter))
        );

        notificationService.sendNotificationWithFlush(notification, EMAIL);

        profileRepository.findById(user.getProfileId()).get().setDoiNotifiedOn(ZonedDateTime.now());
    }

    enum Notifications {
        ASSESSOR_AFFILIATION_EXPIRED
    }
}