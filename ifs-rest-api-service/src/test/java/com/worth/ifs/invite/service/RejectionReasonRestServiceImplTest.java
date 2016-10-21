package com.worth.ifs.invite.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.rejectionReasonResourceListType;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.OK;

public class RejectionReasonRestServiceImplTest extends BaseRestServiceUnitTest<RejectionReasonRestServiceImpl> {
    private static String rejectionReasonRestUrl = "/rejectionReason";

    @Override
    protected RejectionReasonRestServiceImpl registerRestServiceUnderTest() {
        RejectionReasonRestServiceImpl rejectionReasonRestService = new RejectionReasonRestServiceImpl();
        rejectionReasonRestService.setRejectionReasonRestUrl(rejectionReasonRestUrl);
        return rejectionReasonRestService;
    }

    @Test
    public void findAllActive() throws Exception {
        List<RejectionReasonResource> expected = newRejectionReasonResource()
                .build(2);

        setupGetWithRestResultAnonymousExpectations(format("%s/findAllActive", rejectionReasonRestUrl), rejectionReasonResourceListType(), expected, OK);
        List<RejectionReasonResource> response = service.findAllActive().getSuccessObject();
        assertSame(expected, response);
    }

}