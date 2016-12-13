package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
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
