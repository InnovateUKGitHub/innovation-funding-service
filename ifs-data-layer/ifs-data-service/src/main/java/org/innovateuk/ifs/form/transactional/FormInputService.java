package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FormInputService {

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputResource> findFormInput(Long id);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByQuestionId(Long questionId);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByQuestionIdAndScope(Long questionId, FormInputScope scope);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByCompetitionIdAndScope(Long competitionId, FormInputScope scope);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update form inputs")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<FormInputResource> save(FormInputResource formInputResource);

    @SecuredBySpring(value = "DELETE", description = "Only those with either comp admin or project finance roles can delete form inputs")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> delete(Long id);
}
