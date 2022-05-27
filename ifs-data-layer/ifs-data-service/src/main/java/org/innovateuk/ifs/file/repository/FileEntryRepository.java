package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 */
public interface FileEntryRepository extends PagingAndSortingRepository<FileEntry, Long> {

    @Query("SELECT t1 " +
            "FROM FileEntry t1 " +
            "LEFT JOIN  GlusterMigrationStatus t3 ON t3.fileEntryId <> t1.id " +
            "AND t3.status = 'FILE_FOUND' " +
            "LEFT JOIN GlusterMigrationStatus t2 ON t2.fileEntryId <> t1.id " +
            "AND t2.status = 'FILE_NOT_FOUND'")
    Page<FileEntry> findFileEntryByStatusAndFileUUIDIsNUll(Pageable pageable);

}