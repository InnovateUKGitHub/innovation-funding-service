package org.innovateuk.ifs.project.state.transactional;


import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.threads.domain.ProjectStateComments;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.ProjectStateCommentsMapper;
import org.innovateuk.ifs.threads.repository.ProjectStateCommentsRepository;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateHistoryResource;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Service
public class ProjectStateCommentsServiceImpl implements ProjectStateCommentsService {

    private MappingThreadService<ProjectStateComments, ProjectStateHistoryResource, ProjectStateCommentsMapper, Project> service;
    private AuthenticationHelper authenticationHelper;

    @Autowired
    public ProjectStateCommentsServiceImpl(ProjectStateCommentsRepository threadRepository, AuthenticationHelper authenticationHelper, ProjectStateCommentsMapper threadMapper, PostMapper postMapper) {
        service = new MappingThreadService<>(threadRepository, authenticationHelper, threadMapper, postMapper, Project.class);
    }

    @Override
    public ServiceResult<Long> create(long projectId, ProjectState state) {
        UserResource user = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String text = textForState(state);
        PostResource post = new PostResource(null, user, text, emptyList(), now());
        return service.create(new ProjectStateHistoryResource(null, projectId, singletonList(post), state,
                text, now(), null, null));
    }

    private String textForState(ProjectState state) {
        switch (state) {
            case COMPLETED_OFFLINE:
                return "Project has been completed offline.";
            case HANDLED_OFFLINE:
                return "Project has been handled offline.";
            case WITHDRAWN:
                return "Project has been withdrawn.";
            default:
                throw new IFSRuntimeException(String.format("Unsupported state change to %s", state.name()));
        }
    }

    @Override
    public ServiceResult<List<ProjectStateHistoryResource>> findAll(Long contextClassPk) {
        return service.findAll(contextClassPk);
    }

    @Override
    public ServiceResult<ProjectStateHistoryResource> findOne(Long threadId) {
        return service.findOne(threadId);
    }

    @Override
    public ServiceResult<Long> create(ProjectStateHistoryResource projectStateHistoryResource) {
        throw new UnsupportedOperationException("Create by resource not valid for project state comments.");
    }

    @Override
    public ServiceResult<Void> close(Long threadId) {
        return service.close(threadId);
    }

    @Override
    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(post, threadId);
    }
}