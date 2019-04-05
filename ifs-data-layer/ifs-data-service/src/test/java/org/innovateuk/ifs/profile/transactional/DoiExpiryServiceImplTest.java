package org.innovateuk.ifs.profile.transactional;

import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.AffiliationRepository;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class DoiExpiryServiceImplTest {

    @InjectMocks
    private DoiExpiryServiceImpl doiExpiryService;

    @Mock
    private AffiliationRepository affiliationRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    private final static String webBaseUrl = "BaseUrl";

    @Before
    public void setup() {
        setField(doiExpiryService, "webBaseUrl", webBaseUrl);
    }

    @Test
    public void notifyExpiredDoi() {
        long profileId = 5L;
        ZonedDateTime expiry = Profile.startOfCurrentFinancialYear(ZonedDateTime.now()).atStartOfDay(ZoneId.systemDefault());
        User userToNotify = newUser()
                .withProfileId(profileId)
                .withEmailAddress("someone@example.com")
                .withFirstName("Someone")
                .withLastName("Smith")
                .withAffiliations(newAffiliation().withModifiedOn(ZonedDateTime.of(2019, 4, 6, 0, 0, 0, 0, ZoneId.systemDefault())).build(1))
                .build();
        Profile profile = newProfile().build();
        when(affiliationRepository.findUserToBeNotifiedOfExpiry(expiry, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(singletonList(userToNotify)));
        when(notificationService.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess());
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        doiExpiryService.notifyExpiredDoi();

        ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        verify(notificationService).sendNotificationWithFlush(argumentCaptor.capture(), eq(EMAIL));
        Notification notification = argumentCaptor.getValue();
        assertEquals(notification.getTo().size(), 1);
        assertEquals(notification.getTo().get(0).getEmailAddress(), userToNotify.getEmail());
        assertEquals(notification.getTo().get(0).getName(), userToNotify.getName());
        assertEquals(notification.getMessageKey().name(), "ASSESSOR_AFFILIATION_EXPIRED");
        assertEquals(notification.getGlobalArguments().get("webBaseUrl"), webBaseUrl);
        assertEquals(notification.getGlobalArguments().get("affiliationModifiedDate"), "6 April 2019");
        assertEquals(notification.getFrom(), systemNotificationSource);

        assertNotNull(profile.getDoiNotifiedOn());
    }




}