package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitionParticipantRestServiceImpl extends BaseRestService implements CompetitionParticipantRestService {

    private static final String COMPETITION_PARTICIPANT_REST_URL = "/competitionparticipant";

    @Override
    public RestResult<List<CompetitionParticipantResource>> getParticipants(Long userId, CompetitionParticipantRoleResource role) {
        return getWithRestResult(
                String.format("%s/user/%s/role/%s", COMPETITION_PARTICIPANT_REST_URL, userId, role),
                ParameterizedTypeReferences.competitionParticipantResourceListType()
        );
    }
}
