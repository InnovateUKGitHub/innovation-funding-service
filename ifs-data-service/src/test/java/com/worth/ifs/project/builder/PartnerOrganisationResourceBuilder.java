package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

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

    public PartnerOrganisationResourceBuilder withLeadOrganisation(Boolean... lead) {
        return withArray((flag, partnerOrg) -> setField("leadOrganisation", flag, partnerOrg), lead);
    }
}
