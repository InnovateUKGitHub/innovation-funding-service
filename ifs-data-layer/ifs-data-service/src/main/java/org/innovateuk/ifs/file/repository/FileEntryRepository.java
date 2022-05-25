package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 */
public interface FileEntryRepository extends CrudRepository<FileEntry, Long> {

    @Query("SELECT a from FileEntry a WHERE a.fileUuid IS NULL AND a.id NOT IN (SELECT b.fileEntryId from GlusterMigrationStatus b WHERE b.status = 'FILE_NOT_FOUND' )")
    List<FileEntry> findFileEntryByStatusAndFileUUIDIsNUll(Pageable pageable);

}
