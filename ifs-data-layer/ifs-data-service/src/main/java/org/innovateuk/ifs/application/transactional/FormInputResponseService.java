package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional service for application form input responses.
 */
public interface FormInputResponseService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(long formInputId, long applicationId);

    @ZeroDowntime(reference = "IFS-3597", description = "Do not find via name but via questionSetupType. Remove in cleanup")
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FormInputResponseResource> findResponseByApplicationIdAndQuestionName(long applicationId, String questionName);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FormInputResponseResource> findResponseByApplicationIdAndQuestionSetupType(long applicationId,
                                                                                             CompetitionSetupQuestionType questionSetupType);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FormInputResponseResource>> findResponseByApplicationIdAndQuestionId(long applicationId, long questionId);

    // TODO we need to have separate methods for save and update
    @PreAuthorize("hasPermission(#formInputResponseCommand, 'SAVE')")
    ServiceResult<FormInputResponse> saveQuestionResponse(@P("formInputResponseCommand") FormInputResponseCommand formInputResponseCommand);

}
