package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;

import java.util.List;

public interface FormInputRestService {
    RestResult<FormInputResource> getById(Long id);

    RestResult<List<FormInputResource>> getByQuestionId(Long questionId);

    RestResult<List<FormInputResource>> getByQuestionIdAndScope(Long questionId, FormInputScope scope);

    RestResult<List<FormInputResource>> getByCompetitionId(Long competitionId);

    RestResult<List<FormInputResource>> getByCompetitionIdAndScope(Long competitionId, FormInputScope scope);

    RestResult<Void> delete(Long id);

    RestResult<FormInputResource> save(FormInputResource formInputResource);
}
