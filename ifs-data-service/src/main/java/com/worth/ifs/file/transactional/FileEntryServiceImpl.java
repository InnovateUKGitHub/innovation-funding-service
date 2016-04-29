package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

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
    public ServiceResult<FileEntryResource> getFileEntryIdByApplicationFinanceId(Long applicationFinanceId) {
        return find(applicationFinance(applicationFinanceId)).andOnSuccessReturn(finance -> mapper.mapToResource(finance.getFinanceFileEntry()));
    }

    private Supplier<ServiceResult<ApplicationFinance>> applicationFinance(Long applicationFinanceId) {
        return () -> getApplicationFinance(applicationFinanceId);
    }

    private ServiceResult<ApplicationFinance> getApplicationFinance(Long applicationFinanceId) {
        return find(applicationFinanceRepository.findOne(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId));
    }
}