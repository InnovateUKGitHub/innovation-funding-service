package org.innovateuk.ifs.project.state.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.innovateuk.ifs.threads.service.MessageThreadService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface ProjectStateCommentsService extends MessageThreadService<ProjectStateCommentsResource, PostResource> {

    @NotSecured("Not secured")
    ServiceResult<Long> create(long projectId, ProjectState state);

    @NotSecured("Not secured")
    ServiceResult<Long> create(long projectId, ProjectState state, OnHoldReasonResource reason);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_STATE_COMMENTS_FIND_ALL", securedType = ProjectStateCommentsResource.class,
            description = "Project finance users can find all project state comments.")
    @Override
    ServiceResult<List<ProjectStateCommentsResource>> findAll(Long projectId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_STATE_COMMENTS_FIND_ONE", securedType = ProjectStateCommentsResource.class,
            description = "Project finance users can find project state comments.")
    @Override
    ServiceResult<ProjectStateCommentsResource> findOne(Long threadId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_STATE_COMMENTS_FIND_OPEN", securedType = ProjectStateCommentsResource.class,
            description = "Project finance users can find open project state comments.")
    ServiceResult<ProjectStateCommentsResource> findOpenComment(long projectId);

    @NotSecured("Not secured")
    @Override
    default ServiceResult<Long> create(ProjectStateCommentsResource e) {
        throw new UnsupportedOperationException("Create by resource not valid for project state comments.");
    }

    @NotSecured("Not secured")
    @Override
    default ServiceResult<Void> close(Long threadId) {
        throw new UnsupportedOperationException("Close not valid for project state comments.");
    }

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_STATE_COMMENTS_ADD_POST", securedType = ProjectStateCommentsResource.class,
            description = "Project finance users can add posts to project comments")
    @Override
    ServiceResult<Void> addPost(PostResource post, Long threadId);
}