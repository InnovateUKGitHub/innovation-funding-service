package com.worth.ifs.documentation;

import com.worth.ifs.user.builder.ContractResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static java.lang.Boolean.TRUE;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ContractDocs {

    public static final FieldDescriptor[] contractResourceFields = {
            fieldWithPath("id").description("Id of the contract"),
            fieldWithPath("current").description("Flag to signify if this is the current contract"),
            fieldWithPath("text").description("Text of the contract"),
            fieldWithPath("annexA").description("Text of annex A"),
            fieldWithPath("annexB").description("Text of annex B"),
            fieldWithPath("annexC").description("Text of annex C")
    };

    public static final ContractResourceBuilder contractResourceBuilder = newContractResource()
            .with(id(1L))
            .withCurrent(TRUE)
            .withText("Contract text...")
            .withAnnexA("Annex A text...")
            .withAnnexB("Annex B text...")
            .withAnnexC("Annex C text...");
}