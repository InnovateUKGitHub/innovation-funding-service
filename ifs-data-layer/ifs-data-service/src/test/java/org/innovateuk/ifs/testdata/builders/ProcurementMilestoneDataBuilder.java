package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.testdata.builders.data.ProcurementMilestoneData;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.LongStream;

import static java.util.Collections.emptyList;

public class ProcurementMilestoneDataBuilder extends BaseDataBuilder<ProcurementMilestoneData, ProcurementMilestoneDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ProcurementMilestoneDataBuilder.class);

    public ProcurementMilestoneDataBuilder withExistingFinances(
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

    public ProcurementMilestoneDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public ProcurementMilestoneDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public ProcurementMilestoneDataBuilder withOrganisation(String organisationName) {
        return with(data -> data.setOrganisation(retrieveOrganisationResourceByName(organisationName)));
    }

    public ProcurementMilestoneDataBuilder withOrganisation(OrganisationResource organisation) {
        return with(data -> data.setOrganisation(organisation));
    }

    public ProcurementMilestoneDataBuilder withUser(String email) {
        return withUser(retrieveUserByEmail(email));
    }

    public ProcurementMilestoneDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }

    public ProcurementMilestoneDataBuilder withMilestones() {

        return doAsUser(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeService.financeDetails(data.getApplication().getId(), data.getOrganisation().getId()).
                            getSuccess();

            final BigInteger[] runningTotal = {BigInteger.ZERO};
            BigInteger fundingSought = applicationFinance.getTotalFundingSought().toBigInteger();
            long durationInMonths = data.getApplication().getDurationInMonths();
            LongStream.range(0, durationInMonths).forEach(zeroBasedIndex -> {
                ApplicationProcurementMilestoneResource milestone = new ApplicationProcurementMilestoneResource();
                milestone.setApplicationId(applicationFinance.getApplication());
                milestone.setOrganisationId(applicationFinance.getOrganisation());

                milestone.setMonth(Math.toIntExact(zeroBasedIndex + 1));
                milestone.setDescription("Milestone for month " + (zeroBasedIndex + 1));
                milestone.setDeliverable("Oh! You better watch out\n" +
                        "You better not cry\n" +
                        "You better not pout I'm telling you why\n" +
                        "Santa Claus is coming to town");
                milestone.setSuccessCriteria("He sees you when you're sleeping\n" +
                        "He knows when you're awake\n" +
                        "He knows if you've been bad or good\n" +
                        "So be good for goodness sake!");
                milestone.setTaskOrActivity("He's making a list\n" +
                        "Checking it twice\n" +
                        "Gonna find out who's naughty or nice\n" +
                        "Santa Claus is coming to town");
                if (durationInMonths - 1 == zeroBasedIndex) {
                    milestone.setPayment(fundingSought.subtract(runningTotal[0]));
                } else {
                    milestone.setPayment(fundingSought.subtract(runningTotal[0]).divide(BigInteger.valueOf(durationInMonths)));
                }
                runningTotal[0] = runningTotal[0].add(milestone.getPayment());

                ApplicationProcurementMilestoneResource resource = applicationProcurementMilestoneService.create(milestone).getSuccess();

                applicationProcurementMilestoneService.delete(ApplicationProcurementMilestoneId.of(resource.getId())).getSuccess();
            });
        });
    }


    public ProcurementMilestoneDataBuilder markAsComplete(boolean markAsComplete, boolean updateApplicationCompleteStatus) {
        return doAsUser(data -> {
            //TODO fill out once we have a milestone question
//            if (markAsComplete) {
//                List<QuestionResource> questions = questionService
//                        .findByCompetition(data.getCompetition().getId())
//                        .getSuccess();
//
//                List<QuestionResource> questionsToComplete = simpleFilter(questions, QuestionResource::hasMultipleStatuses);
//
//                forEachWithIndex(questionsToComplete, (i, q) -> {
//                    QuestionApplicationCompositeId questionKey = new QuestionApplicationCompositeId(q.getId(), data.getApplication().getId());
//                    Long processRoleId = processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(data.getUser().getId(), applicantProcessRoles(),
//                            data.getApplication().getId()).getId();
//
//                    boolean lastElement = i == questions.size() - 1;
//
//                    if (lastElement && updateApplicationCompleteStatus) {
//                        questionStatusService.markAsComplete(questionKey, processRoleId).getSuccess();
//                    } else {
//                        testQuestionService.markAsCompleteWithoutApplicationCompletionStatusUpdate(questionKey, processRoleId).getSuccess();
//                    }
//                });
//            }
        });
    }

    private ProcurementMilestoneDataBuilder doAsUser(Consumer<ProcurementMilestoneData> action) {
        return with(data -> doAs(data.getUser(), () -> action.accept(data)));
    }

    public static ProcurementMilestoneDataBuilder newProcurementMilestoneDataBuilder(ServiceLocator serviceLocator) {
        return new ProcurementMilestoneDataBuilder(emptyList(), serviceLocator);
    }

    private ProcurementMilestoneDataBuilder(List<BiConsumer<Integer, ProcurementMilestoneData>> multiActions,
                                            ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected ProcurementMilestoneDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcurementMilestoneData>> actions) {
        return new ProcurementMilestoneDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ProcurementMilestoneData createInitial() {
        return new ProcurementMilestoneData();
    }

    @Override
    protected void postProcess(int index, ProcurementMilestoneData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Procurement Milestones for Application '{}', Organisation '{}'", instance.getApplication().getName(), instance.getOrganisation().getName());
    }
}
