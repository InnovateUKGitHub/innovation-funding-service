package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 */
public interface FileEntryRepository extends PagingAndSortingRepository<FileEntry, Long> {

    @Query(value = "SELECT * from ifs.file_entry a " +
            "WHERE a.file_uuid IS NULL " +
            "AND a.id NOT IN " +
            "(SELECT b.file_entry_id from ifs.gluster_migration_status b " +
            "WHERE b.status = 'FILE_NOT_FOUND' OR  b.status = 'FILE_FOUND' ) " +
            "LIMIT :batchSize ", nativeQuery = true)
    List<FileEntry> findFileEntryByStatusAndFileUUIDIsNUll(int batchSize);

}