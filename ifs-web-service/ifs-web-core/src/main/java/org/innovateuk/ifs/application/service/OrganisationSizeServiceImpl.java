package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.service.OrganisationDetailsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link org.innovateuk.ifs.finance.resource.OrganisationSizeResource} related data,
 * through the RestService {@link OrganisationDetailsRestService}.
 */
@Service
public class OrganisationSizeServiceImpl implements OrganisationSizeService {

    @Autowired
    private OrganisationDetailsRestService organisationDetailsRestService;
    
    @Override
    public List<OrganisationSizeResource> getOrganisationSizes() {
        return organisationDetailsRestService.getOrganisationSizes().getSuccessObjectOrThrowException();
    }
}
