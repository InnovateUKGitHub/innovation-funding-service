package org.innovateuk.ifs.address.resource;

/**
 * This enum represents data in address_type entity.
 */
public enum OrganisationAddressType {

    REGISTERED(1L),                        // Captures the Companies House registered address as part of organisation creation
    @Deprecated
    OPERATING(2L),                         // No longer capturing operating address as part of organisation creation
    INTERNATIONAL(5L),                     // Used for international organisations as part of organisation creation
    KNOWLEDGE_BASE(6L);                    // Used for knowledge base organisations as part of organisation creation

    private final long id;

    OrganisationAddressType(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
