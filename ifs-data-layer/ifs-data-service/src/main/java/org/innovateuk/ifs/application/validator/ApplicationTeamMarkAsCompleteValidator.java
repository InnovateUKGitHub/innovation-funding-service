package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.innovateuk.ifs.user.resource.EDIStatus;
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
@Slf4j
@Component
public class ApplicationTeamMarkAsCompleteValidator implements Validator {

    @Autowired
    private ApplicationInviteService applicationInviteService;

    @Autowired
    private ApplicationKtaInviteService applicationKtaInviteService;

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        log.debug("do ApplicationTeamMarkAsComplete Validation");

        Application application = (Application) target;
        if (application.getLeadApplicant().getEdiStatus() == null) {
            reject(errors, "validation.applicationteam.edi.status", application.getLeadOrganisationId());
        } else if (application.getLeadApplicant().getEdiStatus().equals(EDIStatus.INCOMPLETE)) {
            reject(errors, "validation.applicationteam.edi.status", application.getLeadOrganisationId());
        }

        List<InviteOrganisationResource> invites = applicationInviteService.getInvitesByApplication(application.getId()).getSuccess();
        for (InviteOrganisationResource organisation : invites) {
            Optional<ApplicationInviteResource> maybeInvite = organisation.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus() != OPENED)
                    .findFirst();
            if (maybeInvite.isPresent()) {
                reject(errors, "validation.applicationteam.pending.invites", maybeInvite.get().getName(), organisation.getId());
            }
        }

        if (application.getCompetition().isKtp() &&
            application.getProcessRoles().stream().noneMatch(pr -> pr.getRole().isKta())) {

            ApplicationKtaInviteResource ktaInvite = applicationKtaInviteService.getKtaInviteByApplication(application.getId()).getSuccess();
            if (ktaInvite == null) {
                reject(errors, "validation.kta.missing.invite");
            } else {
                if (ktaInvite.getStatus() != InviteStatus.OPENED) {
                    reject(errors, "validation.kta.pending.invite");
                }
            }
        }


    }

}
