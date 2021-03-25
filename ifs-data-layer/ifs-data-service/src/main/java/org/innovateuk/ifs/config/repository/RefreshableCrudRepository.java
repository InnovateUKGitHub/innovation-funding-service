package org.innovateuk.ifs.config.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface RefreshableCrudRepository<T, ID extends Serializable>
        extends CrudRepository<T, ID> {
    void refresh(T t);
}