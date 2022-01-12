package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.finance.builder.NoteResourceBuilder.newNoteResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;

public class NoteResourceBuilderTest {

    @Test
    public void buildOne() {

        long contextClassPk = 123L;
        String title = "title";
        ZonedDateTime now = now();
        AttachmentResource attachmentResource = new AttachmentResource(456L,
                                                                       "name",
                                                                       "fileType",
                                                                       123456789L,
                                                                       now());
        PostResource postResource = new PostResource(123L,
                                                     newUserResource().build(),
                                                     "body",
                                                     singletonList(attachmentResource),
                                                     now());

        NoteResource noteResource = newNoteResource()
                .withContextClassPk(contextClassPk)
                .withTitle(title)
                .withPosts(singletonList(postResource))
                .withCreatedOn(now)
                .build();

        assertEquals(contextClassPk, (long) noteResource.contextClassPk);
        assertEquals(title, noteResource.title);
        assertEquals(postResource, noteResource.posts.get(0));
        assertEquals(now, noteResource.createdOn);
    }

    @Test
    public void buildMany() {
        Long[] contextClassPks = {456L, 789L};
        String[] titles = {"title1", "title2"};
        ZonedDateTime[] dates = {now(), now().minusDays(1L)};
        AttachmentResource attachmentResource = new AttachmentResource(456L,
                                                                       "name",
                                                                       "fileType",
                                                                       123456789L,
                                                                       now());
        PostResource postResource = new PostResource(123L,
                                                     newUserResource().build(),
                                                     "body",
                                                     singletonList(attachmentResource),
                                                     now());

        List<NoteResource> noteResources = newNoteResource()
                .withContextClassPk(contextClassPks)
                .withTitle(titles)
                .withPosts(singletonList(postResource))
                .withCreatedOn(dates)
                .build(2);

        assertEquals(contextClassPks[0], noteResources.get(0).contextClassPk);
        assertEquals(titles[0], noteResources.get(0).title);
        assertEquals(postResource, noteResources.get(0).posts.get(0));
        assertEquals(dates[0], noteResources.get(0).createdOn);

        assertEquals(contextClassPks[1], noteResources.get(1).contextClassPk);
        assertEquals(titles[1], noteResources.get(1).title);
        assertEquals(postResource, noteResources.get(1).posts.get(0));
        assertEquals(dates[1], noteResources.get(1).createdOn);
    }
}
