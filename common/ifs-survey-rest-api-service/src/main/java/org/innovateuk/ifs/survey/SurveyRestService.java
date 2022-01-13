package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.commons.rest.RestResult;

public interface SurveyRestService {

    RestResult<Void> save(SurveyResource surveyResource);

}
