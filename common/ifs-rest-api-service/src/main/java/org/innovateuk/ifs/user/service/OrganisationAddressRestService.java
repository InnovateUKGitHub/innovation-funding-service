package org.innovateuk.ifs.user.service;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrganisationAddressRestService {
    RestResult<List<OrganisationAddressResource>> getOrganisationRegisterdAddressById(long organisationId);
}
