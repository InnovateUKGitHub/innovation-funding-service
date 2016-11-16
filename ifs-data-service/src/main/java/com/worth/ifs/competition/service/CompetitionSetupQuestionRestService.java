package com.worth.ifs.competition.service;

import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.commons.rest.RestResult;

public interface CompetitionSetupQuestionRestService {

    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);
    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);

}
