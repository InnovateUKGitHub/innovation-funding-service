package org.innovateuk.ifs.user.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationResourceListType;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link OrganisationResource}.
 * This class connects to the {org.innovateuk.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationRestServiceImpl extends BaseRestService implements OrganisationRestService {
    private static final Log log = LogFactory.getLog(OrganisationRestServiceImpl.class);

    private String organisationRestURL = "/organisation";

    @Override
    public RestResult<List<OrganisationResource>> getOrganisationsByApplicationId(Long applicationId) {
        return getWithRestResult(organisationRestURL + "/find-by-application-id/" + applicationId, organisationResourceListType());
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationById(long organisationId) {
        return getWithRestResult(organisationRestURL + "/find-by-id/" + organisationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationByIdForAnonymousUserFlow(Long organisationId) {
        return getWithRestResultAnonymous(organisationRestURL + "/find-by-id/" + organisationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getByUserAndApplicationId(long userId, long applicationId) {
        return getWithRestResult(organisationRestURL + "/by-user-and-application-id/" + userId + "/" + applicationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getByUserAndProjectId(long userId, long projectId) {
        return getWithRestResult(organisationRestURL + "/by-user-and-project-id/" + userId + "/" + projectId, OrganisationResource.class);
    }

    @Override
    public RestResult<List<OrganisationResource>> getAllByUserId(long userId) {
        return getWithRestResult(organisationRestURL + "/all-by-user-id/" + userId,  organisationResourceListType());
    }

    @Override
    public RestResult<OrganisationResource> createOrMatch(OrganisationResource organisation) {
        return postWithRestResultAnonymous(organisationRestURL + "/create-or-match", organisation, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> updateNameAndRegistration(OrganisationResource organisation) {
        String organisationName;
        try {
            organisationName = UriUtils.encode(organisation.getName(), "UTF-8");
        } catch (Exception e) {
            log.error(e);
            organisationName = organisation.getName();
        }
        return postWithRestResult(organisationRestURL + "/update-name-and-registration/" +  organisation.getId() + "?name=" + organisationName + "&registration=" + organisation.getCompaniesHouseNumber(), OrganisationResource.class);
    }
}
