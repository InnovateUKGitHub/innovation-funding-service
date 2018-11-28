package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Determines if a registering user has to become part of an already existing organisation
 * Companies House or Je-s organisations on the basis of specific organisation details.
 */
@Service
public class OrganisationMatchingServiceImpl implements OrganisationMatchingService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationPatternMatcher organisationPatternMatcher;

    public Optional<Organisation> findOrganisationMatch(OrganisationResource submittedOrganisationResource) {
        if (OrganisationTypeEnum.isResearch(submittedOrganisationResource.getOrganisationType())) {
            return findFirstResearchMatch(submittedOrganisationResource);
        } else {
            return findFirstCompaniesHouseMatch(submittedOrganisationResource);
        }
    }

    private Optional<Organisation> findFirstCompaniesHouseMatch(OrganisationResource submittedOrganisationResource) {
        if (isNullOrEmpty(submittedOrganisationResource.getCompaniesHouseNumber())) {
            return Optional.empty();
        }
        return findOrganisationByCompaniesHouseId(submittedOrganisationResource).stream()
                .filter(foundOrganisation -> organisationPatternMatcher.organisationTypeMatches(
                        foundOrganisation,
                        submittedOrganisationResource
                ))
                .findFirst();
    }

    private Optional<Organisation> findFirstResearchMatch(OrganisationResource submittedOrganisationResource) {
        return findOrganisationByName(submittedOrganisationResource).stream()
                .filter(foundOrganisation -> organisationPatternMatcher.organisationTypeIsResearch(foundOrganisation))
                .findFirst();
    }

    private List<Organisation> findOrganisationByName(OrganisationResource organisationResource) {
        return organisationRepository.findByNameOrderById(organisationResource.getName());
    }

    private List<Organisation> findOrganisationByCompaniesHouseId(OrganisationResource organisationResource) {
        return organisationRepository.findByCompaniesHouseNumberOrderById(organisationResource.getCompaniesHouseNumber());
    }
}
