package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.publiccontent.builder.PublicContentEventResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentEventResourceBuilder.newPublicContentEventResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PublicContentEventResourceDocs {
    public static final FieldDescriptor[] publicContentEventResourceFields = {
            fieldWithPath("id").description("Id of the event"),
            fieldWithPath("publicContent").description("Id of the public content resource"),
            fieldWithPath("date").description("The datetime of with the event"),
            fieldWithPath("content").description("The content of by the event")
    };

    public static final PublicContentEventResourceBuilder publicContentEventResourceBuilder = newPublicContentEventResource()
            .withId(1L)
            .withPublicContent(2L)
            .withDate(LocalDateTime.now())
            .withContent("content");
}
