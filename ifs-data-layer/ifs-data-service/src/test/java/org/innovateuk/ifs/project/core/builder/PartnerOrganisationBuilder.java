package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public PartnerOrganisationBuilder withPostcode(String... postcodes) {
        return withArray((postcode, partnerOrg) -> setField("postcode", postcode, partnerOrg), postcodes);
    }

    public PartnerOrganisationBuilder withLeadOrganisation(Boolean... lead) {
        return withArray((flag, partnerOrg) -> setField("leadOrganisation", flag, partnerOrg), lead);
    }

    public PartnerOrganisationBuilder withPendingPartnerProgress(PendingPartnerProgress... pendingPartnerProgresss) {
        return withArray((flag, pendingPartnerProgress) -> setField("pendingPartnerProgress", flag, pendingPartnerProgress), pendingPartnerProgresss);
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
