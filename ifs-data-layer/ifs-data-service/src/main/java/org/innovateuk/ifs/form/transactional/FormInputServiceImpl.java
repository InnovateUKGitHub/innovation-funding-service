package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.mapper.FormInputMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FormInputServiceImpl extends BaseTransactionalService implements FormInputService {

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputMapper formInputMapper;

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

    @Override
    @Transactional
    public ServiceResult<FormInputResource> save(FormInputResource formInputResource) {
        return serviceSuccess(formInputMapper.mapToResource(formInputRepository.save(formInputMapper.mapToDomain(formInputResource))));
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(Long id) {
        formInputRepository.delete(formInputMapper.mapIdToDomain(id));
        return serviceSuccess();
    }

    private List<FormInputResource> formInputToResources(List<FormInput> filtered) {
        return simpleMap(filtered, formInput -> formInputMapper.mapToResource(formInput));
    }

    private ServiceResult<FormInput> findFormInputEntity(Long id) {
        return find(formInputRepository.findOne(id), notFoundError(FormInput.class, id));
    }
}
