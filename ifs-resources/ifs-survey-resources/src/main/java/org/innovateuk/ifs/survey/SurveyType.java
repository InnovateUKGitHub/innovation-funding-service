package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.identity.Identifiable;

public enum SurveyType implements Identifiable<SurveyType> {

    APPLICATION_SUBMISSION(1);

    private final long id;

    SurveyType(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }
}
