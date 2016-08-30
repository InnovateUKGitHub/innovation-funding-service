package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionParticipantResourceListType;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static java.lang.String.format;
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
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource()
                .withUser(1L)
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .build();
        List<CompetitionParticipantResource> expected = new ArrayList<>();
        expected.add(competitionParticipantResource);

        setupGetWithRestResultExpectations(format("%s/user/%s/role/%s/status/%s", restUrl, 1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED), competitionParticipantResourceListType(), expected, OK);
        List<CompetitionParticipantResource> actual = service.getParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR , ParticipantStatusResource.ACCEPTED ).getSuccessObject();
        assertEquals(expected, actual);
    }
}
