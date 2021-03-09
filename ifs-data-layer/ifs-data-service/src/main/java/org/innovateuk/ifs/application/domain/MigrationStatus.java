package org.innovateuk.ifs.application.domain;

public enum MigrationStatus {

    CREATED(1), // initial state
    MIGRATED(2);

    final long id;

    MigrationStatus(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
