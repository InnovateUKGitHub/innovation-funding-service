package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.identity.IdentifiableEnum;

public enum Satisfaction implements IdentifiableEnum<Satisfaction> {

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
