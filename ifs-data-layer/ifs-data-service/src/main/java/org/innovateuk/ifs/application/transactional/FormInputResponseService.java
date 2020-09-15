package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * Transactional service for application form input responses.
 */
public interface FormInputResponseService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(long formInputId, long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<FormInputResponseResource> findResponseByApplicationIdAndQuestionSetupType(long applicationId,
                                                                                             QuestionSetupType questionSetupType);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponseByApplicationIdAndQuestionId(long applicationId, long questionId);

    // TODO: IFS-3830 we need to have separate methods for save and update
    @PreAuthorize("hasPermission(#formInputResponseCommand, 'SAVE')")
    ServiceResult<FormInputResponseResource> saveQuestionResponse(@P("formInputResponseCommand") FormInputResponseCommand formInputResponseCommand);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FormInputResponseResource> findResponseByApplicationIdQuestionIdOrganisationIdAndFormInputType(long applicationId, long questionId, long organisationId, FormInputType formInputType);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FormInputResponseResource> findResponseByApplicationIdQuestionIdOrganisationIdFormInputTypeAndDescription(long applicationId, long questionId, long organisationId, FormInputType formInputType, String description);
}
