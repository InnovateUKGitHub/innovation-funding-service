package com.worth.ifs.file.resource;

import com.worth.ifs.file.domain.BaseFile;
import com.worth.ifs.file.repository.BaseFileRepository;
import com.worth.ifs.file.repository.FormInputResponseFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A factory for converting BaseFile implementations to BaseFileResource implementations
 */
@Component
public class BaseFileConverter {

    @Autowired
    private FormInputResponseFileRepository formInputResponseFileRepository;

    public BaseFile valueOf(BaseFileResource resource) {
        return null;
    }

    public BaseFileResource valueOf(BaseFile file) {
        return null;
    }

    public <T extends BaseFile> BaseFileRepository<T> getRepository(Class<T> clazz) {
        return (BaseFileRepository<T>) formInputResponseFileRepository;
    }

    public <T extends BaseFile> T save(T baseFile) {
        BaseFileRepository<T> repository = (BaseFileRepository<T>) getRepository(baseFile.getClass());
        return save(baseFile, repository);
    }

    public <T extends BaseFile> T save(T baseFile, BaseFileRepository<T> repository) {
        return repository.save(baseFile);
    }
}
