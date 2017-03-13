package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.service.OrganisationSizeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link org.innovateuk.ifs.finance.resource.OrganisationSizeResource} related data,
 * through the RestService {@link OrganisationSizeRestService}.
 */
@Service
public class OrganisationSizeServiceImpl implements OrganisationSizeService {

    @Autowired
    private OrganisationSizeRestService organisationSizeRestService;
    
    @Override
    public List<OrganisationSizeResource> getOrganisationSizes() {
        return organisationSizeRestService.getOrganisationSizes().getSuccessObjectOrThrowException();
    }
}
