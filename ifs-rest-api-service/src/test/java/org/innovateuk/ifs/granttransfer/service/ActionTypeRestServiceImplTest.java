package org.innovateuk.ifs.granttransfer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.granttransfer.service.ActionTypeRestServiceImpl.euActionTypeResourceListType;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.OK;

public class ActionTypeRestServiceImplTest extends BaseRestServiceUnitTest<ActionTypeRestServiceImpl> {

    private static final String REST_URL = "/action-type";

    @Override
    protected ActionTypeRestServiceImpl registerRestServiceUnderTest() {
        return new ActionTypeRestServiceImpl();
    }

    @Test
    public void findAll() {
        List<EuActionTypeResource> expected = emptyList();
        setupGetWithRestResultExpectations(format("%s/%s", REST_URL, "find-all"), euActionTypeResourceListType(), expected, OK);
        List<EuActionTypeResource> response = service.findAll().getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void getById() {
        long id = 1L;
        EuActionTypeResource expected = new EuActionTypeResource();
        setupGetWithRestResultExpectations(format("%s/%s/%d", REST_URL, "get-by-id", id), EuActionTypeResource.class, expected, OK);
        EuActionTypeResource response = service.getById(id).getSuccess();
        assertSame(expected, response);
    }
}