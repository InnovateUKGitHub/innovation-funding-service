package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class AssessmentLookupStrategy {
    @Autowired
    private AssessmentRepository assessmentRepository;

    @PermissionEntityLookupStrategy
    public Assessment findById(Long id){
        return assessmentRepository.findById(id);
    }
}
