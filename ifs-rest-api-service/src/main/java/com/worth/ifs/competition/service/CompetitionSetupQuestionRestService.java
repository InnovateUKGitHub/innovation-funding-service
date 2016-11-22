package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.*;
import com.worth.ifs.competition.resource.*;

public interface CompetitionSetupQuestionRestService {

    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);
    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);

}
