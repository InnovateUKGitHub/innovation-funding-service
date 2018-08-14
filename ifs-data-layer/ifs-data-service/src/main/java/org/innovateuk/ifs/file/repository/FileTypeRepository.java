package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileType;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FileTypeRepository extends CrudRepository<FileType, Long> {

    FileType findByName(String name);
}


