package org.innovateuk.ifs.thread.domain;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.user.domain.User;

import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;

public class PostTest {

    private Post post;
    private Long id;
    private User author;
    private String body;
    private List<FileEntry> attachments;
    private LocalDateTime createdOn;

    @Before
    public void setup() {
        id = 23L;
        author = newUser().withId(43L).build();
        body = "Post body";
        attachments = new ArrayList<>();
        createdOn = LocalDateTime.now();
        post = new Post(id, author, body, attachments, createdOn);
    }

    @Test
    public void testItReturnsValuesAsTheyWereDefined() {
        assertEquals(post.id(), id);
        assertEquals(post.author(), author);
        assertEquals(post.body(), body);
        assertEquals(post.attachments(), attachments);
        assertEquals(post.createdOn(), createdOn);
    }

    @Test
    public void testItReturnsAttachmentInSameOrderAsDefined() {
        attachments = newFileEntry().withMediaType("pdf").build(3);
        Post currentPost = new Post(id, author, body, attachments, createdOn);
        assertEquals(currentPost.attachments(), attachments);
    }
}
