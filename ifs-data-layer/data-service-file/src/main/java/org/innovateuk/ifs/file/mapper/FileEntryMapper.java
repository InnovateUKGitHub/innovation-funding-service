package org.innovateuk.ifs.file.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Optional;

@Mapper(
        config = GlobalMapperConfig.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class FileEntryMapper extends BaseMapper<FileEntry, FileEntryResource, Long> {

    public Long mapFileEntryToId(FileEntry object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public FileEntryResource mapToResource(Optional<FileEntry> competitionTerms) {
        return competitionTerms.map(this::mapToResource).orElse(null);
    }
}