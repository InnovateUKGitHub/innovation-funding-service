package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.mapper.FormInputResponseMapper;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Implements {@link FormInputResponseService}
 */
@Service
public class FormInputResponseServiceImpl extends BaseTransactionalService implements FormInputResponseService {

    @Autowired
    private FormInputResponseMapper formInputResponseMapper;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Override
    public ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(final long applicationId) {
        return serviceSuccess(formInputResponsesToResources(formInputResponseRepository.findByApplicationId(applicationId)));
    }

    @Override
    public ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(final long formInputId, final long applicationId) {
        return serviceSuccess(formInputResponsesToResources(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)));
    }

    @Override
    public ServiceResult<FormInputResponseResource> findResponseByApplicationIdAndQuestionSetupType(long applicationId,
                                                                                                    QuestionSetupType questionSetupType){
        return find(formInputResponseRepository.findOneByApplicationIdAndFormInputQuestionQuestionSetupType(applicationId, questionSetupType),
                notFoundError(FormInputResponse.class, applicationId, questionSetupType)).andOnSuccessReturn(formInputResponseMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<FormInputResponseResource>> findResponseByApplicationIdAndQuestionId(long applicationId, long questionId) {
        return serviceSuccess(formInputResponsesToResources(formInputResponseRepository.findByApplicationIdAndFormInputQuestionId(applicationId, questionId)));
    }

    @Override
    @Transactional
    public ServiceResult<FormInputResponse> saveQuestionResponse(FormInputResponseCommand formInputResponseCommand) {
        Long applicationId = formInputResponseCommand.getApplicationId();
        Long formInputId = formInputResponseCommand.getFormInputId();
        String htmlUnescapedValue = formInputResponseCommand.getValue();
        Long userId = formInputResponseCommand.getUserId();
        ProcessRole userAppRole = processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(userId, applicantProcessRoles(), applicationId);

        return find(user(userId), formInput(formInputId), openApplication(applicationId)).
                andOnSuccess((user, formInput, application) ->
                        getOrCreateResponse(application, formInput, userAppRole)
                                .andOnSuccessReturn(response -> updateAndSaveResponse(response, htmlUnescapedValue, userAppRole, application))
                );
    }

    private FormInputResponse updateAndSaveResponse(FormInputResponse response, String htmlUnescapedValue, ProcessRole userAppRole, Application application) {
        if (!response.getValue().equals(htmlUnescapedValue)) {
            response.setUpdateDate(ZonedDateTime.now());
            response.setUpdatedBy(userAppRole);
        }
        response.setValue(htmlUnescapedValue);
        application.addFormInputResponse(response, userAppRole);
        applicationRepository.save(application);
        formInputResponseRepository.save(response);
        return response;
    }


    private ServiceResult<FormInputResponse> getOrCreateResponse(Application application, FormInput formInput, ProcessRole userAppRole) {

        Optional<FormInputResponse> existingResponse = application.getFormInputResponseByFormInputAndProcessRole(formInput, userAppRole);

        return existingResponse.isPresent() ?
                serviceSuccess(existingResponse.get()) :
                serviceSuccess(new FormInputResponse(ZonedDateTime.now(), "", userAppRole, formInput, application));
    }

    private Supplier<ServiceResult<FormInput>> formInput(long id) {
        return () -> findFormInputEntity(id);
    }

    private List<FormInputResponseResource> formInputResponsesToResources(List<FormInputResponse> filtered) {
        return simpleMap(filtered, formInputResponse -> formInputResponseMapper.mapToResource(formInputResponse));
    }

    private ServiceResult<FormInput> findFormInputEntity(long id) {
        return find(formInputRepository.findById(id), notFoundError(FormInput.class, id));
    }

}
