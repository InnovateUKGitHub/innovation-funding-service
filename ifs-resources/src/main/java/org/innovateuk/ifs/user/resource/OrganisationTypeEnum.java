package org.innovateuk.ifs.user.resource;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public enum OrganisationTypeEnum {
    BUSINESS(1, false, true, true, null),
    RESEARCH(2, false, false, true, null),
    RTO(3, false, false, true, null),
    PUBLICSECTOR_OR_CHARITY(4, false, false, false, null);

    private static final Map<Long, OrganisationTypeEnum> lookup = new TreeMap<>();

    static {
        for (OrganisationTypeEnum d : OrganisationTypeEnum.values()) {
            lookup.put(d.getId(), d);
        }
    }

    private final Long organisationTypeId;
    private final boolean restrictOrganisationName; // if true, the user won't be able to enter his organisation name, and should use the search to find his organisation
    private final boolean useOrganisationSearch; // if true there is a search available to search for organisations of this type.
    private final boolean showInCompetitionSetup;
    private final OrganisationTypeEnum parentOrganisationType;

    OrganisationTypeEnum(int organisationTypeId, boolean restrictOrganisationName, boolean useOrganisationSearch, boolean showInCompetitionSetup, OrganisationTypeEnum parent){
        this.organisationTypeId = Long.valueOf(organisationTypeId);
        this.restrictOrganisationName = restrictOrganisationName;
        this.useOrganisationSearch = useOrganisationSearch;
        this.showInCompetitionSetup = showInCompetitionSetup;
        this.parentOrganisationType = parent;

        if(this.restrictOrganisationName && !this.useOrganisationSearch){
            throw new IllegalArgumentException("Can't restrict the organisation name, without enabling organisation search.");
        }
    }

    public static OrganisationTypeEnum getFromId(Long organisationTypeId){
        return lookup.get(organisationTypeId);
    }

    public static boolean isResearch(OrganisationTypeEnum organisationType){
        return organisationType.equals(RESEARCH) || (organisationType.getParentOrganisationType() != null && organisationType.getParentOrganisationType().equals(RESEARCH));
    }

    public static boolean isResearch(Long organisationTypeId){
        if(organisationTypeId!=null) {
            return isResearch(getFromId(organisationTypeId));
        } else {
            return false;
        }
    }

    public boolean hasChildren(){
        Optional<OrganisationTypeEnum> child = lookup.values().stream().filter(o -> o.getParentOrganisationType() != null && o.getParentOrganisationType().equals(this)).findAny();
        return child.isPresent();
    }

    public Long getId() {
        return organisationTypeId;
    }

    public OrganisationTypeEnum getParentOrganisationType() {
        return parentOrganisationType;
    }

    public boolean isShowInCompetitionSetup() {
        return showInCompetitionSetup;
    }

    public boolean isRestrictOrganisationName() {
        return restrictOrganisationName;
    }

    public boolean isUseOrganisationSearch() {
        return useOrganisationSearch;
    }
}
