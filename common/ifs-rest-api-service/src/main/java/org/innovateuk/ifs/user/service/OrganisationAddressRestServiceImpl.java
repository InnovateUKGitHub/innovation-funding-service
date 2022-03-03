package org.innovateuk.ifs.user.service;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationAddressResourceListType;

@Service
public class OrganisationAddressRestServiceImpl extends BaseRestService implements OrganisationAddressRestService {

    private static final String ORGANISATION_ADDRESS_BASE_URL = "/organisationaddress";

    @Override
    public RestResult<List<OrganisationAddressResource>> getOrganisationRegisterdAddressById(long organisationId) {
        return getWithRestResult(ORGANISATION_ADDRESS_BASE_URL + "/find-by-id/" + organisationId, organisationAddressResourceListType());
    }
}