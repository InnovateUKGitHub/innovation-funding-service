package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionParticipantResourceListType;
import static java.lang.String.format;

@Service
public class CompetitionParticipantRestServiceImpl extends BaseRestService implements CompetitionParticipantRestService {

    private static final String competitionParticipantRestUrl = "/competitionparticipant";

    @Override
    public RestResult<List<CompetitionParticipantResource>> getParticipants(Long userId, CompetitionParticipantRoleResource role, ParticipantStatusResource status) {
        return getWithRestResult(String.format("%s/user/%s/role/%s/status/%s", competitionParticipantRestUrl, userId, role, status), ParameterizedTypeReferences.competitionParticipantResourceListType());
    }
}
