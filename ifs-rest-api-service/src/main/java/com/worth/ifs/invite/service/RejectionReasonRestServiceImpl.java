package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.rejectionReasonResourceListType;
import static java.lang.String.format;

/**
 * RejectionReasonRestServiceImpl is a utility for CRUD operations on {@link RejectionReasonResource}.
 */
@Service
public class RejectionReasonRestServiceImpl extends BaseRestService implements RejectionReasonRestService {

    private String rejectionReasonRestUrl = "/rejectionReason";

    protected void setRejectionReasonRestUrl(String rejectionReasonRestUrl) {
        this.rejectionReasonRestUrl = rejectionReasonRestUrl;
    }

    @Override
    public RestResult<List<RejectionReasonResource>> findAllActive() {
        return getWithRestResultAnonymous(format("%s/findAllActive", rejectionReasonRestUrl), rejectionReasonResourceListType());
    }
}