package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.EthnicityResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link EthnicityResource} related data.
 */
public interface EthnicityRestService {

    RestResult<List<EthnicityResource>> findAllActive();
}
