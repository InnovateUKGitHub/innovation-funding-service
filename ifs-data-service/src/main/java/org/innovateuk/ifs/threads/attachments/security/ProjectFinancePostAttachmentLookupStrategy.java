package org.innovateuk.ifs.threads.attachments.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ProjectFinancePostAttachmentLookupStrategy {

    @Autowired
    private NoteRepository repository;

    @Autowired
    private NoteMapper mapper;

    @PermissionEntityLookupStrategy
    public NoteResource findById(final Long id) {
        return mapper.mapToResource(repository.findOne(id));
    }

}