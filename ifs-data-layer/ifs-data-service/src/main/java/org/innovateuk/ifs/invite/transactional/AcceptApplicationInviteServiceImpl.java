package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

/**
 * Service for accepting collaborator's {@link ApplicationInvite}s
 * and initialising their state on the application correctly.
 */
@Service
public class AcceptApplicationInviteServiceImpl extends InviteService<ApplicationInvite> implements AcceptApplicationInviteService {

    private static final Logger LOG = LoggerFactory.getLogger(AcceptApplicationInviteServiceImpl.class);

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Override
    protected Class<ApplicationInvite> getInviteClass() {
        return ApplicationInvite.class;
    }

    @Override
    protected InviteRepository<ApplicationInvite> getInviteRepository() {
        return applicationInviteRepository;
    }

    @Override
    @Transactional
    public ServiceResult<Void> acceptInvite(String inviteHash, Long userId) {
        return find(invite(inviteHash), user(userId)).andOnSuccess((invite, user) -> {
            if (!invite.getEmail().equalsIgnoreCase(user.getEmail())) {
                LOG.error(
                        "Invite (id: {}) email address does not match user's (id: {})",
                        invite.getId(),
                        user.getId()
                );

                return serviceFailure(new Error(
                        "Invite email address not the same as the user's email address",
                        NOT_ACCEPTABLE
                ));
            }

            invite.open();

            if (invite.getInviteOrganisation().getOrganisation() == null) {
                // A bit contentious, but we assume that the first organisation
                // that a user belongs to is their 'current' organisation.
                organisationRepository.findFirstByUsers(user)
                        .ifPresent(organisation -> assignOrganisationToInvite(invite, organisation));
            }

            initializeInvitee(invite, user);

            return serviceSuccess();
        });
    }

    private void assignOrganisationToInvite(ApplicationInvite invite, Organisation organisation) {
        Optional<InviteOrganisation> existingCollaboratorInviteOrganisation =
                inviteOrganisationRepository.findFirstByOrganisationIdAndInvitesApplicationId(
                        organisation.getId(),
                        invite.getTarget().getId()
                );

        if (existingCollaboratorInviteOrganisation.isPresent()) {
            replaceInviteOrganisationOnInvite(invite, existingCollaboratorInviteOrganisation.get());
        } else {
            invite.getInviteOrganisation().setOrganisation(organisation);
        }
    }

    private void replaceInviteOrganisationOnInvite(
            ApplicationInvite invite,
            InviteOrganisation newInviteOrganisation
    ) {
        unlinkOldInviteOrganisation(invite);

        invite.setInviteOrganisation(newInviteOrganisation);
    }

    private void unlinkOldInviteOrganisation(ApplicationInvite invite) {
        InviteOrganisation currentInviteOrganisation = invite.getInviteOrganisation();
        currentInviteOrganisation.removeInvite(invite);
        inviteOrganisationRepository.saveAndFlush(currentInviteOrganisation);

        if (currentInviteOrganisation.getInvites().isEmpty()) {
            inviteOrganisationRepository.delete(currentInviteOrganisation);
        }
    }

    private void initializeInvitee(ApplicationInvite invite, User user) {
        Application application = invite.getTarget();
        Organisation organisation = invite.getInviteOrganisation().getOrganisation();

        ProcessRole processRole = new ProcessRole(user, application.getId(), Role.COLLABORATOR, organisation.getId());
        processRoleRepository.save(processRole);
        application.addProcessRole(processRole);

        applicationProgressService.updateApplicationProgress(application.getId());
    }
}
