package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.mapper.QuestionStatusMapper;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**Responsible for doing lookups of QuestionStatusResources for permission methods*/
@Component
@PermissionEntityLookupStrategies
public class QuestionStatusResourceLookupStrategy {

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private QuestionStatusMapper questionStatusMapper;

    @PermissionEntityLookupStrategy
    public QuestionStatusResource findResourceById(Long id){
        return questionStatusMapper.mapIdToResource(id);
    }
}
