package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionnaireResponseServiceImpl extends AbstractIfsCrudServiceImpl<QuestionnaireResponseResource, QuestionnaireResponse, UUID> implements QuestionnaireResponseService {

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Override
    protected CrudRepository<QuestionnaireResponse, UUID> crudRepository() {
        return questionnaireResponseRepository;
    }

    @Override
    protected Class<QuestionnaireResponse> getDomainClazz() {
        return QuestionnaireResponse.class;
    }

    @Override
    protected QuestionnaireResponse mapToDomain(QuestionnaireResponse questionnaireResponse, QuestionnaireResponseResource questionnaireResponseResource) {
        return questionnaireResponse;
    }


}
