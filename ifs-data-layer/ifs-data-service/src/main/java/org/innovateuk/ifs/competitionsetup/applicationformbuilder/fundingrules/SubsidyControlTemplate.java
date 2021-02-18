package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.featureswitch.SubsidyControlNorthernIrelandMode;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.MultipleChoiceOptionBuilder.aMultipleChoiceOption;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;

@Component
public class SubsidyControlTemplate implements FundingRulesTemplate {

    @Value("${ifs.subsidy.control.northern.ireland.mode}")
    private SubsidyControlNorthernIrelandMode subsidyControlNorthernIrelandMode;

    @Autowired
    private Environment environment;

    @Override
    public FundingRules type() {
        return FundingRules.SUBSIDY_CONTROL;
    }

    @Override
    public List<SectionBuilder> sections(Competition competition, List<SectionBuilder> sectionBuilders) {
        if (tacticalNorthernIrelandSubsidyControlModeEnabled() || generatingWebtestDataForComp(competition)) {
            insertNorthernIrelandDeclaration(sectionBuilders);
        }
        return sectionBuilders;
    }

    private boolean generatingWebtestDataForComp(Competition competition) {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> "automated".equals(profile))
                && competition.getName().contains("Subsidy control tactical");
    }

    private boolean tacticalNorthernIrelandSubsidyControlModeEnabled() {
        return SubsidyControlNorthernIrelandMode.TACTICAL == subsidyControlNorthernIrelandMode;
    }


    private static void insertNorthernIrelandDeclaration(List<SectionBuilder> sectionBuilders) {
        sectionBuilders.stream()
                .filter(section -> "Project details".equals(section.getName()))
                .findAny()
                .ifPresent(section -> section.getQuestions().add(0, northernIrelandDeclaration()));
    }

    private static QuestionBuilder northernIrelandDeclaration() {
        return aQuestion()
                .withShortName("Subsidy basis")
                .withName("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")
                .withDescription("For example, if the project or related activities are undertaken in Northern Ireland that would be a 'Yes' etc")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.NORTHERN_IRELAND_DECLARATION)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("Yes"),
                                        aMultipleChoiceOption()
                                                .withText("No")
                                ))
                ));
    }
}
