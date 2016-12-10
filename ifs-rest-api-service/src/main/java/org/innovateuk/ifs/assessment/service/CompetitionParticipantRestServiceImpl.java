package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionParticipantResourceListType;
import static java.lang.String.format;

@Service
public class CompetitionParticipantRestServiceImpl extends BaseRestService implements CompetitionParticipantRestService {

    private static final String competitionParticipantRestUrl = "/competitionparticipant";

    @Override
    public RestResult<List<CompetitionParticipantResource>> getParticipants(Long userId, CompetitionParticipantRoleResource role, ParticipantStatusResource status) {
        return getWithRestResult(String.format("%s/user/%s/role/%s/status/%s", competitionParticipantRestUrl, userId, role, status), ParameterizedTypeReferences.competitionParticipantResourceListType());
    }
}
