package com.worth.ifs.application.security;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class QuestionResourceLookupStrategy {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @PermissionEntityLookupStrategy
    public QuestionResource findResourceById(Long id){
        return questionMapper.mapToResource(questionRepository.findOne(id));
    }
}
