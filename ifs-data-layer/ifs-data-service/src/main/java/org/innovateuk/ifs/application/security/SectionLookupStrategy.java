package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.mapper.SectionMapper;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
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
