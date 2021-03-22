package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

public interface QuestionnaireResponseService extends IfsCrudService<QuestionnaireResponseResource, UUID> {
    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionnaireResponseResource> get(UUID uuid);

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionnaireResponseResource>> get(List<UUID> uuids);

    @Override
    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource', 'UPDATE_OR_DELETE')")
    ServiceResult<QuestionnaireResponseResource> update(UUID id, QuestionnaireResponseResource responseResource);

    @Override
    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource', 'UPDATE_OR_DELETE')")
    ServiceResult<Void> delete(UUID id);

    @Override
    @PreAuthorize("permitAll")
    ServiceResult<QuestionnaireResponseResource> create(QuestionnaireResponseResource responseResource);
}
