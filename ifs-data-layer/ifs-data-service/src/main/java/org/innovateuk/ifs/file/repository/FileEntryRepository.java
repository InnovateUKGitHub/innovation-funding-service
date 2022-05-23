package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 */
public interface FileEntryRepository extends CrudRepository<FileEntry, Long> {

    List<FileEntry> findFileEntryByIdNotInAndFileUuidIsNull(List<Long> fileEntryIds, Pageable pageable);

}
