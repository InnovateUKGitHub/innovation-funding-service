package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingServiceImpl;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Transactional service responsible for deciding if and how to create organisations upon user sign up.
 */
@Service
public class OrganisationInitialCreationServiceImpl extends BaseTransactionalService implements OrganisationInitialCreationService {

    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private OrganisationMatchingServiceImpl organisationMatchingService;

    @Autowired
    private ApplicationInviteService applicationInviteService;

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> createOrMatch(OrganisationResource organisationToCreate) {
        Optional<Organisation> matchedOrganisation =
                organisationMatchingService.findOrganisationMatch(organisationToCreate);

        Organisation resultingOrganisation =
                matchedOrganisation.orElseGet(() -> createNewOrganisation(organisationToCreate));

        return serviceSuccess(organisationMapper.mapToResource(resultingOrganisation));
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> createAndLinkByInvite(
            OrganisationResource organisationToCreate,
            String inviteHash
    ) {
        ApplicationInvite invite = applicationInviteService.findOneByHash(inviteHash).getSuccess();

        InviteOrganisation inviteOrganisation = invite.getInviteOrganisation();
        Organisation linkedOrganisation = invite.getInviteOrganisation().getOrganisation();

        if (linkedOrganisation == null) {
            linkedOrganisation = organisationMatchingService.findOrganisationMatch(organisationToCreate)
                    .filter(match -> isNotExistingCollaboratorOrLeadOrganisation(match, invite))
                    .map(match -> linkOrganisation(invite.getInviteOrganisation(), match).getOrganisation())
                    .orElseGet(() -> createOrganisationAndLinkToInviteOrganisation(
                            organisationToCreate,
                            inviteOrganisation
                    ));
        }

        return serviceSuccess(organisationMapper.mapToResource(linkedOrganisation));
    }

    /**
     * This may seem counter-intuitive, but we need to avoid a
     * new collaborator being able to add themselves to any other
     * existing organisations that are collaborating on the
     * application or even the lead organisation.
     *
     * We should only link the {@link ApplicationInvite#inviteOrganisation}
     * to the matched organisation if neither of these criteria
     * are met. Otherwise we create a new organisation and link
     * that instead.
     */
    private boolean isNotExistingCollaboratorOrLeadOrganisation(
            Organisation organisationMatch,
            ApplicationInvite invite
    ) {
        // Check the lead organisation directly from the
        // `ApplicationInvite` as this information won't exist
        // in an `InviteOrganisation`.
        if (organisationMatch.getId().equals(invite.getTarget().getLeadOrganisationId())) {
            return false;
        }

        Optional<InviteOrganisation> existingCollaboratorInviteOrganisation =
                inviteOrganisationRepository.findFirstByOrganisationIdAndInvitesApplicationId(
                        organisationMatch.getId(),
                        invite.getTarget().getId()
                );

        return !existingCollaboratorInviteOrganisation.isPresent();
    }

    private Organisation createOrganisationAndLinkToInviteOrganisation(
            OrganisationResource organisationResource,
            InviteOrganisation inviteOrganisation
    ) {
        Organisation createdOrganisation = createNewOrganisation(organisationResource);
        return linkOrganisation(inviteOrganisation, createdOrganisation).getOrganisation();
    }

    private InviteOrganisation linkOrganisation(
            InviteOrganisation inviteOrganisation,
            Organisation organisation
    ) {
        inviteOrganisation.setOrganisation(organisation);
        return inviteOrganisationRepository.save(inviteOrganisation);
    }

    private Organisation createNewOrganisation(OrganisationResource organisationResource) {
        Organisation mappedOrganisation = organisationMapper.mapToDomain(organisationResource);

        //Add organisation to addresses to persist reference
        mappedOrganisation.getAddresses().forEach(address -> address.setOrganisation(mappedOrganisation));

        return organisationRepository.save(mappedOrganisation);
    }
}
