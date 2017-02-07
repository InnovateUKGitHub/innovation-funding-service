package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectFinanceNotesService extends ThreadService<NoteResource, PostResource> {
    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<NoteResource>> findAll(Long contextClassPk);

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<NoteResource> findOne(Long contextClassPk);

    @Override
    @PreAuthorize("hasPermission(#noteResource, 'CREATE')")
    ServiceResult<Long> create(NoteResource noteResource);

    @Override
    @PreAuthorize("hasPermission(#noteId, 'org.innovateuk.threads.resource.NoteResource', 'ADD_POST')")
    ServiceResult<Void> addPost(PostResource post, Long noteId);
}
