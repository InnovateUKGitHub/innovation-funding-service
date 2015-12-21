package com.worth.ifs.file.repository;

import com.worth.ifs.file.domain.FileEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 */
public interface FileEntryRepository extends PagingAndSortingRepository<FileEntry, Long> {

}
