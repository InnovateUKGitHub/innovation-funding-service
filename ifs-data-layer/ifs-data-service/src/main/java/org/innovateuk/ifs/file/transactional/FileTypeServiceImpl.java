package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileTypeService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of File Type
 */
@Service
public class FileTypeServiceImpl extends BaseTransactionalService implements FileTypeService {

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private FileTypeMapper fileTypeMapper;


    @Override
    @Transactional
    public ServiceResult<FileTypeResource> findOne(long id) {

        return find(fileTypeRepository.findOne(id), notFoundError(FileType.class, id))
                .andOnSuccessReturn(fileType -> fileTypeMapper.mapToResource(fileType));
    }

    @Override
    @Transactional
    public ServiceResult<FileTypeResource> findByName(String name) {

        return find(fileTypeRepository.findByName(name), notFoundError(FileType.class, name))
                .andOnSuccessReturn(fileType -> fileTypeMapper.mapToResource(fileType));
    }
}

