package com.worth.ifs.form.repository;

import com.worth.ifs.form.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by worthsystems on 07/01/16.
 */
public interface FormInputTypeRepository extends PagingAndSortingRepository<FormInputType, Long> {
    List<FormInputType> findByTitle(String title);
    FormInputType findOneByTitle(String title);

}
