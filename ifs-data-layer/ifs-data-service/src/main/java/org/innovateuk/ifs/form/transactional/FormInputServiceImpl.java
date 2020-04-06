package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.mapper.FormInputMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;
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

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private FileService fileService;

    @Override
    public ServiceResult<FormInputResource> findFormInput(long id) {
        return findFormInputEntity(id).andOnSuccessReturn(formInputMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByQuestionId(long questionId) {
        return serviceSuccess(formInputToResources(formInputRepository.findByQuestionIdAndActiveTrueOrderByPriorityAsc(questionId)));
    }

    @Override
    @Cacheable(cacheNames="formInputByQuestionAndScope",
            key = "T(java.lang.String).format('formInputByQuestionAndScope:%d:%s', #questionId, #scope.name())",
            unless = "!T(org.innovateuk.ifs.cache.CacheHelper).cacheResultIfCompetitionIsOpen(#result)")
    public ServiceResult<List<FormInputResource>> findByQuestionIdAndScope(long questionId, FormInputScope scope) {
        return serviceSuccess(formInputToResources(formInputRepository.findByQuestionIdAndScopeAndActiveTrueOrderByPriorityAsc(questionId, scope)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByCompetitionId(long competitionId) {
        return serviceSuccess(formInputToResources(formInputRepository.findByCompetitionIdAndActiveTrueOrderByPriorityAsc(competitionId)));
    }

    @Override
    public ServiceResult<List<FormInputResource>> findByCompetitionIdAndScope(long competitionId, FormInputScope scope) {
        return serviceSuccess(formInputToResources(formInputRepository.findByCompetitionIdAndScopeAndActiveTrueOrderByPriorityAsc(competitionId, scope)));
    }

    @Override
    @Transactional
    public ServiceResult<FormInputResource> save(FormInputResource formInputResource) {
        return serviceSuccess(formInputMapper.mapToResource(formInputRepository.save(formInputMapper.mapToDomain(formInputResource))));
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(long id) {
        formInputRepository.delete(formInputMapper.mapIdToDomain(id));
        return serviceSuccess();
    }

    @Override
    public ServiceResult<FileAndContents> downloadFile(long formInputId) {
        return findFormInputEntity(formInputId).andOnSuccess(formInput ->
                fileEntryService.findOne(formInput.getFile().getId())
                        .andOnSuccess(this::getFileAndContents));
    }

    @Override
    public ServiceResult<FileEntryResource> findFile(long formInputId) {
        return findFormInputEntity(formInputId).andOnSuccess(formInput ->
                ofNullable(formInput.getFile())
                        .map(FileEntry::getId)
                        .map(fileEntryService::findOne)
                        .orElse(serviceSuccess(null)));
    }


    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }

    private List<FormInputResource> formInputToResources(List<FormInput> filtered) {
        return simpleMap(filtered, formInput -> formInputMapper.mapToResource(formInput));
    }

    private ServiceResult<FormInput> findFormInputEntity(long id) {
        return find(formInputRepository.findById(id), notFoundError(FormInput.class, id));
    }
}
