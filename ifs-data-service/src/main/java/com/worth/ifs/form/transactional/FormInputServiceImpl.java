package com.worth.ifs.form.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.mapper.FormInputMapper;
import com.worth.ifs.form.mapper.FormInputResponseMapper;
import com.worth.ifs.form.mapper.FormInputTypeMapper;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.form.repository.FormInputTypeRepository;
import com.worth.ifs.form.resource.*;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class FormInputServiceImpl extends BaseTransactionalService implements FormInputService {

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputTypeRepository formInputTypeRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputTypeMapper formInputTypeMapper;

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
        return serviceSuccess(formInputToResources(formInputRepository.findByQuestionIdOrderByPriorityAsc(questionId)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByQuestionIdAndScope(Long questionId, FormInputScope scope) {
        return serviceSuccess(formInputToResources(formInputRepository.findByQuestionIdAndScopeOrderByPriorityAsc(questionId, scope)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId) {
        return serviceSuccess(formInputToResources(formInputRepository.findByCompetitionIdOrderByPriorityAsc(competitionId)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByCompetitionIdAndScope(Long competitionId, FormInputScope scope) {
        return serviceSuccess(formInputToResources(formInputRepository.findByCompetitionIdAndScopeOrderByPriorityAsc(competitionId, scope)));
    }

    private ServiceResult<FormInput> findFormInputEntity(Long id) {
        return find(formInputRepository.findOne(id), notFoundError(FormInput.class, id));
    }

    @Override
    public ServiceResult<FormInputTypeResource> findFormInputType(Long id) {
        return find(formInputTypeRepository.findOne(id), notFoundError(FormInputType.class, id)).
                andOnSuccessReturn(formInputTypeMapper::mapToResource);
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
                        response.setUpdateDate(LocalDateTime.now());
                        response.setUpdatedBy(userAppRole);
                    }
                    response.setValue(htmlUnescapedValue);
                    application.addFormInputResponse(response);
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

    	Optional<FormInputResponse> existingResponse = application.getFormInputResponseByFormInput(formInput);

        return existingResponse != null && existingResponse.isPresent() ?
                serviceSuccess(existingResponse.get()) :
                serviceSuccess(new FormInputResponse(LocalDateTime.now(), "", userAppRole, formInput, application));
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