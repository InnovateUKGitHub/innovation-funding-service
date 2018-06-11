package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.identity.Identifiable;

public enum SurveyTargetType implements Identifiable<SurveyTargetType> {

    COMPETITION(1);

    private final long id;

    SurveyTargetType(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

}
