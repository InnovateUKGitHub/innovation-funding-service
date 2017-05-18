package org.innovateuk.ifs.competitionsetup.utils;

/**
 * Keeps of track of sectors that should not be applied to non-ifs competitions.
 */
public enum FilteredNonIfsSectors {
    OPEN_SECTOR(0L);

    private Long id;
    FilteredNonIfsSectors(Long id) {this.id = id;}

    public Long getId() {
        return id;
    }
}