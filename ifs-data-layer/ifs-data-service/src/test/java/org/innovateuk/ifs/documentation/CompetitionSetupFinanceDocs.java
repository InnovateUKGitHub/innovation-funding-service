package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionSetupFinanceDocs {

    public static final FieldDescriptor[] COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS = {
            fieldWithPath("competitionId").description("The id of the competition"),
            fieldWithPath("applicationFinanceType").description("The type of finances for the application"),
            fieldWithPath("includeGrowthTable").description("The active status of staff count and organisation " +
                    "turnover form inputs are false when this is true"),
            fieldWithPath("includeYourOrganisationSection").description("Flag to indicate if the Your organisation " +
                    "page for Research applications is included"),
            fieldWithPath("includeJesForm").description("Does the competition include the Je-S form for academics"),
    };

}
