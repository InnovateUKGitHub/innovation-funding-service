package com.worth.ifs.validator.repository;

import com.worth.ifs.validator.domain.Validator;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ValidatorRepository extends CrudRepository<Validator, Long> {
    List<Validator> findAll();

}
