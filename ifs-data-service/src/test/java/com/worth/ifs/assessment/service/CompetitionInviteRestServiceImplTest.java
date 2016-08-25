package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class CompetitionInviteRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionInviteRestServiceImpl> {
    private static final String restUrl = "/competitioninvite";

    @Override
    protected CompetitionInviteRestServiceImpl registerRestServiceUnderTest() {
        CompetitionInviteRestServiceImpl competitionInviteRestService = new CompetitionInviteRestServiceImpl();
        return competitionInviteRestService;
    }

    @Test
    public void getInvite() throws Exception {
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/getInvite", "hash"), CompetitionInviteResource.class, expected);
        CompetitionInviteResource actual = service.getInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite() {
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/openInvite", "hash"), CompetitionInviteResource.class, null, expected, OK);
        CompetitionInviteResource actual = service.openInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite_hashNotExists() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/openInvite", "hashNotExists"), CompetitionInviteResource.class, null, null, NOT_FOUND);
        RestResult<CompetitionInviteResource> restResult = service.openInvite("hashNotExists");
        assertTrue(restResult.isFailure());
    }
}