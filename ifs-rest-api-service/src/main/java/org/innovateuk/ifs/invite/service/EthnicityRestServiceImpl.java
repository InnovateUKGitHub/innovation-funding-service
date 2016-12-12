package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.ethnicityResourceListType;
import static java.lang.String.format;

/**
 * Service for CRUD operations on {@link EthnicityResource}s.
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
