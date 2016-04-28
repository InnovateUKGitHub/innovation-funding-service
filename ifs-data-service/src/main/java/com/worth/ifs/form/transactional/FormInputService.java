package com.worth.ifs.form.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

public interface FormInputService {

    @NotSecured("Anyone can see a form input type")
    ServiceResult<FormInputTypeResource> findFormInputType(Long id);

    @NotSecured("anyone can see a form input")
    ServiceResult<FormInputResource> findFormInput(Long id);

    @NotSecured("anyone can see a form input")
    ServiceResult<List<FormInputResource>> findByQuestionId(Long questionId);

    @NotSecured("anyone can see a form input")
    ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId);

    @PostFilter("hasPermission(returnObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(Long applicationId);

    @NotSecured("TODO RB - implement when permissions matrix available")
    ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(Long formInputId, Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<FormInputResponse> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String htmlUnescapedValue);


}