package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.identity.Identifiable;

public enum Satisfaction implements Identifiable<Satisfaction> {

    VERY_SATISFIED(5),
    SATISFIED(4),
    NEITHER(3),
    DISSATISFIED(2),
    VERY_DISSATISFIED(1);

    private final long id;

    Satisfaction(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

}
