package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 */
public interface FileEntryRepository extends PagingAndSortingRepository<FileEntry, Long> {

}