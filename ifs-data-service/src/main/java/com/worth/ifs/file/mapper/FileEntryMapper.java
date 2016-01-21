package com.worth.ifs.file.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {

    }
)
public abstract class FileEntryMapper {

    @Autowired
    private FileEntryRepository repository;

    public abstract FileEntryResource mapFileEntryToResource(FileEntry object);

    public abstract FileEntry resourceToFileEntry(FileEntryResource resource);

    public static final FileEntryMapper FileEntryMAPPER = Mappers.getMapper(FileEntryMapper.class);

    public Long mapFileEntryToId(FileEntry object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public FileEntry mapIdToFileEntry(Long id) {
        return repository.findOne(id);
    }

    public Long mapFileEntryResourceToId(FileEntryResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}