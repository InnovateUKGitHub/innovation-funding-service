package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SpendProfileTableDocs {
    public static final FieldDescriptor[] spendProfileTableFields = {
            fieldWithPath("eligibleCostPerCategoryMap").description("Map which holds the total eligible cost per category on the Spend Profile page"),
            fieldWithPath("monthlyCostsPerCategoryMap").description("Map which holds costs per month per category on the Spend Profile page"),
            fieldWithPath("months").description("List of months covered in the Spend Profile")
    };
}
