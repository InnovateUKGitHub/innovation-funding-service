package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ContentEventResourceDocs {
    public static final FieldDescriptor[] contentEventResourceFields = {
            fieldWithPath("id").description("Id of the event"),
            fieldWithPath("publicContent").description("Id of the public content resource"),
            fieldWithPath("date").description("The datetime of with the event"),
            fieldWithPath("content").description("The content of by the event")
    };

    public static final ContentEventResourceBuilder contentEventResourceBuilder = newContentEventResource()
            .withId(1L)
            .withPublicContent(2L)
            .withDate(LocalDateTime.now())
            .withContent("content");
}
