package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;

/**
 * Base model modelpopulator that handles extraction of specific resources from a map of
 * {@link AffiliationResource} lists grouped by {@link AffiliationType}.
 */
public abstract class AssessorProfileDeclarationBasePopulator {

    private final Predicate<List<AffiliationResource>> filterByExisting =
            affiliationsByType -> affiliationsByType.size() >= 1 && affiliationsByType.get(0).getExists();

    protected Map<AffiliationType, List<AffiliationResource>> getAffiliationsMap(List<AffiliationResource> affiliations) {
        if (affiliations == null) {
            return emptyMap();
        } else {
            return affiliations.stream().collect(groupingBy(AffiliationResource::getAffiliationType));
        }
    }

    // Principal Employer

    protected Optional<AffiliationResource> getPrincipalEmployer(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(EMPLOYER, affiliations);
    }

    // Professional Affiliations

    protected String getProfessionalAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(PROFESSIONAL, affiliations)
                .map(AffiliationResource::getDescription)
                .orElse(null);
    }

    // Appointments

    protected Boolean hasAppointments(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(PERSONAL, affiliations);
    }

    protected List<AffiliationResource> getAppointments(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationsByType(PERSONAL, affiliations)
                .filter(filterByExisting)
                .orElse(emptyList());
    }

    // Financial Interests

    protected Boolean hasFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(PERSONAL_FINANCIAL, affiliations);
    }

    protected String getFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(PERSONAL_FINANCIAL, affiliations)
                .map(AffiliationResource::getDescription)
                .orElse(null);
    }

    // Family Affiliations

    protected Boolean hasFamilyAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(FAMILY, affiliations);
    }

    protected List<AffiliationResource> getFamilyAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationsByType(FAMILY, affiliations)
                .filter(filterByExisting)
                .orElse(emptyList());
    }

    // Family Financial Interests

    protected Boolean hasFamilyFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(FAMILY_FINANCIAL, affiliations);
    }

    protected String getFamilyFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(FAMILY_FINANCIAL, affiliations)
                .map(AffiliationResource::getDescription)
                .orElse(null);
    }

    protected Optional<AffiliationResource> getAffiliationByType(AffiliationType affiliationType,
                                                                 Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType))
                .flatMap(affiliationsByType -> affiliationsByType.stream().findFirst());
    }

    protected Optional<List<AffiliationResource>> getAffiliationsByType(AffiliationType affiliationType,
                                                                        Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType));
    }

    protected Boolean hasAffiliationsByType(AffiliationType affiliationType,
                                            Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType))
                .map(affiliationsByType -> affiliationsByType.size() >= 1 && affiliationsByType.get(0).getExists())
                .orElse(null);
    }
}
