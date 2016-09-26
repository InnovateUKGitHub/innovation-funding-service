package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.resource.EthnicityResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.ethnicityResourceListType;
import static java.lang.String.format;

/**
 * Service for CRUD operations on {@link com.worth.ifs.user.domain.Ethnicity}s.
 * <p>
 * This class connects to the {@link com.worth.ifs.invite.controller.EthnicityController}
 * through a REST call.
 */
@Service
public class EthnicityRestServiceImpl extends BaseRestService implements EthnicityRestService {

    private String ethnicityRestUrl = "/ethnicity";

    protected void setEthnicityRestUrl(String ethnicityRestUrl) {
        this.ethnicityRestUrl = ethnicityRestUrl;
    }

    @Override
    public RestResult<List<EthnicityResource>> findAllActive() {
        return getWithRestResultAnonymous(format("%s/findAllActive", ethnicityRestUrl), ethnicityResourceListType());
    }
}