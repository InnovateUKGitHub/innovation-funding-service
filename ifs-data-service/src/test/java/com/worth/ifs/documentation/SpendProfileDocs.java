package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SpendProfileDocs {

    public static final FieldDescriptor[] spendProfileTableFields = {
            fieldWithPath("eligibleCostPerCategoryMap").description("Map which holds the total eligible cost per category on the Spend Profile page"),
            fieldWithPath("monthlyCostsPerCategoryMap").description("Map which holds costs per month per category on the Spend Profile page"),
            fieldWithPath("months").description("List of months covered in the Spend Profile"),
            fieldWithPath("markedAsComplete").description("Whether spend profile has been marked as complete or not"),
            fieldWithPath("validationMessages").description("Validation messages pertaining to spend profile table"),
            fieldWithPath("costCategoryResourceMap").description("Cost category Id to Cost category resource mapping"),
            fieldWithPath("costCategoryGroupMap").description("Cost category grouped based on labels")
    };

    public static final FieldDescriptor[] spendProfileCSVFields = {
            fieldWithPath("csvData").description("Spend profile in the CSV format"),
            fieldWithPath("fileName").description("CSV file name consisting of partner name and data and time of the download"),
       };

    public static final FieldDescriptor[] spendProfileResourceFields = {
            fieldWithPath("id").description("Id of the Spend Profile Resource"),
            fieldWithPath("organisation").description("Organisation Id of the Spend Profile"),
            fieldWithPath("project").description("Project Id of the Spend Profile"),
            fieldWithPath("costCategoryType").description("Cost Category Type Id of the Spend Profile"),
            fieldWithPath("eligibleCosts").description("Eligible costs for each category of the Spend Profile"),
            fieldWithPath("spendProfileFigures").description("Spend Profile Figures for each month, for each category of the Spend Profile"),
            fieldWithPath("markedAsComplete").description("Whether spend profile has been marked as complete or not"),
            fieldWithPath("generatedBy").description("The user who generated this Spend Profile"),
            fieldWithPath("generatedDate").description("The date and time at which this Spend Profile was generated"),
            fieldWithPath("approval").description("If the spend profile is approved or rejected")
    };
}
