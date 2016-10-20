package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;

@Service
public class FormInputRestServiceImpl extends BaseRestService implements FormInputRestService {

    private String formInputRestURL = "/forminput";

    protected void setFormInputRestURL(String formInputRestURL) {
        this.formInputRestURL = formInputRestURL;
    }

    @Override
    public RestResult<FormInputResource> getById(Long formInputId) {
        return getWithRestResult(formInputRestURL + "/" + formInputId, FormInputResource.class);
    }

    @Override
    public RestResult<List<FormInputResource>> getByQuestionId(Long questionId) {
        return getWithRestResult(formInputRestURL + "/findByQuestionId/" + questionId, formInputResourceListType());
    }

    @Override
    public RestResult<List<FormInputResource>> getByQuestionIdAndScope(Long questionId, FormInputScope scope) {
        return getWithRestResult(formInputRestURL + "/findByQuestionId/" + questionId + "/scope/" + scope, formInputResourceListType());
    }

    @Override
    public RestResult<List<FormInputResource>> getByCompetitionId(Long competitionId) {
        return getWithRestResult(formInputRestURL + "/findByCompetitionId/" + competitionId, formInputResourceListType());
    }

    @Override
    public RestResult<List<FormInputResource>> getByCompetitionIdAndScope(Long competitionId, FormInputScope scope) {
        return getWithRestResult(formInputRestURL + "/findByCompetitionId/" + competitionId + "/scope/" + scope, formInputResourceListType());
    }

    @Override
    public RestResult<Void> delete(Long formInputId) {
        return deleteWithRestResult(formInputRestURL + "/" + formInputId, Void.class) ;
    }

    @Override
    public RestResult<FormInputResource> save(FormInputResource formInputResource) {
        return putWithRestResult(formInputRestURL + "/", formInputResource, FormInputResource.class);
    }
}
