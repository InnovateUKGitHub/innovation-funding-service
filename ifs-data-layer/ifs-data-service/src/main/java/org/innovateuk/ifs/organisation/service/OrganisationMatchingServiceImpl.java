package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Determines if a registering user has to become part of an already existing organisation Companies House or Je-s organisations on the basis of specific organisation details.
 */
@Service
public class OrganisationMatchingServiceImpl implements OrganisationMatchingService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationPatternMatcher organisationPatternMatcher;

    public Optional<Organisation> findOrganisationMatch(OrganisationResource organisationResource) {
        if(OrganisationTypeEnum.isResearch(organisationResource.getOrganisationType())) {
            return findOrganisationByName(organisationResource).stream()
                    .filter(foundOrganisation -> organisationPatternMatcher.organisationTypeIsResearch(foundOrganisation))
                    .filter(foundOrganisation -> organisationPatternMatcher.organisationAddressMatches(foundOrganisation, organisationResource, OrganisationAddressType.OPERATING, true))
                    .findFirst();
        } else {
            return findOrganisationByCompaniesHouseId(organisationResource).stream()
                    .filter(foundOrganisation -> organisationPatternMatcher.organisationTypeMatches(foundOrganisation, organisationResource))
                    .filter(foundOrganisation -> organisationPatternMatcher.organisationAddressMatches(foundOrganisation, organisationResource, OrganisationAddressType.OPERATING, false))
                    .filter(foundOrganisation -> organisationPatternMatcher.organisationAddressMatches(foundOrganisation, organisationResource, OrganisationAddressType.REGISTERED, true))
                    .findFirst();
        }
    }

    private List<Organisation> findOrganisationByName(OrganisationResource organisationResource) {
        return find(organisationRepository.findByName(organisationResource.getName()), notFoundError(OrganisationResource.class, organisationResource)).getOrElse(Collections.emptyList());
    }

    private List<Organisation> findOrganisationByCompaniesHouseId(OrganisationResource organisationResource) {
        return find(organisationRepository.findByCompanyHouseNumber(organisationResource.getCompanyHouseNumber()), notFoundError(OrganisationResource.class, organisationResource)).getOrElse(Collections.emptyList());
    }
}
