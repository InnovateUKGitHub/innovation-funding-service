package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class FileEntryServiceImpl extends BaseTransactionalService implements FileEntryService {

    @Autowired
    private FileEntryRepository repository;

    @Autowired
    private FileEntryMapper mapper;

    @Override
    public ServiceResult<FileEntryResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(FileEntry.class, id)).andOnSuccessReturn(mapper::mapFileEntryToResource);
    }
}