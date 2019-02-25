package org.innovateuk.ifs.euinvite;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eucontact.repository.EuContactRepository;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuFunding;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.domain.EuOrganisation;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;

@Service
public class EuInviteServiceImpl implements EuInviteService {

    @Autowired
    private EuContactRepository euContactRepository;

    @Autowired
    EuGrantRepository euGrantRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Override
    @Transactional
    public ServiceResult<Void> sendInvites(List<Long> euContactIds) {
        euContactIds
                .forEach(id -> sendInvite(id)
                        .andOnSuccessReturnVoid(euContact -> euContact.setNotified(true))
                );

        return serviceSuccess();
    }

    private ServiceResult<EuContact> sendInvite(long euContactId) {

        EuContact euContact = euContactRepository.getById(euContactId);
        EuGrant euGrant = euGrantRepository.getByContact(euContact);
        EuFunding euFunding = euGrant.getFunding();
        EuOrganisation euOrganisation = euGrant.getOrganisation();
        NotificationTarget recipient = new UserNotificationTarget(
                euContact.getName(),
                euContact.getEmail()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("referenceCode", euGrant.getShortCode());
        notificationArguments.put("organisationType", euOrganisation.getOrganisationType());
        notificationArguments.put("registeredName", euOrganisation.getName());
        notificationArguments.put("registrationNumber", euOrganisation.getCompaniesHouseNumber());
        notificationArguments.put("fullName", euContact.getName());
        notificationArguments.put("jobTitle", euContact.getJobTitle());
        notificationArguments.put("email", euContact.getEmail());
        notificationArguments.put("telephone", euContact.getTelephone());
        notificationArguments.put("grantAgreementNumber", euFunding.getGrantAgreementNumber());
        notificationArguments.put("actionType", euFunding.getActionType().getName());
        notificationArguments.put("participationIdentificationCode", euFunding.getParticipantId());
        notificationArguments.put("projectName", euFunding.getProjectName());
        notificationArguments.put("startDate", euFunding.getProjectStartDate().format(formatter));
        notificationArguments.put("endDate", euFunding.getProjectEndDate().format(formatter));
        notificationArguments.put("fundingAmount", euFunding.getFundingContribution());
        notificationArguments.put("inviteUrl", "http://change.me");

        Notification notification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                Notifications.INVITE_EU_REGISTRANT,
                notificationArguments
        );

        return notificationService.sendNotificationWithFlush(notification, EMAIL)
                .andOnSuccessReturn(() -> euContact);
    }

    private enum Notifications {
        INVITE_EU_REGISTRANT
    }
}
