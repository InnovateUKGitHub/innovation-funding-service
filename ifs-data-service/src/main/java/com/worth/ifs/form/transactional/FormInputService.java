package com.worth.ifs.form.transactional;

import java.util.List;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.security.NotSecured;

import org.springframework.security.access.prepost.PreAuthorize;

public interface FormInputService {

    @NotSecured("Anyone can see a form input type")
    ServiceResult<FormInputTypeResource> findFormInputType(Long id);

    @NotSecured("anyone can see a form input")
    ServiceResult<FormInputResource> findFormInput(Long id);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.domain.Application', 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<FormInputResponse> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String htmlUnescapedValue);
}