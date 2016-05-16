package com.worth.ifs.form.transactional;

import java.util.List;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.security.NotSecured;

import org.springframework.security.access.prepost.PostFilter;

public interface FormInputService {

    @NotSecured(value = "Anyone can see a form input type", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputTypeResource> findFormInputType(Long id);

    // @NotSecured("anyone can see a form input")
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputResource> findFormInput(Long id);

    // @NotSecured("anyone can see a form input")
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByQuestionId(Long questionId);

    // @NotSecured("anyone can see a form input")
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(Long formInputId, Long applicationId);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<FormInputResponse> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String htmlUnescapedValue);



}