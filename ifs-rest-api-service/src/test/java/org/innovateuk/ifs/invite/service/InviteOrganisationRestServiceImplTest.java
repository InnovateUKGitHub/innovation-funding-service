package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class InviteOrganisationRestServiceImplTest extends BaseRestServiceUnitTest<InviteOrganisationRestServiceImpl> {

    private static final String restUrl = "/inviteorganisation";

    @Override
    protected InviteOrganisationRestServiceImpl registerRestServiceUnderTest() {
        return new InviteOrganisationRestServiceImpl();
    }

    @Test
    public void getById() throws Exception {
        Long inviteOrganisationId = 1L;

        InviteOrganisationResource expected = newInviteOrganisationResource().build();

        setupGetWithRestResultExpectations(format("%s/%s", restUrl, inviteOrganisationId), InviteOrganisationResource.class, expected);
        InviteOrganisationResource actual = service.getById(inviteOrganisationId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);
    }

    @Test
    public void getByIdForAnonymousUserFlow() throws Exception {
        Long inviteOrganisationId = 1L;

        InviteOrganisationResource expected = newInviteOrganisationResource().build();

        setupGetWithRestResultAnonymousExpectations(format("%s/%s", restUrl, inviteOrganisationId), InviteOrganisationResource.class, expected);
        InviteOrganisationResource actual = service.getByIdForAnonymousUserFlow(inviteOrganisationId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);
    }

    @Test
    public void put() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        setupPutWithRestResultExpectations(format("%s/save", restUrl), inviteOrganisationResource, OK);
        RestResult<Void> result = service.put(inviteOrganisationResource);
        assertTrue(result.isSuccess());
    }
}