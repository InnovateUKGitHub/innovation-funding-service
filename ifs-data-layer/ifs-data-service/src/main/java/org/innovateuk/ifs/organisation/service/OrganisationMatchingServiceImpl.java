package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Determines if a registering user has to become part of an already existing organisation Companies House or Je-s organisations on the basis of specific organisation details.
 */
@Service
public class OrganisationMatchingServiceImpl implements OrganisationMatchingService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationPatternMatcher organisationPatternMatcher;

    public Optional<Organisation> findOrganisationMatch(OrganisationResource submittedOrganisationResource) {
        if(OrganisationTypeEnum.isResearch(submittedOrganisationResource.getOrganisationType())) {
            return findFirstCompaniesHouseMatch(submittedOrganisationResource);
        } else {
            return findFirstResearchMatch(submittedOrganisationResource);
        }
    }

    private Optional<Organisation> findFirstResearchMatch(OrganisationResource submittedOrganisationResource) {
        return findOrganisationByCompaniesHouseId(submittedOrganisationResource).stream()
                .filter(foundOrganisation -> organisationPatternMatcher.organisationTypeMatches(foundOrganisation, submittedOrganisationResource))
                .filter(foundOrganisation -> organisationPatternMatcher.organisationAddressMatches(foundOrganisation, submittedOrganisationResource, OrganisationAddressType.OPERATING, false))
                .filter(foundOrganisation -> organisationPatternMatcher.organisationAddressMatches(foundOrganisation, submittedOrganisationResource, OrganisationAddressType.REGISTERED, true))
                .findFirst();
    }

    private Optional<Organisation> findFirstCompaniesHouseMatch(OrganisationResource submittedOrganisationResource) {
        return findOrganisationByName(submittedOrganisationResource).stream()
                .filter(foundOrganisation -> organisationPatternMatcher.organisationTypeIsResearch(foundOrganisation))
                .filter(foundOrganisation -> organisationPatternMatcher.organisationAddressMatches(foundOrganisation, submittedOrganisationResource, OrganisationAddressType.OPERATING, true))
                .findFirst();
    }

    private List<Organisation> findOrganisationByName(OrganisationResource organisationResource) {
        return organisationRepository.findByNameOrderById(organisationResource.getName());
    }

    private List<Organisation> findOrganisationByCompaniesHouseId(OrganisationResource organisationResource) {
        return organisationRepository.findByCompanyHouseNumberOrderById(organisationResource.getCompanyHouseNumber());
    }
}
