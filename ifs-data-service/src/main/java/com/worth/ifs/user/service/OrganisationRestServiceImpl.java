package com.worth.ifs.user.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link Organisation}.
 * This class connects to the {@link com.worth.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationRestServiceImpl extends BaseRestService implements OrganisationRestService {
    @Value("${ifs.data.service.rest.organisation}")
    String organisationRestURL;

    private final Log log = LogFactory.getLog(getClass());

    public List<Organisation> getOrganisationsByApplicationId(Long applicationId) {
        return asList(restGet(organisationRestURL + "/findByApplicationId/" + applicationId, Organisation[].class));
    }

    public Organisation getOrganisationById(Long organisationId) {
        return restGet(organisationRestURL + "/findById/"+organisationId, Organisation.class);
    }

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    @Override
    public OrganisationResource save(Organisation organisation) {
        return restPost(organisationRestURL + "/save", organisation, OrganisationResource.class);
    }

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    @Override
    public OrganisationResource addAddress(OrganisationResource organisation, Address address, AddressType type) {
        return restPost(organisationRestURL + "/addAddress/"+organisation.getId()+"?addressType="+type.name(), address, OrganisationResource.class);
    }

}
