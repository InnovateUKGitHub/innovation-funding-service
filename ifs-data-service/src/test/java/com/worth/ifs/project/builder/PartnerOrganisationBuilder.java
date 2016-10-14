package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class PartnerOrganisationBuilder extends BaseBuilder<PartnerOrganisation, PartnerOrganisationBuilder> {

    private PartnerOrganisationBuilder(List<BiConsumer<Integer, PartnerOrganisation>> multiActions) {
        super(multiActions);
    }

    public static PartnerOrganisationBuilder newPartnerOrganisation() {
        return new PartnerOrganisationBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected PartnerOrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PartnerOrganisation>> actions) {
        return new PartnerOrganisationBuilder(actions);
    }

    @Override
    protected PartnerOrganisation createInitial() {
        return newInstance(PartnerOrganisation.class);
    }

    public PartnerOrganisationBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public PartnerOrganisationBuilder withProject(Project... project) {
        return withArray((p, partnerOrg) -> setField("project", p, partnerOrg), project);
    }

    public PartnerOrganisationBuilder withOrganisation(Organisation... organisation) {
        return withArray((org, partnerOrg) -> setField("organisation", org, partnerOrg), organisation);
    }

    public PartnerOrganisationBuilder withLeadOrganisation(Boolean... lead) {
        return withArray((flag, partnerOrg) -> setField("leadOrganisation", flag, partnerOrg), lead);
    }

    @Override
    protected void postProcess(int index, PartnerOrganisation partnerOrganisation) {

        // add Hibernate-style backlinks to the PartnerOrganisations

        Project project = partnerOrganisation.getProject();

        if (project != null && !project.getOrganisations().contains(partnerOrganisation.getOrganisation())) {
            project.addPartnerOrganisation(partnerOrganisation);
        }
    }
}
