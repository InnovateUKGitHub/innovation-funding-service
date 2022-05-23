package org.innovateuk.ifs.glustermigration.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileEntryMigrationRepository extends CrudRepository<FileEntry, Long> {

    List<FileEntry> findFileEntryByIdNotInAAndFileUuidIsNull(List<Long> fileEntryIds, Pageable pageable);

}
