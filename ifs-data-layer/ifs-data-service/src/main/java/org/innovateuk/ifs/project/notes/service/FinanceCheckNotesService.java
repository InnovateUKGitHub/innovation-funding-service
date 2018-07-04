package org.innovateuk.ifs.project.notes.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FinanceCheckNotesService extends ThreadService<NoteResource, PostResource> {
    @Override
    @PostFilter("hasPermission(filterObject, 'PF_READ')")
    ServiceResult<List<NoteResource>> findAll(Long contextClassPk);

    @Override
    @PostAuthorize("hasPermission(returnObject, 'PF_READ')")
    ServiceResult<NoteResource> findOne(Long id);

    @Override
    @PreAuthorize("hasPermission(#noteResource, 'PF_CREATE')")
    ServiceResult<Long> create(@P("noteResource") NoteResource noteResource);

    @Override
    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "CLOSE_NOTE", description = "Only project finance users can close notes")
    ServiceResult<Void> close(Long noteId);

    @Override
    @PreAuthorize("hasPermission(#noteId, 'org.innovateuk.ifs.threads.resource.NoteResource', 'PF_ADD_POST')")
    ServiceResult<Void> addPost(PostResource post, @P("noteId") Long noteId);
}