package org.innovateuk.ifs.management.admin.form;

import java.util.stream.Stream;

public enum InviteUserView {

    INTERNAL_USER("internal"),
    KTA_USER("kta");

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
