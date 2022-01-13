package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link RejectionReasonResource} related data.
 */
public interface RejectionReasonRestService {

    RestResult<List<RejectionReasonResource>> findAllActive();

}
