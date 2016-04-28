package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;

public interface FormInputRestService {
    RestResult<FormInputResource> getById(Long id);
    RestResult<List<FormInputResource>> getByQuestionId(Long questionId);
    RestResult<List<FormInputResource>> getByCompetitionId(Long competitionId);
}
