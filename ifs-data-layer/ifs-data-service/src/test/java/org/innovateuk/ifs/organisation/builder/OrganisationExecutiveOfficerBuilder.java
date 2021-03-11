package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.domain.ExecutiveOfficer;
import org.innovateuk.ifs.organisation.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OrganisationExecutiveOfficerBuilder extends BaseBuilder<ExecutiveOfficer, OrganisationExecutiveOfficerBuilder>  {
    private OrganisationExecutiveOfficerBuilder(List<BiConsumer<Integer, ExecutiveOfficer>> multiActions) {
        super(multiActions);
    }

    public static OrganisationExecutiveOfficerBuilder newOrganisationExecutiveOfficer() {
        return new OrganisationExecutiveOfficerBuilder(emptyList()).
                with(uniqueIds());
    }

    @Override
    protected OrganisationExecutiveOfficerBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExecutiveOfficer>> actions) {
        return new OrganisationExecutiveOfficerBuilder(actions);
    }

    @Override
    protected ExecutiveOfficer createInitial() {
        return new ExecutiveOfficer();
    }

    public OrganisationExecutiveOfficerBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public OrganisationExecutiveOfficerBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public OrganisationExecutiveOfficerBuilder withOrganisation(Organisation... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }
}
