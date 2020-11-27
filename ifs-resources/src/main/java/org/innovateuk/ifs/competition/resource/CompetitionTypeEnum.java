package org.innovateuk.ifs.competition.resource;

import static java.util.Arrays.stream;

public enum CompetitionTypeEnum {

    PROGRAMME("Programme"),
    SECTOR("Sector"),
    GENERIC("Generic"),
    EXPRESSION_OF_INTEREST("Expression of interest"),
    ADVANCED_PROPULSION_CENTRE("Advanced Propulsion Centre"),
    AEROSPACE_TECHNOLOGY_INSTITUTE("Aerospace Technology Institute"),
    THE_PRINCES_TRUST("The Prince's Trust"),
    HORIZON_2020("Horizon 2020"),
    HEUKAR("HEUKAR");


    CompetitionTypeEnum(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }

    public static CompetitionTypeEnum fromText(String text) {
        return stream(values())
                .filter(type -> type.getText().equals(text))
                .findFirst()
                .orElse(null);
    }
}