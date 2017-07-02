package org.innovateuk.ifs.project.financecheck.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Field descriptors for Controller method documentation for methods returning ProjectFinances
 */
public class ProjectFinanceResponseFields {
    public static final FieldDescriptor[] projectFinanceFields = {
            fieldWithPath("[]").description("An array of ProjectFinances for the given projectId, one for each Organisation"),
            fieldWithPath("[].id").description("Id of the ProjectFinance record"),
            fieldWithPath("[].organisation").description("The Organisation id to which the ProjectFinance belongs"),
            fieldWithPath("[].target").description("The id of the project to which the ProjectFinance belongs"),
            fieldWithPath("[].organisationSize").description("The organisation size of the owning organisation as recorded against their finances"),
            fieldWithPath("[].financeOrganisationDetails").description("A detailed breakdown of the organisation's finances"),
    };

    public static final FieldDescriptor[] projectFinanceGrantClaimRowFields = {
            fieldWithPath("id").description("Id of cost item"),
            fieldWithPath("total").description("Total amount of cost item"),
            fieldWithPath("costType").description("Type of cost (e.g. FINANCE, ACADEMIC)"),
            fieldWithPath("name").description("Name of cost item row"),
            fieldWithPath("empty").description("Flag indicating if cost item is empty"),
            fieldWithPath("minRows").description("Minimum rows required"),
            fieldWithPath("type").description("Type of cost item"),
            fieldWithPath("grantClaimPercentage").description("Percentage grant claim being requested.")
    };
}
