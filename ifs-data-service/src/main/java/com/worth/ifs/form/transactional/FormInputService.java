package com.worth.ifs.form.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseCommand;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FormInputService {

    @NotSecured(value = "Anyone can see a form input type", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputTypeResource> findFormInputType(Long id);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputResource> findFormInput(Long id);


    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByQuestionId(Long questionId);

    @NotSecured(value = "Anyone can see a form input", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(Long formInputId, Long applicationId);

    // TODO we need to have separate methods for save and update
    @PreAuthorize("hasPermission(#formInputResponseCommand, 'SAVE')")
    ServiceResult<FormInputResponse> saveQuestionResponse(@P("formInputResponseCommand")FormInputResponseCommand formInputResponseCommand);

}