package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionParticipantResourceListType;
import static org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource.ASSESSOR;
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
        List<CompetitionParticipantResource> expected = Arrays.asList(1,2).stream().map(i -> {
            CompetitionParticipantResource cpr = new CompetitionParticipantResource();
            cpr.setUserId(1L);
            cpr.setRole(ASSESSOR);
            cpr.setCompetitionId(i == 0 ? 2L : 3L);
            return cpr;
        }).collect(Collectors.toList());

        setupGetWithRestResultExpectations(String.format("%s/user/%s", restUrl, 1L), competitionParticipantResourceListType(), expected, OK);
        List<CompetitionParticipantResource> actual = service.getAssessorParticipants(1L).getSuccess();
        assertEquals(expected, actual);
    }
}
