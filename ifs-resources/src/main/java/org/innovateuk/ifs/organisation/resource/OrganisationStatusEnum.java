package org.innovateuk.ifs.organisation.resource;

import java.util.Optional;
import java.util.stream.Stream;

public enum OrganisationStatusEnum {
    DISSOLVED("dissolved"),
    CLOSED("closed"),
    CLOSED_ON("closed-on"),
    LIQUIDATION("liquidation"),
    RECEIVER_ACTION("receivership"),
    CONVERTED_CLOSED("converted-closed"),
    VOLUNTARY_ARRANGEMENT("voluntary-arrangement"),
    INSOLVENCY_PROCEEDINGS("insolvency-proceedings"),
    IN_ADMINISTRATION("administration");

    private String status;

    OrganisationStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static boolean isOrganisationInvalidSatus(String organisationStatus) {
        Optional<OrganisationStatusEnum> orgStatusValue =  Stream.of(OrganisationStatusEnum.values())
                .filter(orgStatus -> orgStatus.getStatus().equalsIgnoreCase(organisationStatus))
                .findAny();
        return orgStatusValue.isPresent();
    }
}