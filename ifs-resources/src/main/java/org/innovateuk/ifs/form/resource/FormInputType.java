package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.util.enums.Identifiable;

import static java.util.Arrays.stream;

/**
 * FormInputType is used to identify what response a FormInput needs.
 * This is also used to choose a template in the web-service. Depending on the FormInputType we
 * can also implement extra behaviour like form / input validation.
 */
public enum FormInputType implements Identifiable {

    TEXTINPUT(1),
    TEXTAREA(2),
    DATE(3),
    FILEUPLOAD(4),
    APPLICATION_DETAILS(5),
    EMPTY(6),
    FINANCE(7),
    LABOUR(8),
    OVERHEADS(9),
    MATERIALS(10),
    CAPITAL_USAGE(11),
    SUBCONTRACTING(12),
    TRAVEL(13),
    OTHER_COSTS(14),
    YOUR_FINANCE(15),
    FINANCIAL_SUMMARY(16),
    OTHER_FUNDING(17),
    PERCENTAGE(18),
    ORGANISATION_SIZE(19),
    FINANCE_UPLOAD(20),
    ASSESSOR_RESEARCH_CATEGORY(21),
    ASSESSOR_APPLICATION_IN_SCOPE(22),
    ASSESSOR_SCORE(23),
    STAFF_TURNOVER(24),
    STAFF_COUNT(25),
    FINANCIAL_YEAR_END(26),
    FINANCIAL_OVERVIEW_ROW(27),
    FINANCIAL_STAFF_COUNT(28);

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

    public static FormInputType findByName(String name) {
        return stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No FormInputType found for name: " + name));
    }
}
