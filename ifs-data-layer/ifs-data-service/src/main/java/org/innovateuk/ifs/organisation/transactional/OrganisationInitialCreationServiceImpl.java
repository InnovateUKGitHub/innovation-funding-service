package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingServiceImpl;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
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
    private InviteService inviteService;

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
        ApplicationInvite invite = inviteService.findOneByHash(inviteHash).getSuccessObjectOrThrowException();

        InviteOrganisation inviteOrganisation = invite.getInviteOrganisation();
        Organisation linkedOrganisation = invite.getInviteOrganisation().getOrganisation();

        if (linkedOrganisation == null) {
            linkedOrganisation = organisationMatchingService.findOrganisationMatch(organisationToCreate)
                    .flatMap(match -> linkOrganisationIfNotExistingOrLeadOrganisation(match, invite))
                    .orElseGet(() -> createOrganisationAndLinkToInviteOrganisation(
                            organisationToCreate,
                            inviteOrganisation
                    ));
        }

        return serviceSuccess(organisationMapper.mapToResource(linkedOrganisation));
    }

    private Optional<Organisation> linkOrganisationIfNotExistingOrLeadOrganisation(
            Organisation organisationMatch,
            ApplicationInvite invite
    ) {
        if (organisationMatch.getId().equals(invite.getTarget().getLeadOrganisationId())) {
            return Optional.empty();
        }

        Optional<InviteOrganisation> existingInvite =
                inviteOrganisationRepository.findFirstByOrganisationIdAndInvitesApplicationId(
                        organisationMatch.getId(),
                        invite.getTarget().getId()
                );

        if (existingInvite.isPresent()) {
            // This seems counter-intuitive, but for now we need to avoid a
            // new collaborator being able to add themselves to any existing
            // application organisations (including the lead applicant's).
            // Consequently, if there is an existing matching organisation,
            // we just ignore it for now and create a new organisation.
            return Optional.empty();
        }

        return Optional.of(linkOrganisation(invite.getInviteOrganisation(), organisationMatch).getOrganisation());
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
