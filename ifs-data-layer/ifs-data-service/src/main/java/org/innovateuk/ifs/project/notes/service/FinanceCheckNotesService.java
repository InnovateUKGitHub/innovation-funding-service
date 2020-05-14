package org.innovateuk.ifs.project.notes.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.service.MessageThreadService;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FinanceCheckNotesService extends MessageThreadService<NoteResource, PostResource> {
    @Override
    @PostFilter("hasPermission(filterObject, 'NOTES_READ')")
    ServiceResult<List<NoteResource>> findAll(Long contextClassPk);

    @Override
    @PostAuthorize("hasPermission(returnObject, 'PF_READ')")
    ServiceResult<NoteResource> findOne(Long id);

    @Override
    @PreAuthorize("hasPermission(#noteResource, 'PF_CREATE')")
    ServiceResult<Long> create(@P("noteResource") NoteResource noteResource);

    @Override
    @PreAuthorize("hasAnyAuthority('project_finance', 'external_finance')")
    @SecuredBySpring(value = "CLOSE_NOTE", description = "Only project finance users can close notes")
    ServiceResult<Void> close(Long noteId);

    @Override
    @PreAuthorize("hasPermission(#noteId, 'org.innovateuk.ifs.threads.resource.NoteResource', 'PF_ADD_POST')")
    ServiceResult<Void> addPost(PostResource post, @P("noteId") Long noteId);
}