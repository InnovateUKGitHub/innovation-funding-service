package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface QuestionnaireQuestionResponseService extends IfsCrudService<QuestionnaireQuestionResponseResource, Long> {

    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionnaireQuestionResponseResource> get(Long id);

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionnaireQuestionResponseResource>> get(List<Long> longs);

    @Override
    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource', 'UPDATE_OR_DELETE')")
    ServiceResult<QuestionnaireQuestionResponseResource> update(Long id, QuestionnaireQuestionResponseResource questionnaireQuestionResponseResource);

    @Override
    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource', 'UPDATE_OR_DELETE')")
    ServiceResult<Void> delete(Long id);

    @Override
    @PreAuthorize("hasPermission(#resource, 'READ')")
    ServiceResult<QuestionnaireQuestionResponseResource> create(QuestionnaireQuestionResponseResource resource);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(long questionId, String responseId);
}
