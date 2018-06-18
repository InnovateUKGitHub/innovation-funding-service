package org.innovateuk.ifs.survey.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.survey.SurveyResource;

public interface SurveyService {

    ServiceResult<Void> save(SurveyResource surveyResource);

}
