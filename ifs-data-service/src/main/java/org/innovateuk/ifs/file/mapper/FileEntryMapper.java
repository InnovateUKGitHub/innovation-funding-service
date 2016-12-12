package org.innovateuk.ifs.file.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
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
