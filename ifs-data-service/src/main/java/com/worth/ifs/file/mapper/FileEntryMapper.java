package com.worth.ifs.file.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {

    }
)
public abstract class FileEntryMapper extends BaseMapper<FileEntry, FileEntryResource, Long> {

    public Long mapFileEntryToId(FileEntry object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}