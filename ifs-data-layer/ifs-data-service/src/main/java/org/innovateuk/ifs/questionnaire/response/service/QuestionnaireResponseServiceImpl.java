package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
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

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Override
    protected CrudRepository<QuestionnaireResponse, UUID> crudRepository() {
        return questionnaireResponseRepository;
    }

    @Override
    protected Class<QuestionnaireResponse> getDomainClazz() {
        return QuestionnaireResponse.class;
    }

    @Override
    protected QuestionnaireResponse mapToDomain(QuestionnaireResponse domain, QuestionnaireResponseResource resource) {
        if (domain.getQuestionnaire() == null) {
            domain.setQuestionnaire(questionnaireRepository.findById(resource.getQuestionnaire()).orElseThrow(ObjectNotFoundException::new));
        }
        if (resource.getId() != null) {
            domain.setId(UUID.fromString(resource.getId()));
        }
        return domain;
    }


}
