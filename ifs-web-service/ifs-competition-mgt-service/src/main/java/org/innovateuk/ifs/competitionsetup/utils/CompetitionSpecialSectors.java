package org.innovateuk.ifs.competitionsetup.utils;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.function.Predicate;

/**
 * Keeps of track of sectors that have a special status and define custom behaviours.
 */
public enum CompetitionSpecialSectors {
    OPEN_SECTOR(0L);

    private Long id;
    CompetitionSpecialSectors(Long id) {this.id = id;}

    public Long getId() {
        return id;
    }

    public static Predicate<InnovationSectorResource> sectorIsExcludedForNonIfs() {
        return sector -> sector.getId().equals(CompetitionSpecialSectors.OPEN_SECTOR.getId());
    }

    public static Predicate<Long> isOpenSector() {
        return sectorId -> sectorId.equals(CompetitionSpecialSectors.OPEN_SECTOR.getId());
    }
}