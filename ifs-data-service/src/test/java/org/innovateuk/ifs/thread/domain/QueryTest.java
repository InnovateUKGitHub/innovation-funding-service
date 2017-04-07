package org.innovateuk.ifs.thread.domain;

import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;

public class QueryTest {

    private Query query;
    private Long id;
    private Long classPk;
    private String className;
    private List<Post> posts;
    private FinanceChecksSectionType section;
    private String title;
    private ZonedDateTime createdOn;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        classPk = 22L;
        className = "org.innovateuk.ifs.class";
        posts = new ArrayList<>();
        section = FinanceChecksSectionType.VIABILITY;
        title = "Test Query Title";
        createdOn = ZonedDateTime.now();

        query = new Query(id, classPk, className, posts, section, title, createdOn);
    }

    @Test
    public void testItReturnsValuesAsTheyWereDefined() throws Exception {
        Assert.assertEquals(query.id(), id);
        Assert.assertEquals(query.contextClassPk(), classPk);
        Assert.assertEquals(query.contextClassName(), className);
        Assert.assertEquals(query.posts(), posts);
        Assert.assertEquals(query.section(), section);
        Assert.assertEquals(query.title(), title);
        Assert.assertEquals(query.createdOn(), createdOn);
    }

    @Test
    public void testItReturnsOptionalEmptyWhenNoPosts() {
        Assert.assertEquals(query.latestPost(), Optional.empty());
    }

    @Test
    public void testItReturnsLatestAddedPostCorrectly() {
        final Post p1 = new Post(33L, null, null, null, null);
        final Post p2 = new Post(44L, null, null, null, null);
        query.addPost(p1);
        query.addPost(p2);
        Assert.assertEquals(query.latestPost(), of(p2));
    }

    @Test
    public void testIsAwaitingResponsePositive() {
        addPostWithUserHavingRole(UserRoleType.PROJECT_FINANCE);
        Assert.assertTrue(query.isAwaitingResponse());
    }

    @Test
    public void testIsAwaitingResponseNegative() {
        addPostWithUserHavingRole(UserRoleType.FINANCE_CONTACT);
        Assert.assertFalse(query.isAwaitingResponse());
    }

    private void addPostWithUserHavingRole(UserRoleType role) {
        final User user = newUser().withRoles(newRole().withType(role).buildSet(1)).build();
        final Post p1 = new Post(33L, user, null, null, null);
        query.addPost(p1);
    }
}