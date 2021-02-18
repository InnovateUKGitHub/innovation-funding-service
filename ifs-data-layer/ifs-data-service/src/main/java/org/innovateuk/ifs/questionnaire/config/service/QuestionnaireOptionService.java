package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface QuestionnaireOptionService extends IfsCrudService<QuestionnaireOptionResource, Long> {
    @Override
    @PreAuthorize("permitAll")
    ServiceResult<QuestionnaireOptionResource> get(Long aLong);

    @Override
    @PreAuthorize("permitAll")
    ServiceResult<List<QuestionnaireOptionResource>> get(List<Long> longs);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<QuestionnaireOptionResource> update(Long aLong, QuestionnaireOptionResource questionnaireOptionResource);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> delete(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<QuestionnaireOptionResource> create(QuestionnaireOptionResource questionnaireOptionResource);
}
