package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.mapper.FormInputMapper;
import org.innovateuk.ifs.form.mapper.FormInputResponseMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FormInputServiceImpl extends BaseTransactionalService implements FormInputService {

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputMapper formInputMapper;

    @Autowired
    private FormInputResponseMapper formInputResponseMapper;

    @Override
    public ServiceResult<FormInputResource> findFormInput(Long id) {
        return findFormInputEntity(id).andOnSuccessReturn(formInputMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByQuestionId(Long questionId) {
        return serviceSuccess(formInputToResources(formInputRepository.findByQuestionIdAndActiveTrueOrderByPriorityAsc(questionId)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByQuestionIdAndScope(Long questionId, FormInputScope scope) {
        return serviceSuccess(formInputToResources(formInputRepository.findByQuestionIdAndScopeAndActiveTrueOrderByPriorityAsc(questionId, scope)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId) {
        return serviceSuccess(formInputToResources(formInputRepository.findByCompetitionIdAndActiveTrueOrderByPriorityAsc(competitionId)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByCompetitionIdAndScope(Long competitionId, FormInputScope scope) {
        return serviceSuccess(formInputToResources(formInputRepository.findByCompetitionIdAndScopeAndActiveTrueOrderByPriorityAsc(competitionId, scope)));
    }

    private ServiceResult<FormInput> findFormInputEntity(Long id) {
        return find(formInputRepository.findOne(id), notFoundError(FormInput.class, id));
    }

    @Override
    public ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(final Long applicationId) {
        return serviceSuccess(formInputResponsesToResources(formInputResponseRepository.findByApplicationId(applicationId)));
    }

    @Override
    public ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(final Long formInputId, final Long applicationId) {
        return serviceSuccess(formInputResponsesToResources(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)));
    }

    @Override
    public ServiceResult<FormInputResponseResource> findResponseByApplicationIdAndQuestionName(long applicationId, String questionName) {
        return find(formInputResponseRepository.findOneByApplicationIdAndFormInputQuestionName(applicationId, questionName),
                notFoundError(FormInputResponse.class, applicationId, questionName)).andOnSuccessReturn(formInputResponseMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<FormInputResponseResource>> findResponseByApplicationIdAndQuestionId(long applicationId, long questionId) {
        return serviceSuccess(formInputResponsesToResources(formInputResponseRepository.findByApplicationIdAndFormInputQuestionId(applicationId, questionId)));
    }

    @Override
    public ServiceResult<FormInputResponse> saveQuestionResponse(FormInputResponseCommand formInputResponseCommand) {
        long applicationId = formInputResponseCommand.getApplicationId();
        long formInputId = formInputResponseCommand.getFormInputId();
        String htmlUnescapedValue = formInputResponseCommand.getValue();
        long userId = formInputResponseCommand.getUserId();
        ProcessRole userAppRole = processRoleRepository.findByUserIdAndApplicationId(userId, applicationId);
        return find(user(userId), formInput(formInputId), openApplication(applicationId)).
                andOnSuccess((user, formInput, application) ->
                getOrCreateResponse(application, formInput, userAppRole).andOnSuccessReturn(response -> {
                    if (!response.getValue().equals(htmlUnescapedValue)) {
                        response.setUpdateDate(ZonedDateTime.now());
                        response.setUpdatedBy(userAppRole);
                    }
                    response.setValue(htmlUnescapedValue);
                    application.addFormInputResponse(response, userAppRole);
                    applicationRepository.save(application);
                    return response;
                })
            );
    }

    @Override
    public ServiceResult<FormInputResource> save(FormInputResource formInputResource) {
        return serviceSuccess(formInputMapper.mapToResource(formInputRepository.save(formInputMapper.mapToDomain(formInputResource))));
    }

    @Override
    public ServiceResult<Void> delete(Long id) {
        formInputRepository.delete(formInputMapper.mapIdToDomain(id));
        return serviceSuccess();
    }

    private ServiceResult<FormInputResponse> getOrCreateResponse(Application application, FormInput formInput, ProcessRole userAppRole) {

    	Optional<FormInputResponse> existingResponse = application.getFormInputResponseByFormInputAndProcessRole(formInput, userAppRole);

        return existingResponse != null && existingResponse.isPresent() ?
                serviceSuccess(existingResponse.get()) :
                serviceSuccess(new FormInputResponse(ZonedDateTime.now(), "", userAppRole, formInput, application));
    }

    private Supplier<ServiceResult<FormInput>> formInput(Long id) {
        return () -> findFormInputEntity(id);
    }

    private List<FormInputResource> formInputToResources(List<FormInput> filtered) {
        return simpleMap(filtered, formInput -> formInputMapper.mapToResource(formInput));
    }

    private List<FormInputResponseResource> formInputResponsesToResources(List<FormInputResponse> filtered) {
        return simpleMap(filtered, formInputResponse -> formInputResponseMapper.mapToResource(formInputResponse));
    }

}
