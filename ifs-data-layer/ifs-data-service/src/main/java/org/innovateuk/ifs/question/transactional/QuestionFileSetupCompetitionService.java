package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;

import javax.servlet.http.HttpServletRequest;

public interface QuestionFileSetupCompetitionService {

    ServiceResult<Void> uploadTemplateFile(String contentType, String contentLength, String originalFilename, long questionId,
                                           HttpServletRequest request);

    ServiceResult<Void> deleteTemplateFile(long questionId);

    ServiceResult<FileAndContents> downloadTemplateFile(long questionId);

    ServiceResult<FileEntryResource> findTemplateFile(long questionId);

}
