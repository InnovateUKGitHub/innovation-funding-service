package com.worth.ifs.form.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface FormInputService {

    @NotSecured("TODO")
    ServiceResult<FormInputTypeResource> findFormInputType(Long id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<FormInput> findFormInput(Long id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<List<FormInputResponse>> findResponsesByApplication(Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<FormInputResponse> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String htmlUnescapedValue);
}