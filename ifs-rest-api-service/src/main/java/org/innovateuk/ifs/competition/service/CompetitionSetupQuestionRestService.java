package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.*;
import org.innovateuk.ifs.competition.resource.*;

public interface CompetitionSetupQuestionRestService {

    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);

    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);
}
