package com.worth.ifs.user.service;

import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.organisationResourceListType;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link OrganisationResource}.
 * This class connects to the {com.worth.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationRestServiceImpl extends BaseRestService implements OrganisationRestService {
    private static final Log log = LogFactory.getLog(OrganisationRestServiceImpl.class);

    private String organisationRestURL = "/organisation";

    @Override
    public RestResult<List<OrganisationResource>> getOrganisationsByApplicationId(Long applicationId) {
        return getWithRestResult(organisationRestURL + "/findByApplicationId/" + applicationId, organisationResourceListType());
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationById(Long organisationId) {
        return getWithRestResult(organisationRestURL + "/findById/"+organisationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationByIdForAnonymousUserFlow(Long organisationId) {
        return getWithRestResultAnonymous(organisationRestURL + "/findById/" + organisationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> create(OrganisationResource organisation) {
        return postWithRestResultAnonymous(organisationRestURL + "/create", organisation, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> update(OrganisationResource organisation) {
        return putWithRestResult(organisationRestURL + "/update", organisation, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> updateNameAndRegistration(OrganisationResource organisation) {
        String organisationName;
        try {
            organisationName = UriUtils.encode(organisation.getName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e);
            organisationName = organisation.getName();
        }
        return postWithRestResult(organisationRestURL + "/updateNameAndRegistration/" +  organisation.getId() + "?name=" + organisationName + "&registration=" + organisation.getCompanyHouseNumber(), OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> updateByIdForAnonymousUserFlow(OrganisationResource organisation) {
        return putWithRestResultAnonymous(organisationRestURL + "/update", organisation, OrganisationResource.class);
    }

    @Override
     public RestResult<OrganisationResource> addAddress(OrganisationResource organisation, AddressResource address, OrganisationAddressType type) {
        return postWithRestResultAnonymous(organisationRestURL + "/addAddress/"+organisation.getId()+"?addressType="+type.name(), address, OrganisationResource.class);
    }
}
