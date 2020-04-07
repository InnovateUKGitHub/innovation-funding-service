package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
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


    @Override
    @Transactional
    public ServiceResult<OrganisationResource> createOrMatch(OrganisationResource organisationToCreate) {
        Optional<Organisation> matchedOrganisation =
                organisationMatchingService.findOrganisationMatch(organisationToCreate);

        Organisation resultingOrganisation =
                matchedOrganisation.orElseGet(() -> createNewOrganisation(organisationToCreate));

        return serviceSuccess(organisationMapper.mapToResource(resultingOrganisation));
    }

    private Organisation createNewOrganisation(OrganisationResource organisationResource) {
        Organisation mappedOrganisation = organisationMapper.mapToDomain(organisationResource);
        return organisationRepository.save(mappedOrganisation);
    }
}
