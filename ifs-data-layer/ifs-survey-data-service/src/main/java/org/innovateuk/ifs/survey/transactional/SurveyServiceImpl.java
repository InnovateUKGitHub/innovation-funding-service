package org.innovateuk.ifs.survey.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.mapper.SurveyMapper;
import org.innovateuk.ifs.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private SurveyRepository surveyRepository;

    @Override
    public ServiceResult<Void> save(SurveyResource surveyResource) {
        surveyRepository.save(surveyMapper.mapToDomain(surveyResource));
        return ServiceResult.serviceSuccess();
    }
}
