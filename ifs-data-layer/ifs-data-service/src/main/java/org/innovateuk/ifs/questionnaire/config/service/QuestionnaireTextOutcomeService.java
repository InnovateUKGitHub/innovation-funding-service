package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface QuestionnaireTextOutcomeService extends IfsCrudService<QuestionnaireTextOutcomeResource, Long> {
    @Override
    @PreAuthorize("permitAll")
    ServiceResult<QuestionnaireTextOutcomeResource> get(Long aLong);

    @Override
    @PreAuthorize("permitAll")
    ServiceResult<List<QuestionnaireTextOutcomeResource>> get(List<Long> longs);

    @Override
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<QuestionnaireTextOutcomeResource> update(Long aLong, QuestionnaireTextOutcomeResource resource);

    @Override
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<Void> delete(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<QuestionnaireTextOutcomeResource> create(QuestionnaireTextOutcomeResource resource);
}
