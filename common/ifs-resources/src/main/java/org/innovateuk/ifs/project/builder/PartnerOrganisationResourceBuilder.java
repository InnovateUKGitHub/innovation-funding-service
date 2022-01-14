package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PartnerOrganisationResourceBuilder extends BaseBuilder<PartnerOrganisationResource, PartnerOrganisationResourceBuilder> {

    private PartnerOrganisationResourceBuilder(List<BiConsumer<Integer, PartnerOrganisationResource>> multiActions) {
        super(multiActions);
    }

    public static PartnerOrganisationResourceBuilder newPartnerOrganisationResource() {
        return new PartnerOrganisationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected PartnerOrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PartnerOrganisationResource>> actions) {
        return new PartnerOrganisationResourceBuilder(actions);
    }

    @Override
    protected PartnerOrganisationResource createInitial() {
        return newInstance(PartnerOrganisationResource.class);
    }

    public PartnerOrganisationResourceBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public PartnerOrganisationResourceBuilder withProject(Long... project) {
        return withArray((p, partnerOrg) -> setField("project", p, partnerOrg), project);
    }

    public PartnerOrganisationResourceBuilder withOrganisation(Long... organisation) {
        return withArray((org, partnerOrg) -> setField("organisation", org, partnerOrg), organisation);
    }

    public PartnerOrganisationResourceBuilder withOrganisationName(String... organisationNames) {
        return withArray((flag, partnerOrg) -> setField("organisationName", flag, partnerOrg), organisationNames);
    }

    public PartnerOrganisationResourceBuilder withPostcode(String... postcodes) {
        return withArray((postcode, partnerOrg) -> setField("postcode", postcode, partnerOrg), postcodes);
    }

    public PartnerOrganisationResourceBuilder withLeadOrganisation(Boolean... lead) {
        return withArray((flag, partnerOrg) -> setField("leadOrganisation", flag, partnerOrg), lead);
    }
}
