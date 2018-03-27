package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Populator to convert a {@link QueryResource} into a {@link ThreadViewModel}
 */
@Component
public class ThreadViewModelPopulator {

    private OrganisationService organisationService;

    @Autowired
    public ThreadViewModelPopulator(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    public interface PostLabelingStrategy extends Function<UserResource, String> {

    };

    public List<ThreadViewModel> threadViewModelListFromQueries(long projectId, long organisationId,
                                                                List<QueryResource> queries,
                                                                PostLabelingStrategy userToUsernameFn) {

        List<QueryResource> sortedQueries = queries.stream().
                flatMap(t -> t.posts.stream()
                        .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                .sorted((e1, e2) -> e2.getValue().createdOn.compareTo(e1.getValue().createdOn))
                .map(m -> m.getKey())
                .distinct()
                .collect(Collectors.toList());

        return simpleMap(sortedQueries, query -> threadViewModelFromQuery(projectId, organisationId, query, userToUsernameFn));
    }

    public ThreadViewModel threadViewModelFromQuery(long projectId, long organisationId, QueryResource query, PostLabelingStrategy userToUsernameFn) {

        List<ThreadPostViewModel> posts = addPosts(query.posts, userToUsernameFn);

        return new ThreadViewModel(posts, query.section,
                query.title, query.createdOn, query.id,
                organisationId, projectId, query.closedBy, query.closedDate);
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
            user.hasRole(PROJECT_FINANCE) ?
                user.getName() + " - Innovate UK (Finance team)" :
                user.getName() + " - Innovate UK");

        return new ThreadViewModel(posts, null,
                note.title, note.createdOn, note.id,
                organisationId, projectId, null, null);
    }

    public PostLabelingStrategy projectFinanceOrExternalUserLabelingStrategy() {

        return user -> {

            if (user.isInternalUser()) {
                return user.getName() + " - Innovate UK (Finance team)";
            } else {
                return user.getName() + " - " + organisationService.getOrganisationForUser(user.getId()).getName();
            }
        };
    }

    public PostLabelingStrategy projectFinanceOrExternalUserLabelingStrategyHyphenated() {
        return user -> {
            if (user.isInternalUser()) {
                return "Innovate UK - Finance team";
            } else {
                return user.getName() + " - " + organisationService.getOrganisationForUser(user.getId()).getName();
            }
        };
    }

    private List<ThreadPostViewModel> addPosts(List<PostResource> posts, PostLabelingStrategy userToUsernameFn) {
        return simpleMap(posts, p -> {
            ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
            post.setUsername(userToUsernameFn.apply(p.author));
            return post;
        });
    }
}
