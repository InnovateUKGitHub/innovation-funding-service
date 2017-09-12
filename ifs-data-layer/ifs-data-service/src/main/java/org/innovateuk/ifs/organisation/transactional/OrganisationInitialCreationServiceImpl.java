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
    private InviteOrganisationRepository inviteOrganisationRespository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> createOrMatch(final OrganisationResource organisationToCreate) {
        Optional<Organisation> matchedOrganisation = organisationMatchingService.findOrganisationMatch(organisationToCreate);
        Organisation resultingOrganisation;

        if (matchedOrganisation.isPresent()) {
            resultingOrganisation = matchedOrganisation.get();
        } else {
            resultingOrganisation = createNewOrganisation(organisationToCreate);
        }

        return serviceSuccess(organisationMapper.mapToResource(resultingOrganisation));
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> createAndLinkByInvite(final OrganisationResource organisationToCreate, String inviteHash) {
        ApplicationInvite invite = inviteService.findOneByHash(inviteHash).getSuccessObjectOrThrowException();

        InviteOrganisation foundInviteOrganisation = invite.getInviteOrganisation();
        Organisation linkedOrganisation = invite.getInviteOrganisation().getOrganisation();

        if (linkedOrganisation == null) {
            linkedOrganisation = createOrganisationAndLinkToInviteOrganisation(organisationToCreate, foundInviteOrganisation);
        }

        return serviceSuccess(organisationMapper.mapToResource(linkedOrganisation));
    }

    private Organisation createOrganisationAndLinkToInviteOrganisation(OrganisationResource organisationResource, InviteOrganisation inviteOrganisation) {
        Organisation createdOrganisation = createNewOrganisation(organisationResource);

        inviteOrganisation.setOrganisation(createdOrganisation);
        inviteOrganisationRespository.save(inviteOrganisation);

        return createdOrganisation;
    }

    private Organisation createNewOrganisation(OrganisationResource organisationResource) {
        Organisation mappedOrganisation = organisationMapper.mapToDomain(organisationResource);

        //Add organisation to addresses to persist reference
        mappedOrganisation.getAddresses().stream().forEach(address -> address.setOrganisation(mappedOrganisation));

        return organisationRepository.save(mappedOrganisation);
    }
}
