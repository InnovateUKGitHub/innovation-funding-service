package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FormInputService {

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputResource> findFormInput(long id);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByQuestionId(long questionId);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByQuestionIdAndScope(long questionId, FormInputScope scope);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByCompetitionId(long competitionId);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByCompetitionIdAndScope(long competitionId, FormInputScope scope);

    @SecuredBySpring(value = "DELETE", description = "Only those with either comp admin or project finance roles can delete form inputs")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> delete(long id);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<FileAndContents> downloadFile(long formInputId);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> findFile(long formInputId);
}
