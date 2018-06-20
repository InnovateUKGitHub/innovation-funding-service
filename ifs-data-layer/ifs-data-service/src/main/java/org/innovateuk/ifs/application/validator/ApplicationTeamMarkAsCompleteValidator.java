package org.innovateuk.ifs.application.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

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

        ServiceResult<List<InviteOrganisationResource>> invitesResult = applicationInviteService.getInvitesByApplication(application.getId());
        List<InviteOrganisationResource> invites = invitesResult.getSuccess();
        for (InviteOrganisationResource organisation : invites) {
            if (organisation.getInviteResources()
                    .stream()
                    .anyMatch(invite -> invite.getStatus() != OPENED)) {
                LOG.debug("MarkAsComplete application team validation message for invite organisation: " + organisation.getOrganisationName());
                reject(errors, "validation.applicationteam.pending.invites", new Object[] {organisation.getId()});
            }
        }
    }

}
