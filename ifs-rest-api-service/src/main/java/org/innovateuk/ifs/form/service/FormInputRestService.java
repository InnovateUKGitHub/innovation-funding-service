package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface FormInputRestService {
    RestResult<FormInputResource> getById(Long id);

    RestResult<List<FormInputResource>> getByQuestionId(Long questionId);

    RestResult<List<FormInputResource>> getByQuestionIdAndScope(Long questionId, FormInputScope scope);

    RestResult<List<FormInputResource>> getByCompetitionId(Long competitionId);

    RestResult<List<FormInputResource>> getByCompetitionIdAndScope(Long competitionId, FormInputScope scope);

    RestResult<Void> delete(Long id);

    RestResult<ByteArrayResource> downloadFile(long formInputId);

    RestResult<FileEntryResource> findFile(long formInputId);
}
