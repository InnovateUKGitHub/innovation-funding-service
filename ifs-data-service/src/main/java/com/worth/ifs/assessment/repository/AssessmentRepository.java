package com.worth.ifs.assessment.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by nunoalexandre on 16/09/15.
 */

public interface AssessmentRepository extends PagingAndSortingRepository<Assessment, Long> {
    Assessment findById(@Param("id") Long id);
    List<Assessment> findAll();
}
