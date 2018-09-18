package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionSetupFinanceDocs {

    public static final FieldDescriptor[] COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS = {
            fieldWithPath("competitionId").description("The id of the competition"),

            // @ZeroDowntime(reference = "IFS-4280", description = "This field is being removed from CompetitionSetupFinanceResource")
            fieldWithPath("fullApplicationFinance").description("Full application finance"),

            fieldWithPath("applicationFinanceType").description("The type of finances for the application"),
            fieldWithPath("includeGrowthTable").description("The active status of staff count and organisation " +
                    "turnover form inputs are false when this is true"),
    };

}
