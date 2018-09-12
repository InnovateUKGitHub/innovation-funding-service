package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.testdata.builders.data.ApplicationFinanceData;
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
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Generates Application Finance data for an Organisation on an Application
 */
public class ApplicationFinanceDataBuilder extends BaseDataBuilder<ApplicationFinanceData, ApplicationFinanceDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFinanceDataBuilder.class);

    public ApplicationFinanceDataBuilder withExistingFinances(
            ApplicationResource application,
            CompetitionResource competition,
            UserResource user,
            OrganisationResource organisation) {

        return with(data -> {
           data.setApplication(application);
           data.setCompetition(competition);
           data.setUser(user);
           data.setOrganisation(organisation);
        });
    }

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
                    financeRowCostsService.addCost(new ApplicationFinanceResourceId(data.getApplication().getId(), data.getOrganisation().getId())).
                            getSuccess();

            IndustrialCostDataBuilder baseFinanceBuilder = newIndustrialCostData(serviceLocator).
                    withApplicationFinance(applicationFinance).
                    withCompetition(data.getCompetition());

            costBuilderFn.apply(baseFinanceBuilder).build();
        });
    }

    public ApplicationFinanceDataBuilder withAcademicCosts(UnaryOperator<AcademicCostDataBuilder> costBuilderFn) {

        return doAsUser(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeRowCostsService.addCost(new ApplicationFinanceResourceId(data.getApplication().getId(), data.getOrganisation().getId())).
                            getSuccess();

            AcademicCostDataBuilder baseFinanceBuilder = newAcademicCostData(serviceLocator).
                    withApplicationFinance(applicationFinance).
                    withCompetition(data.getCompetition());

            costBuilderFn.apply(baseFinanceBuilder).build();
        });
    }

    public ApplicationFinanceDataBuilder markAsComplete(boolean markAsComplete, boolean updateApplicationCompleteStatus) {
        return doAsUser(data -> {
            if (markAsComplete) {
                List<QuestionResource> questions = questionService
                        .findByCompetition(data.getCompetition().getId())
                        .getSuccess();

                List<QuestionResource> questionsToComplete = simpleFilter(questions, QuestionResource::hasMultipleStatuses);

                forEachWithIndex(questionsToComplete, (i, q) -> {
                    QuestionApplicationCompositeId questionKey = new QuestionApplicationCompositeId(q.getId(), data.getApplication().getId());
                    Long processRoleId = processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(data.getUser().getId(), applicantProcessRoles(),
                            data.getApplication().getId()).getId();

                    boolean lastElement = i == questions.size() - 1;

                    if (lastElement && updateApplicationCompleteStatus) {
                        questionStatusService.markAsComplete(questionKey, processRoleId).getSuccess();
                    } else {
                        testQuestionService.markAsCompleteWithoutApplicationCompletionStatusUpdate(questionKey, processRoleId).getSuccess();
                    }
                });
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
