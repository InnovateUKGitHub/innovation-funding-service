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
    private static final String ORGANISATION_BASE_URL = "/organisation";

    @Override
    public RestResult<List<OrganisationResource>> getOrganisationsByApplicationId(Long applicationId) {
        return getWithRestResult(ORGANISATION_BASE_URL + "/find-by-application-id/" + applicationId, organisationResourceListType());
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationById(long organisationId) {
        return getWithRestResult(ORGANISATION_BASE_URL + "/find-by-id/" + organisationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationByIdForAnonymousUserFlow(Long organisationId) {
        return getWithRestResultAnonymous(ORGANISATION_BASE_URL + "/find-by-id/" + organisationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getByUserAndApplicationId(long userId, long applicationId) {
        return getWithRestResult(ORGANISATION_BASE_URL + "/by-user-and-application-id/" + userId + "/" + applicationId, OrganisationResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getByUserAndProjectId(long userId, long projectId) {
        return getWithRestResult(ORGANISATION_BASE_URL + "/by-user-and-project-id/" + userId + "/" + projectId, OrganisationResource.class);
    }

    @Override
    public RestResult<List<OrganisationResource>> getAllByUserId(long userId) {
        return getWithRestResult(ORGANISATION_BASE_URL + "/all-by-user-id/" + userId,  organisationResourceListType());
    }

    @Override
    public RestResult<List<OrganisationResource>> getOrganisations(long userId, boolean international) {
        return getWithRestResult(ORGANISATION_BASE_URL + "?userId=" + userId + "&international=" + international,  organisationResourceListType());
    }

    @Override
    public RestResult<OrganisationResource> createOrMatch(OrganisationResource organisation) {
        return postWithRestResultAnonymous(ORGANISATION_BASE_URL + "/create-or-match", organisation, OrganisationResource.class);
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
        return postWithRestResult(ORGANISATION_BASE_URL + "/update-name-and-registration/" +  organisation.getId() + "?name=" + organisationName + "&registration=" + organisation.getCompaniesHouseNumber(), OrganisationResource.class);
    }
}
