package org.innovateuk.ifs.thread.domain;

import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.domain.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;

public class NoteTest {
    private Note note;
    private Long id;
    private Long classPk;
    private String className;
    private List<Post> posts;
    private String title;
    private ZonedDateTime createdOn;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        classPk = 22L;
        className = "org.innovateuk.ifs.class";
        posts = new ArrayList<>();
        title = "Test Note Title";
        createdOn = ZonedDateTime.now();

        note = new Note(id, classPk, className, posts, title, createdOn);
    }

    @Test
    public void testItReturnsValuesAsTheyWereDefined() throws Exception {
        Assert.assertEquals(note.id(), id);
        Assert.assertEquals(note.contextClassPk(), classPk);
        Assert.assertEquals(note.contextClassName(), className);
        Assert.assertEquals(note.posts(), posts);
        Assert.assertEquals(note.title(), title);
        Assert.assertEquals(note.createdOn(), createdOn);
    }

    @Test
    public void testItReturnsOptionalEmptyWhenNoPosts() {
        Assert.assertEquals(note.latestPost(), Optional.empty());
    }

    @Test
    public void testItReturnsLatestAddedPostCorrectly() {
        final Post p1 = new Post(33L, null, null, null, null);
        final Post p2 = new Post(44L, null, null, null, null);
        note.addPost(p1);
        note.addPost(p2);
        Assert.assertEquals(note.latestPost(), of(p2));
    }
}
