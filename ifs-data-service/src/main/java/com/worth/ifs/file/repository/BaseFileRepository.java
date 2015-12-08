package com.worth.ifs.file.repository;

import com.worth.ifs.file.domain.BaseFile;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 */
public interface BaseFileRepository<T extends BaseFile> extends PagingAndSortingRepository<T, Long> {

}
