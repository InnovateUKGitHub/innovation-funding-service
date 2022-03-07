package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.MultipleChoiceOptionBuilder.aMultipleChoiceOption;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;

@Component
public class HorizonEuropeGuaranteeTemplate implements CompetitionTemplate {


    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HORIZON_EUROPE_GUARANTEE;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Horizon Europe Guarantee"));
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(84);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationDetails(),
                                applicationTeam(),
                                aQuestion()
                                        .withShortName("Horizon Europe Guarantee grant agreement")
                                        .withName("Horizon Europe Guarantee grant agreement")
                                        .withAssignEnabled(false)
                                        .withMultipleStatuses(false)
                                        .withMarkAsCompletedEnabled(true)
                                        .withType(QuestionType.LEAD_ONLY)
                                        .withQuestionSetupType(QuestionSetupType.GRANT_AGREEMENT),
                                equalityDiversityAndInclusion()
                        )),
                applicationQuestions()
                        .withQuestions(horizonEuropeGuaranteeDefaultQuestions()),
                finances(),
                termsAndConditions()
        );
    }

    public static List<QuestionBuilder> horizonEuropeGuaranteeDefaultQuestions() {
        return newArrayList(
                organisationBased(),
                participatingOrganisationProjectRegion(),
                eicCall()
//                applicationReferenceNumber(),
//                ukFundingContribution(),
//                tasksAssigned(),
        );
    }

    private static QuestionBuilder organisationBased() {
        QuestionBuilder organisationBasedQuestion =
                genericQuestion()
                        .withShortName("Tell us where your organisation is based")
                        .withName("Tell us where your organisation is based")
                        .withAssignEnabled(true)
                        .withMarkAsCompletedEnabled(true)
                        .withMultipleStatuses(true)
                        .withFormInputs(asList(
                                aFormInput()
                                        .withType(FormInputType.MULTIPLE_CHOICE)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(true)
                                        .withMultipleChoiceOptions(newArrayList(
                                                aMultipleChoiceOption()
                                                        .withText("My organisation is based in the UK or a British Overseas Territory"),
                                                aMultipleChoiceOption()
                                                        .withText("My organisation is NOT based in the UK or a British Overseas Territory")
                                        )),
                                aFormInput()
                                        .withType(FormInputType.FILEUPLOAD)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(false),
                                aFormInput()
                                        .withType(FormInputType.TEMPLATE_DOCUMENT)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(false)
                        ));

        organisationBasedQuestion.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(ASSESSMENT))
                .forEach(fi -> fi.withActive(false));
        return organisationBasedQuestion;
    }

    private static QuestionBuilder participatingOrganisationProjectRegion() {
//        QuestionBuilder participatingOrganisationProjectRegion =
        return genericQuestion()
                        .withShortName("Participating Organisation project region")
                        .withName("Participating Organisation project region")
                        .withAssignEnabled(true)
                        .withMarkAsCompletedEnabled(true)
                        .withMultipleStatuses(true)
                        .withFormInputs(asList(
                                aFormInput()
                                        .withType(FormInputType.MULTIPLE_CHOICE)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(true)
                                        .withMultipleChoiceOptions(newArrayList(
                                                aMultipleChoiceOption()
                                                        .withText("East Midlands"),
                                                aMultipleChoiceOption()
                                                        .withText("East of England"),
                                                aMultipleChoiceOption()
                                                        .withText("London"),
                                                aMultipleChoiceOption()
                                                        .withText("North East & Cumbria"),
                                                aMultipleChoiceOption()
                                                        .withText("North West"),
                                                aMultipleChoiceOption()
                                                        .withText("Northern Ireland"),
                                                aMultipleChoiceOption()
                                                        .withText("Scotland"),
                                                aMultipleChoiceOption()
                                                        .withText("South East"),
                                                aMultipleChoiceOption()
                                                        .withText("South West"),
                                                aMultipleChoiceOption()
                                                        .withText("Wales"),
                                                aMultipleChoiceOption()
                                                        .withText("West Midlands"),
                                                aMultipleChoiceOption()
                                                        .withText("Yorkshire & the Humber"),
                                                aMultipleChoiceOption()
                                                        .withText("Overseas British Territory")
                                        )),
                                aFormInput()
                                        .withType(FormInputType.FILEUPLOAD)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(false),
                                aFormInput()
                                        .withType(FormInputType.TEMPLATE_DOCUMENT)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(false),
                                aFormInput()
                                        .withType(ASSESSOR_SCORE)
                                        .withScope(ASSESSMENT)
                                        .withActive(false),
                                aFormInput()
                                        .withType(ASSESSOR_SCORE)
                                        .withScope(ASSESSMENT)
                                        .withActive(false)
                        ));

//        participatingOrganisationProjectRegion.getFormInputs().stream()
//                .filter(fi -> fi.getScope().equals(ASSESSMENT))
//                .forEach(fi -> fi.withActive(false));
//        return participatingOrganisationProjectRegion;
    }

    private static QuestionBuilder eicCall() {
        QuestionBuilder eicCall =
                genericQuestion()
                        .withShortName("What EIC call have you been successfully evaluated for?")
                        .withName("What EIC call have you been successfully evaluated for?")
                        .withAssignEnabled(true)
                        .withMarkAsCompletedEnabled(true)
                        .withMultipleStatuses(true)
                        .withFormInputs(asList(
                                aFormInput()
                                        .withType(FormInputType.MULTIPLE_CHOICE)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(true)
                                        .withMultipleChoiceOptions(newArrayList(
                                                aMultipleChoiceOption()
                                                        .withText("EIC Transition"),
                                                aMultipleChoiceOption()
                                                        .withText("EIC Pathfinder"),
                                                aMultipleChoiceOption()
                                                        .withText("EIC Accelerator")
                                        )),
                                aFormInput()
                                        .withType(FormInputType.FILEUPLOAD)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(false),
                                aFormInput()
                                        .withType(FormInputType.TEMPLATE_DOCUMENT)
                                        .withScope(FormInputScope.APPLICATION)
                                        .withActive(false)
                        ));

        eicCall.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(ASSESSMENT))
                .forEach(fi -> fi.withActive(false));
        return eicCall;
    }

}