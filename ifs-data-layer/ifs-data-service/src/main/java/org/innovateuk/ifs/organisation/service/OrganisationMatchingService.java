package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Determines if a registering user has to become part of an already existing organisation Companies House or Je-s organisations by specific organisation details.
 */
@Service
public class OrganisationMatchingService {

    @Autowired
    private OrganisationRepository organisationRepository;

    public ServiceResult<Organisation> findOrganisationMatch(OrganisationResource organisationResource) {
        if(organisationResource.getOrganisationType().equals(OrganisationTypeEnum.RESEARCH)) {
            return findOrganisationMatchByJesNameAndAddressDetails(organisationResource);
        }
        else {
            return findOrganisationMatchByCompaniesHouseAndAddressDetails(organisationResource);
        }
    }

    public ServiceResult<Organisation> findOrganisationMatchByCompaniesHouseAndAddressDetails(OrganisationResource organisationResource) {
        //Companies House organisation matching routine - to be finished
        return find(organisationRepository.findOneByName(organisationResource.getName()), notFoundError(Organisation.class, organisationResource));
    }

    public ServiceResult<Organisation> findOrganisationMatchByJesNameAndAddressDetails(OrganisationResource organisationResource) {
        //Je-s acadamic organisation matching routine - not implemented
        //TODO: IFS-1325
        return serviceFailure(notFoundError(OrganisationResource.class, organisationResource));
    }
}
