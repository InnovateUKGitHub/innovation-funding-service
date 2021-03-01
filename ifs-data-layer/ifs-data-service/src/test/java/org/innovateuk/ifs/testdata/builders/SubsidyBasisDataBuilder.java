package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.testdata.builders.data.ProcurementMilestoneData;
import org.innovateuk.ifs.testdata.builders.data.SubsidyBasisData;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.LongStream;

import static java.util.Collections.emptyList;

public class SubsidyBasisDataBuilder extends BaseDataBuilder<SubsidyBasisData, SubsidyBasisDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(SubsidyBasisDataBuilder.class);

//    public SubsidyBasisDataBuilder withExistingFinances(
//            ApplicationResource application,
//            CompetitionResource competition,
//            UserResource user,
//            OrganisationResource organisation) {
//
//        return with(data -> {
//            data.setApplication(application);
//            data.setCompetition(competition);
//            data.setUser(user);
//            data.setOrganisation(organisation);
//        });
//    }

    public SubsidyBasisDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public SubsidyBasisDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public SubsidyBasisDataBuilder withOrganisation(String organisationName) {
        return with(data -> data.setOrganisation(retrieveOrganisationResourceByName(organisationName)));
    }

    public SubsidyBasisDataBuilder withOrganisation(OrganisationResource organisation) {
        return with(data -> data.setOrganisation(organisation));
    }

    public SubsidyBasisDataBuilder withUser(String email) {
        return withUser(retrieveUserByEmail(email));
    }

    public SubsidyBasisDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }

    public SubsidyBasisDataBuilder withSubsidyBasis() {
        return null; // TODO
    }
    
    private SubsidyBasisDataBuilder doAsUser(Consumer<SubsidyBasisData> action) {
        return with(data -> doAs(data.getUser(), () -> action.accept(data)));
    }

    public static SubsidyBasisDataBuilder newSubsidyBasisDataBuilder(ServiceLocator serviceLocator) {
        return new SubsidyBasisDataBuilder(emptyList(), serviceLocator);
    }

    private SubsidyBasisDataBuilder(List<BiConsumer<Integer, SubsidyBasisData>> multiActions,
                                    ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected SubsidyBasisDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SubsidyBasisData>> actions) {
        return new SubsidyBasisDataBuilder(actions, serviceLocator);
    }

    @Override
    protected SubsidyBasisData createInitial() {
        return new SubsidyBasisData();
    }

    @Override
    protected void postProcess(int index, SubsidyBasisData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Subsidy Basis for Application '{}', Organisation '{}'", instance.getApplication().getName(), instance.getOrganisation().getName());
    }
}
