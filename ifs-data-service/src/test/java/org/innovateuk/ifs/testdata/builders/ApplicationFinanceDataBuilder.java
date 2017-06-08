package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.testdata.builders.data.ApplicationFinanceData;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.testdata.builders.AcademicCostDataBuilder.newAcademicCostData;
import static org.innovateuk.ifs.testdata.builders.IndustrialCostDataBuilder.newIndustrialCostData;

/**
 * Generates Application Finance data for an Organisation on an Application
 */
public class ApplicationFinanceDataBuilder extends BaseDataBuilder<ApplicationFinanceData, ApplicationFinanceDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFinanceDataBuilder.class);

    public ApplicationFinanceDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public ApplicationFinanceDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public ApplicationFinanceDataBuilder withOrganisation(String organisationName) {
        return with(data -> data.setOrganisation(retrieveOrganisationResourceByName(organisationName)));
    }

    public ApplicationFinanceDataBuilder withOrganisation(OrganisationResource organisation) {
        return with(data -> data.setOrganisation(organisation));
    }

    public ApplicationFinanceDataBuilder withUser(String email) {
        return withUser(retrieveUserByEmail(email));
    }

    public ApplicationFinanceDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }

    public ApplicationFinanceDataBuilder withIndustrialCosts(UnaryOperator<IndustrialCostDataBuilder> costBuilderFn) {

        return doAsUser(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeRowService.addCost(new ApplicationFinanceResourceId(data.getApplication().getId(), data.getOrganisation().getId())).
                            getSuccessObjectOrThrowException();

            IndustrialCostDataBuilder baseFinanceBuilder = newIndustrialCostData(serviceLocator).
                    withApplicationFinance(applicationFinance).
                    withCompetition(data.getCompetition());

            costBuilderFn.apply(baseFinanceBuilder).build();
        });
    }

    public ApplicationFinanceDataBuilder withAcademicCosts(UnaryOperator<AcademicCostDataBuilder> costBuilderFn) {

        return doAsUser(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeRowService.addCost(new ApplicationFinanceResourceId(data.getApplication().getId(), data.getOrganisation().getId())).
                            getSuccessObjectOrThrowException();

            AcademicCostDataBuilder baseFinanceBuilder = newAcademicCostData(serviceLocator).
                    withApplicationFinance(applicationFinance).
                    withCompetition(data.getCompetition());

            costBuilderFn.apply(baseFinanceBuilder).build();
        });
    }

    public ApplicationFinanceDataBuilder markAsComplete(boolean markAsComplete) {
        return doAsUser(data -> {
            if (markAsComplete) {
                List<QuestionResource> questions = questionService
                        .findByCompetition(data.getCompetition().getId())
                        .getSuccessObjectOrThrowException();

                questions
                        .stream()
                        .filter(QuestionResource::hasMultipleStatuses)
                        .forEach(q -> questionService.markAsComplete(
                                new QuestionApplicationCompositeId(q.getId(), data.getApplication().getId()),
                                processRoleRepository.findByUserIdAndApplicationId(
                                        data.getUser().getId(),
                                        data.getApplication().getId())
                                        .getId()).getSuccessObjectOrThrowException());
            }
        });
    }

    private ApplicationFinanceDataBuilder doAsUser(Consumer<ApplicationFinanceData> action) {
        return with(data -> doAs(data.getUser(), () -> action.accept(data)));
    }

    public static ApplicationFinanceDataBuilder newApplicationFinanceData(ServiceLocator serviceLocator) {
        return new ApplicationFinanceDataBuilder(emptyList(), serviceLocator);
    }

    private ApplicationFinanceDataBuilder(List<BiConsumer<Integer, ApplicationFinanceData>> multiActions,
                                          ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected ApplicationFinanceDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationFinanceData>> actions) {
        return new ApplicationFinanceDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ApplicationFinanceData createInitial() {
        return new ApplicationFinanceData();
    }

    @Override
    protected void postProcess(int index, ApplicationFinanceData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Finances for Application '{}', Organisation '{}'", instance.getApplication().getName(), instance.getOrganisation().getName());
    }
}
