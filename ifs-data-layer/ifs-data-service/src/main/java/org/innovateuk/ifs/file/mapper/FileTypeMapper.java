package org.innovateuk.ifs.file.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class FileTypeMapper extends BaseMapper<FileType, FileTypeResource, Long> {

    @Override
    public abstract FileTypeResource mapToResource(FileType domain);

    @Override
    public abstract FileType mapToDomain(FileTypeResource resource);

    public Long mapFileTypeToId(FileType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}

