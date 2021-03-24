package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface QuestionnaireService extends IfsCrudService<QuestionnaireResource, Long> {
    @Override
    @PreAuthorize("permitAll")
    ServiceResult<QuestionnaireResource> get(Long aLong);

    @Override
    @PreAuthorize("permitAll")
    ServiceResult<List<QuestionnaireResource>> get(List<Long> longs);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "CREATE_UPDATE_DELETE_QUESTIONNAIRE", securedType = QuestionnaireResource.class, description = "Only comp admins can perform update create delete actions on questionnaires" )
    ServiceResult<QuestionnaireResource> update(Long aLong, QuestionnaireResource questionnaireResource);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "CREATE_UPDATE_DELETE_QUESTIONNAIRE", securedType = QuestionnaireResource.class, description = "Only comp admins can perform update create delete actions on questionnaires" )
    ServiceResult<Void> delete(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "CREATE_UPDATE_DELETE_QUESTIONNAIRE", securedType = QuestionnaireResource.class, description = "Only comp admins can perform update create delete actions on questionnaires" )
    ServiceResult<QuestionnaireResource> create(QuestionnaireResource questionnaireResource);
}
