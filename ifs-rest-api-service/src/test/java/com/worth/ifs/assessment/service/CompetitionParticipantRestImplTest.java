package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionParticipantResourceListType;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.invite.resource.CompetitionParticipantRoleResource.ASSESSOR;
import static com.worth.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class CompetitionParticipantRestImplTest extends BaseRestServiceUnitTest<CompetitionParticipantRestServiceImpl> {

    private static final String restUrl = "/competitionparticipant";

    @Override
    protected CompetitionParticipantRestServiceImpl registerRestServiceUnderTest() {
        CompetitionParticipantRestServiceImpl competitionParticipantRestService = new CompetitionParticipantRestServiceImpl();
        return competitionParticipantRestService;
    }

    @Test
    public void getParticipants() {
        List<CompetitionParticipantResource> expected = newCompetitionParticipantResource()
                .withUser(1L, 1L)
                .withCompetitionParticipantRole(ASSESSOR, ASSESSOR)
                .withStatus(ACCEPTED, ACCEPTED)
                .withCompetition(2L, 3L)
                .build(2);

        setupGetWithRestResultExpectations(String.format("%s/user/%s/role/%s/status/%s", restUrl, 1L, ASSESSOR, ACCEPTED), competitionParticipantResourceListType(), expected, OK);
        List<CompetitionParticipantResource> actual = service.getParticipants(1L, ASSESSOR, ACCEPTED).getSuccessObject();
        assertEquals(expected, actual);
    }
}
