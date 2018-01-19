package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Populator to convert a {@link QueryResource} into a {@link ThreadViewModel}
 */
@Component
public class ThreadViewModelPopulator {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationService organisationService;

    public List<ThreadViewModel> threadViewModelListFromQueries(long projectId, long organisationId, List<QueryResource> queries) {

        List<QueryResource> sortedQueries = queries.stream().
                flatMap(t -> t.posts.stream()
                        .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                .sorted((e1, e2) -> e2.getValue().createdOn.compareTo(e1.getValue().createdOn))
                .map(m -> m.getKey())
                .distinct()
                .collect(Collectors.toList());

        return simpleMap(sortedQueries, query -> threadViewModelFromQuery(projectId, organisationId, query));
    }

    public ThreadViewModel threadViewModelFromQuery(long projectId, long organisationId, QueryResource query) {

        List<ThreadPostViewModel> posts = addPosts(query.posts, user ->
            user.hasRole(UserRoleType.PROJECT_FINANCE) ?
                user.getName() + " - Innovate UK (Finance team)" :
                user.getName() + " - " + organisationService.getOrganisationForUser(user.getId()).getName());

        return new ThreadViewModel(posts, query.section,
                query.title, query.awaitingResponse, query.createdOn, query.id,
                organisationId, projectId, false);
    }

    public List<ThreadViewModel> threadViewModelListFromNotes(long projectId, long organisationId, List<NoteResource> notes) {

        List<NoteResource> sortedNotes = notes.stream().
                flatMap(t -> t.posts.stream()
                        .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                .sorted((e1, e2) -> e2.getValue().createdOn.compareTo(e1.getValue().createdOn))
                .map(m -> m.getKey())
                .distinct()
                .collect(Collectors.toList());

        return simpleMap(sortedNotes, note -> threadViewModelFromNote(projectId, organisationId, note));
    }

    public ThreadViewModel threadViewModelFromNote(long projectId, long organisationId, NoteResource note) {

        List<ThreadPostViewModel> posts = addPosts(note.posts, user ->
            user.hasRole(UserRoleType.PROJECT_FINANCE) ?
                user.getName() + " - Innovate UK (Finance team)" :
                user.getName() + " - Innovate UK");

        return new ThreadViewModel(posts, null,
                note.title, false, note.createdOn, note.id,
                organisationId, projectId,false);
    }

    private List<ThreadPostViewModel> addPosts(List<PostResource> posts, Function<UserResource, String> userToUsernameFn) {
        return simpleMap(posts, p -> {
            UserResource user = userService.findById(p.author.getId());
            ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
            post.setUsername(userToUsernameFn.apply(user));
            return post;
        });
    }
}
