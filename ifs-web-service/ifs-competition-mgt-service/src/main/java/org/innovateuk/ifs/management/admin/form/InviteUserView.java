package org.innovateuk.ifs.management.admin.form;

import java.util.stream.Stream;

public enum InviteUserView {

    INTERNAL_USERS("internal"),
    EXTERNAL_USERS("external");

    private final String name;

    InviteUserView(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InviteUserView getByName(String name) {
        return Stream.of(values())
                .filter(inviteUserView -> inviteUserView.getName().equals(name))
                .findFirst()
                .get();
    }
}
