package org.innovateuk.ifs.projectteam.viewmodel;

public abstract class AbstractProjectTeamRowViewModel {

    private final long id;
    private final String email;
    private final String name;
    private final boolean removeable;

    public AbstractProjectTeamRowViewModel(long id, String email, String name, boolean removeable) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.removeable = removeable;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isRemoveable() {
        return removeable;
    }

    public abstract boolean isInvite();
}
