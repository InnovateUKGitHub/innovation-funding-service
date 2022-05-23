package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 */
public interface FileEntryRepository extends PagingAndSortingRepository<FileEntry, Long> {

    @Query("Select a from FileEntry a where a.id not in (:fileIds)")
    List<FileEntry> findByNullUUID(Pageable pageable, List<Long> fileIds);

}
