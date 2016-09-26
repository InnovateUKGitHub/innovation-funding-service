package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class SectionLookupStrategy {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionMapper sectionMapper;

    @PermissionEntityLookupStrategy
    public Section findById(Long id){
        return sectionRepository.findOne(id);
    }

    @PermissionEntityLookupStrategy
    public SectionResource findResourceById(Long id){
        return sectionMapper.mapToResource(sectionRepository.findOne(id));
    }
}
