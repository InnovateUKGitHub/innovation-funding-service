package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class QueryFieldsDocs {

    public static FieldDescriptor[] queryResourceFields() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("Query unique id if already created"),
                fieldWithPath("contextClassPk").description("The primary key / id of the class that holds the context of this Query"),
                fieldWithPath("posts").description("The list of posts of this Query"),
                fieldWithPath("section").description("The Finance Check section to which this Query belongs to."),
                fieldWithPath("title").description("The title of this Query."),
                fieldWithPath("awaitingResponse").description("Informs if this Query is awaiting response."),
                fieldWithPath("createdOn").description("The datetime this Query was created.")
        };
    }
}