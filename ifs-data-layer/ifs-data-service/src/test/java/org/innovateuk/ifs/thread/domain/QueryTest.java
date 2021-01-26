package org.innovateuk.ifs.thread.domain;

import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Optional.of;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.*;

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
    public void itReturnsValuesAsTheyWereDefined() throws Exception {
        assertEquals(query.id(), id);
        assertEquals(query.contextClassPk(), classPk);
        assertEquals(query.contextClassName(), className);
        assertEquals(query.posts(), posts);
        assertEquals(query.section(), section);
        assertEquals(query.title(), title);
        assertEquals(query.createdOn(), createdOn);
    }

    @Test
    public void itReturnsOptionalEmptyWhenNoPosts() {
        assertEquals(query.latestPost(), Optional.empty());
    }

    @Test
    public void itReturnsLatestAddedPostCorrectly() {
        final Post p1 = new Post(33L, null, null, null, null);
        final Post p2 = new Post(44L, null, null, null, null);
        query.addPost(p1);
        query.addPost(p2);
        assertEquals(query.latestPost(), of(p2));
    }

    @Test
    public void isAwaitingResponsePositive() {
        addPostWithUserHavingRole(PROJECT_FINANCE);
        assertTrue(query.isAwaitingResponse());
    }

    @Test
    public void isAwaitingResponseNegative() {
        addPostWithUserHavingRole(COMP_ADMIN);
        assertFalse(query.isAwaitingResponse());
    }

    @Test
    public void closeThread() {

        User closingUser = new User();
        query.closeThread(closingUser);

        assertSame(closingUser, query.getClosedBy());
        ZonedDateTime closedDate = query.getClosedDate();
        ZonedDateTime closedDateWithTolerance = closedDate.plus(50, ChronoUnit.MILLIS);

        ZonedDateTime now = ZonedDateTime.now();
        assertTrue(closedDateWithTolerance.isAfter(now) && !closedDate.isAfter(now));
    }

    private void addPostWithUserHavingRole(Role role) {
        final User user = newUser().withRoles(singleton(role)).build();
        final Post p1 = new Post(33L, user, null, null, null);
        query.addPost(p1);
    }
}