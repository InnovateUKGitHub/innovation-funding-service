package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationOrganisationAddressBuilder extends BaseBuilder<ApplicationOrganisationAddress, ApplicationOrganisationAddressBuilder> {

    private ApplicationOrganisationAddressBuilder(List<BiConsumer<Integer, ApplicationOrganisationAddress>> multiActions) {
        super(multiActions);
    }

    public static ApplicationOrganisationAddressBuilder newApplicationOrganisationAddress() {
        return new ApplicationOrganisationAddressBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationOrganisationAddressBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationOrganisationAddress>> actions) {
        return new ApplicationOrganisationAddressBuilder(actions);
    }

    @Override
    protected ApplicationOrganisationAddress createInitial() {
        return newInstance(ApplicationOrganisationAddress.class);
    }

    public ApplicationOrganisationAddressBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }
}
