package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public abstract class PageResourceDocs
{
    public static final FieldDescriptor[] pageResourceFields = {
            fieldWithPath("content[]").description("The current page's list of items."),
            fieldWithPath("number").description("The current page number."),
            fieldWithPath("size").description("The maximum size of any page's list of items."),
            fieldWithPath("totalElements").description("The total number of elements across all pages."),
            fieldWithPath("totalPages").description("The total number of all pages."),
    };
}
