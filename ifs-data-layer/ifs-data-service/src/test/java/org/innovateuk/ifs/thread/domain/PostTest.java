package org.innovateuk.ifs.thread.domain;

import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.nOf;
import static org.junit.Assert.assertEquals;

public class PostTest {

    private Post post;
    private User author;
    private String body;
    private List<Attachment> attachments;
    private ZonedDateTime createdOn;

    @Before
    public void setup() {
        author = newUser().withId(43L).build();
        body = "Post body";
        attachments = new ArrayList<>();
        createdOn = ZonedDateTime.now();
        post = new Post(author, body, attachments, createdOn);
    }

    @Test
    public void testItReturnsValuesAsTheyWereDefined() {
        assertEquals(post.author(), author);
        assertEquals(post.body(), body);
        assertEquals(post.attachments(), attachments);
        assertEquals(post.createdOn(), createdOn);
    }

    @Test
    public void testItReturnsAttachmentInSameOrderAsDefined() {
        attachments = nOf(3, new Attachment());
        Post currentPost = new Post(author, body, attachments, createdOn);
        assertEquals(currentPost.attachments(), attachments);
    }
}
