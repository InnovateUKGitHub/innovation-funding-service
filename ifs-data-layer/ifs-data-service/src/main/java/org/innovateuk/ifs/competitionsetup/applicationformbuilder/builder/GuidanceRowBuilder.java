package org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder;

import org.innovateuk.ifs.form.domain.GuidanceRow;

public final class GuidanceRowBuilder {
    private String subject;
    private String justification;

    private GuidanceRowBuilder() {
    }

    public static GuidanceRowBuilder aGuidanceRow() {
        return new GuidanceRowBuilder();
    }

    public GuidanceRowBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public GuidanceRowBuilder withJustification(String justification) {
        this.justification = justification;
        return this;
    }

    public GuidanceRow build() {
        GuidanceRow guidanceRow = new GuidanceRow();
        guidanceRow.setSubject(subject);
        guidanceRow.setJustification(justification);
        return guidanceRow;
    }
}
