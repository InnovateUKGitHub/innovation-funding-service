package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.EthnicityResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link EthnicityResource} related data.
 */
public interface EthnicityRestService {

    RestResult<List<EthnicityResource>> findAllActive();
}
