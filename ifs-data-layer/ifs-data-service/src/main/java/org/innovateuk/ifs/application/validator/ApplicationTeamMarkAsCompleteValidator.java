package org.innovateuk.ifs.application.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.ValidationMessages.reject;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;

/**
 * Validates the inputs in the application team page, if valid on the markAsComplete action
 *
 */
 @Component
 public class ApplicationTeamMarkAsCompleteValidator implements Validator {

    private static final Log LOG = LogFactory.getLog(ApplicationTeamMarkAsCompleteValidator.class);

    @Autowired
    private ApplicationInviteService applicationInviteService;

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        LOG.debug("do ApplicationTeamMarkAsComplete Validation");

        Application application = (Application) target;

        List<InviteOrganisationResource> invites = applicationInviteService.getInvitesByApplication(application.getId()).getSuccess();
        for (InviteOrganisationResource organisation : invites) {
            Optional<ApplicationInviteResource> maybeInvite = organisation.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus() != OPENED)
                    .findFirst();
            if (maybeInvite.isPresent()) {
                LOG.debug("MarkAsComplete application team validation message for invite organisation: " + organisation.getOrganisationName());
                reject(errors, "validation.applicationteam.pending.invites", maybeInvite.get().getName(), organisation.getId());
            }
        }

        if (application.getCompetition().isKtp()) {
            List<ApplicationKtaInviteResource> ktaInvites = applicationInviteService.getKtaInvitesByApplication(application.getId()).getSuccess();
            if (ktaInvites.isEmpty()) {
                reject(errors, "validation.kta.missing.invite");
            } else {
                if (ktaInvites.stream().anyMatch(invite -> invite.getStatus() != InviteStatus.OPENED)) {
                    LOG.debug("MarkAsComplete kta validation message");
                    reject(errors, "validation.kta.pending.invite");
                }
            }
        }


    }

}
