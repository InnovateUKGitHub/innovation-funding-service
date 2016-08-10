package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionSearchResultDocs {
    public static final FieldDescriptor[] competitionSearchResultFields = {
            fieldWithPath("totalElements").description("The total number of competitions the search query matched."),
            fieldWithPath("totalPages").description("The total number of pages the search query matched."),
            fieldWithPath("content").description("The list of competitions in this page of search results."),
            fieldWithPath("number").description("The page number of this search result."),
            fieldWithPath("size").description("The size of a page of search results.")
    };
}
