package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * FormInputType is used to identify what response a FormInput needs.
 * This is also used to choose a template in the web-service. Depending on the FormInputType we
 * can also implement extra behaviour like form / input validation.
 */
public enum FormInputType implements IdentifiableEnum {

    TEXTAREA(2),
    FILEUPLOAD(4),
    APPLICATION_DETAILS(5), //TODO Remove IFS-6216
    FINANCE(7), // TODO remove all finance types IFS-6217
    LABOUR(8), // TODO remove all finance types IFS-6217
    OVERHEADS(9), // TODO remove all finance types IFS-6217
    MATERIALS(10), // TODO remove all finance types IFS-6217
    CAPITAL_USAGE(11), // TODO remove all finance types IFS-6217
    SUBCONTRACTING(12), // TODO remove all finance types IFS-6217
    TRAVEL(13), // TODO remove all finance types IFS-6217
    OTHER_COSTS(14), // TODO remove all finance types IFS-6217
    YOUR_FINANCE(15), // TODO remove all finance types IFS-6217
    FINANCIAL_SUMMARY(16), // TODO remove all finance types IFS-6217
    ORGANISATION_SIZE(19), // TODO remove all finance types IFS-6217
    OTHER_FUNDING(17), // TODO remove all finance types IFS-6217
    FINANCE_UPLOAD(20), // TODO remove all finance types IFS-6217
    ASSESSOR_RESEARCH_CATEGORY(21),
    ASSESSOR_APPLICATION_IN_SCOPE(22),
    ASSESSOR_SCORE(23),
    ORGANISATION_TURNOVER(24),
    STAFF_COUNT(25),
    FINANCIAL_YEAR_END(26),
    FINANCIAL_OVERVIEW_ROW(27),
    FINANCIAL_STAFF_COUNT(28),
    TEMPLATE_DOCUMENT(29);

    private static List<FormInputType> COST_CATEGORIES =
            asList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING, TRAVEL, OTHER_COSTS);

    private static List<FormInputType> FINANCE_TYPES = combineLists(COST_CATEGORIES, FINANCE, YOUR_FINANCE, OTHER_FUNDING, ORGANISATION_SIZE, STAFF_COUNT, ORGANISATION_TURNOVER, FINANCIAL_YEAR_END, FINANCIAL_OVERVIEW_ROW, FINANCIAL_STAFF_COUNT);

    private static List<FormInputType> ACADEMIC_FINANCE_TYPES = asList(YOUR_FINANCE, FINANCE_UPLOAD);

    private static List<FormInputType> FINANCIAL_SUMMARY_TYPES = singletonList(FINANCIAL_SUMMARY);

    private static List<FormInputType> PRINT_TYPES = asList(APPLICATION_DETAILS, TEXTAREA);

    private long id;

    FormInputType(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getNameLower() {
        return this.name().toLowerCase();
    }

    public boolean isDisplayableQuestionType() {
        return !combineLists(FINANCE_TYPES, ACADEMIC_FINANCE_TYPES, FINANCIAL_SUMMARY_TYPES).contains(this);
    }

    public boolean isDisplayableFinanceType() {
        return FINANCE_TYPES.contains(this);
    }

    public boolean isDisplayableAcademicFinanceType() {
        return ACADEMIC_FINANCE_TYPES.contains(this);
    }

    public boolean isDisplayableFinancialSummaryType() {
        return FINANCIAL_SUMMARY_TYPES.contains(this);
    }

    public boolean isDisplayableFinanceType(String financeView) {

        switch (financeView) {
            case "finance": return isDisplayableFinanceType();
            case "academic-finance": return isDisplayableAcademicFinanceType();
            default: throw new IllegalArgumentException("Don't know how to filter financial fields based on view " + financeView);
        }
    }

    public boolean isDisplayablePrintType() {
        return PRINT_TYPES.contains(this);
    }

    public static FormInputType findByName(String name) {
        return stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No FormInputType found for name: " + name));
    }
}
