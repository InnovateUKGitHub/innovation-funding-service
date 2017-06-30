package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PublicContentItemResourceDocs {
    public static final FieldDescriptor[] publicContentItemPageResourceFields = {
            fieldWithPath("totalElements").description("Total items of elements found"),
            fieldWithPath("totalPages").description("Total pages with found items"),
            fieldWithPath("content").description("List of public content items found on this page"),
            fieldWithPath("number").description("The number of the current page"),
            fieldWithPath("size").description("The size of the current page"),
    };

    public static final FieldDescriptor[] publicContentItemResourceFields = {
            fieldWithPath("publicContentResource").description("The public content resource"),
            fieldWithPath("competitionTitle").description("Title of the competition linked to the public content resource"),
            fieldWithPath("competitionOpenDate").description("Opening date of the competition linked to the public content resource"),
            fieldWithPath("competitionCloseDate").description("Closing date of the competition linked to the public content resource"),
            fieldWithPath("nonIfsUrl").description("The URL to apply to a competition if it is not managed via IFS"),
            fieldWithPath("nonIfs").description("Boolean that indicates if this is a non IFS competition"),
            fieldWithPath("setupComplete").description(("Boolean that indicates if this competition has completed setup"))
    };
}
