package org.innovateuk.ifs.project.state.transactional;


import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.threads.domain.ProjectStateComments;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.ProjectStateCommentsMapper;
import org.innovateuk.ifs.threads.repository.ProjectStateCommentsRepository;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.innovateuk.ifs.threads.service.MappingMessageThreadService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectStateCommentsServiceImpl extends BaseTransactionalService implements ProjectStateCommentsService {

    private MappingMessageThreadService<ProjectStateComments, ProjectStateCommentsResource, ProjectStateCommentsMapper, Project> service;
    private ProjectStateCommentsRepository projectStateCommentsRepository;
    private ProjectStateCommentsMapper projectStateCommentsMapper;


    @Autowired
    public ProjectStateCommentsServiceImpl(ProjectStateCommentsRepository projectStateCommentsRepository, AuthenticationHelper authenticationHelper, ProjectStateCommentsMapper projectStateCommentsMapper, PostMapper postMapper) {
        this.service = new MappingMessageThreadService<>(projectStateCommentsRepository, authenticationHelper, projectStateCommentsMapper, postMapper, Project.class);
        this.projectStateCommentsMapper = projectStateCommentsMapper;
        this.projectStateCommentsRepository = projectStateCommentsRepository;

    }

    @Override
    @Transactional
    public ServiceResult<Long> create(long projectId, ProjectState state) {
        return service.create(commentsFromState(projectId, state))
                .andOnSuccess(threadId -> closeAllOtherComments(threadId, projectId));
    }

    @Override
    @Transactional
    public ServiceResult<Long> create(long projectId, ProjectState state, OnHoldReasonResource reason) {
        return service.create(commentsFromStateAndReason(projectId, state, reason))
                .andOnSuccess(threadId -> closeAllOtherComments(threadId, projectId));
    }

    @Override
    public ServiceResult<List<ProjectStateCommentsResource>> findAll(Long contextClassPk) {
        return service.findAll(contextClassPk);
    }

    @Override
    public ServiceResult<ProjectStateCommentsResource> findOne(Long threadId) {
        return service.findOne(threadId);
    }

    @Override
    public ServiceResult<ProjectStateCommentsResource> findOpenComment(long projectId) {
        return find(projectStateCommentsRepository.findOpenComments(projectId), notFoundError(ProjectStateComments.class, projectId))
                .andOnSuccessReturn(projectStateCommentsMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(post, threadId);
    }

    private ServiceResult<Long> closeAllOtherComments(long threadId, long projectId) {
        return service.findAll(projectId)
                .andOnSuccessReturn(comments -> {
                    comments.forEach(comment -> {
                        if (threadId != comment.id) {
                            service.close(comment.id);
                        }
                    });
                    return threadId;
                });
    }


    private ProjectStateCommentsResource commentsFromStateAndReason(long projectId, ProjectState state, OnHoldReasonResource reason) {
        String text = textForState(state);
        return newComments(projectId, state, text + " - " + reason.getTitle(), reason.getBody());
    }

    private ProjectStateCommentsResource commentsFromState(long projectId, ProjectState state) {
        String text = textForState(state);
        return newComments(projectId, state, text, text);
    }

    private ProjectStateCommentsResource newComments(long projectId, ProjectState state, String title, String body) {
        UserResource user = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        PostResource post = new PostResource(null, user, body, emptyList(), now());
        return new ProjectStateCommentsResource(null, projectId, singletonList(post), state,
                title, now(), null, null);
    }

    private String textForState(ProjectState state) {
        switch (state) {
            case COMPLETED_OFFLINE:
                return "Project has been completed offline";
            case HANDLED_OFFLINE:
                return "Project has been handled offline";
            case WITHDRAWN:
                return "Project has been withdrawn";
            case ON_HOLD:
                return "Marked as on hold";
            case SETUP:
                return "Project has been resumed from on hold";
            case UNSUCCESSFUL:
                return "Project has been marked as unsuccessful";
            default:
                throw new IFSRuntimeException(String.format("Unsupported state change to %s", state.name()));
        }
    }
}