package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SpendProfileDocs {
    public static final FieldDescriptor[] spendProfileResourceFields = {
            fieldWithPath("id").description("Id of the Spend Profile Resource"),
            fieldWithPath("eligibleCostPerCategoryMap").description("Map which holds the total eligible cost per category on the Spend Profile page")
    };
}
