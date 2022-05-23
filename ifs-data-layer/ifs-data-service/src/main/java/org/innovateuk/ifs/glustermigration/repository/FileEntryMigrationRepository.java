package org.innovateuk.ifs.glustermigration.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileEntryMigrationRepository extends JpaRepository<FileEntry, Long> {

    List<FileEntry> findFileEntryByIdNotInAAndFileUuidIsNull(List<Long> fileEntryIds, Pageable pageable);

}
