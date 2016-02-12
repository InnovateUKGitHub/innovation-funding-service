package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class FileEntryServiceImpl extends BaseTransactionalService implements FileEntryService {

    @Autowired
    private FileEntryRepository repository;

    @Override
    public ServiceResult<FileEntry> findOne(Long id) {
        return find(() -> repository.findOne(id), notFoundError(FileEntry.class, id));
    }
}