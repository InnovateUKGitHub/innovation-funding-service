package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.user.resource.Role;

import java.util.stream.Stream;

public enum Satisfaction implements IdentifiableEnum {

    VERY_SATISFIED(5, "Very satisfied"),
    SATISFIED(4, "Satisfied"),
    NEITHER(3, "Neither satisfied or dissatisfied"),
    DISSATISFIED(2, "Dissatisfied"),
    VERY_DISSATISFIED(1, "Very dissatisfied");

    private final long id;
    private final String displayName;

    Satisfaction(final long id, final String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Satisfaction getById (long id) {
        return Stream.of(values()).filter(satisfaction -> satisfaction.getId() == id).findFirst().get();
    }
}
