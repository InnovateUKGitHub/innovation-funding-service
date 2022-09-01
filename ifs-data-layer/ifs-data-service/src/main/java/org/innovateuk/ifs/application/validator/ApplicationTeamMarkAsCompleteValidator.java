package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.innovateuk.ifs.user.resource.EDIStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.ValidationMessages.reject;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;

/**
 * Validates the inputs in the application team page, if valid on the markAsComplete action
 */
@Slf4j
@Component
@Scope(value="prototype", proxyMode= ScopedProxyMode.TARGET_CLASS)  //TODO- Remove this when feature toogle for EDI removed
public class ApplicationTeamMarkAsCompleteValidator implements Validator {

    @Autowired
    private ApplicationInviteService applicationInviteService;

    @Autowired
    private ApplicationKtaInviteService applicationKtaInviteService;

    @Autowired
    private CompetitionService competitionService;

    @Value("${ifs.edi.update.enabled}")
    private boolean isEDIUpdateEnabled;

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        log.debug("do ApplicationTeamMarkAsComplete Validation");

        Application application = (Application) target;

        List<InviteOrganisationResource> invites = applicationInviteService.getInvitesByApplication(application.getId()).getSuccess();
        for (InviteOrganisationResource organisation : invites) {
            Optional<ApplicationInviteResource> maybeInvite = organisation.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus() != OPENED)
                    .findFirst();
            maybeInvite.ifPresent(applicationInviteResource -> reject(errors, "validation.applicationteam.pending.invites", applicationInviteResource.getName(), organisation.getId()));
        }

        if (application.getCompetition().isKtpOnly() &&
                application.getProcessRoles().stream().noneMatch(pr -> pr.getRole().isKta())) {

            ApplicationKtaInviteResource ktaInvite = getKtpInviteByApplication(application);

            if (ktaInvite == null) {
                reject(errors, "validation.kta.missing.invite");
            } else {
                ktpInviteStatusIsNotOpened(errors, ktaInvite);
            }
        }

        if (application.getCompetition().isKtpAkt() &&
                application.getProcessRoles().stream().noneMatch(pr -> pr.getRole().isKta())) {

            ApplicationKtaInviteResource ktaInvite = getKtpInviteByApplication(application);

            if (ktaInvite != null) {
                ktpInviteStatusIsNotOpened(errors, ktaInvite);
            }
        }

        boolean hasEDIQuestions = competitionService.hasEDIQuestion(application.getCompetition().getId()).getSuccess();
        isEDIUpdateEnabled = isEDIUpdateEnabled && !hasEDIQuestions;
        if (isEDIUpdateEnabled) {
            validateLeadEDIStatus(errors, application);
        }

    }

    private void ktpInviteStatusIsNotOpened(Errors errors, ApplicationKtaInviteResource ktaInvite) {
        if (ktaInvite.getStatus() != InviteStatus.OPENED) {
            reject(errors, "validation.kta.pending.invite");
        }
    }

    private ApplicationKtaInviteResource getKtpInviteByApplication(Application application) {
        return applicationKtaInviteService.getKtaInviteByApplication(application.getId()).getSuccess();
    }

    private void validateLeadEDIStatus(Errors errors, Application application) {
        EDIStatus ediStatus = application.getLeadApplicant().getEdiStatus();
        if (ediStatus == null || !ediStatus.equals(EDIStatus.COMPLETE)) {
            reject(errors, "validation.applicationteam.edi.status", application.getLeadApplicant().getName(), application.getLeadOrganisationId());
        }
    }
}
