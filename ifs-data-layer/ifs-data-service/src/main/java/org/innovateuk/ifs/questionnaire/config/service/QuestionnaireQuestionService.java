package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface QuestionnaireQuestionService extends IfsCrudService<QuestionnaireQuestionResource, Long> {
    @Override
    @PreAuthorize("permitAll")
    ServiceResult<QuestionnaireQuestionResource> get(Long aLong);

    @Override
    @PreAuthorize("permitAll")
    ServiceResult<List<QuestionnaireQuestionResource>> get(List<Long> longs);

    @Override
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<QuestionnaireQuestionResource> update(Long aLong, QuestionnaireQuestionResource questionnaireQuestionResource);

    @Override
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<Void> delete(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<QuestionnaireQuestionResource> create(QuestionnaireQuestionResource questionnaireQuestionResource);
}
