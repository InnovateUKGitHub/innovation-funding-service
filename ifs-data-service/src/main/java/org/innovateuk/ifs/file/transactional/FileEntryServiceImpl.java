package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FileEntryServiceImpl extends BaseTransactionalService implements FileEntryService {

    @Autowired
    private FileEntryRepository repository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private FileEntryMapper mapper;

    @Override
    public ServiceResult<FileEntryResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(FileEntry.class, id)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> saveFile(FileEntryResource newFile) {
        return serviceSuccess(mapper.mapToResource(repository.save(mapper.mapToDomain(newFile))));
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeFile(Long fileId) {
        repository.delete(fileId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<FileEntryResource> getFileEntryByApplicationFinanceId(Long applicationFinanceId) {
        return find(applicationFinance(applicationFinanceId)).andOnSuccessReturn(finance -> mapper.mapToResource(finance.getFinanceFileEntry()));
    }

    private Supplier<ServiceResult<ApplicationFinance>> applicationFinance(Long applicationFinanceId) {
        return () -> getApplicationFinance(applicationFinanceId);
    }

    private ServiceResult<ApplicationFinance> getApplicationFinance(Long applicationFinanceId) {
        return find(applicationFinanceRepository.findOne(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId));
    }
}
