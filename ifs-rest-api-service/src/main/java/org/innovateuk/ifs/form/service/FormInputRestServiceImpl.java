package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;

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
        return getWithRestResult(formInputRestURL + "/find-by-question-id/" + questionId, formInputResourceListType());
    }

    @Override
    public RestResult<List<FormInputResource>> getByQuestionIdAndScope(Long questionId, FormInputScope scope) {
        return getWithRestResult(formInputRestURL + "/find-by-question-id/" + questionId + "/scope/" + scope, formInputResourceListType());
    }

    @Override
    public RestResult<List<FormInputResource>> getByCompetitionId(Long competitionId) {
        return getWithRestResult(formInputRestURL + "/find-by-competition-id/" + competitionId, formInputResourceListType());
    }

    @Override
    public RestResult<List<FormInputResource>> getByCompetitionIdAndScope(Long competitionId, FormInputScope scope) {
        return getWithRestResult(formInputRestURL + "/find-by-competition-id/" + competitionId + "/scope/" + scope, formInputResourceListType());
    }

    @Override
    public RestResult<Void> delete(Long formInputId) {
        return deleteWithRestResult(formInputRestURL + "/" + formInputId, Void.class) ;
    }

    @Override
    public RestResult<ByteArrayResource> downloadFile(long formInputId) {
        String url = format("%s/%s/%s", formInputRestURL, "file", formInputId);
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> findFile(long formInputId) {
        String url = format("%s/%s/%s", formInputRestURL, "file-details", formInputId);
        return getWithRestResult(url, FileEntryResource.class);
    }

}
