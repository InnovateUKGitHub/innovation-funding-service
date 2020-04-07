package org.innovateuk.ifs.address.resource;

/**
 * This enum represents data in address_type entity.
 */
public enum OrganisationAddressType {
    @Deprecated
    REGISTERED(1L),                         // No longer capturing registered address as part of organisation creation
    @Deprecated
    OPERATING(2L),                          // No longer capturing operating address as part of organisation creation
    INTERNATIONAL(5L);                      // Used for international organisations as part of organisation creation

    private final long ordinal;

    OrganisationAddressType(long ordinal) {
        this.ordinal = ordinal;
    }

    public long getOrdinal() {
        return ordinal;
    }
}
