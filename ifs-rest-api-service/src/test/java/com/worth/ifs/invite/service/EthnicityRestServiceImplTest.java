package com.worth.ifs.invite.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.user.resource.EthnicityResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.ethnicityResourceListType;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.OK;

public class EthnicityRestServiceImplTest extends BaseRestServiceUnitTest<EthnicityRestServiceImpl> {

    private static final String ethnicityRestUrl = "/ethnicity";

    @Override
    protected EthnicityRestServiceImpl registerRestServiceUnderTest() {
        EthnicityRestServiceImpl ethnicityRestService = new EthnicityRestServiceImpl();
        ethnicityRestService.setEthnicityRestUrl(ethnicityRestUrl);
        return ethnicityRestService;
    }

    @Test
    public void findAllActive() throws Exception {
        List<EthnicityResource> expected = newEthnicityResource().build(2);

        setupGetWithRestResultAnonymousExpectations(format("%s/findAllActive", ethnicityRestUrl), ethnicityResourceListType(), expected, OK);
        List<EthnicityResource> response = service.findAllActive().getSuccessObject();
        assertSame(expected, response);
    }
}