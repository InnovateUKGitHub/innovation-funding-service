package org.innovateuk.ifs.testdata.data;

import org.innovateuk.ifs.testdata.builders.PreRegistrationSectionLineBuilder;
import org.innovateuk.ifs.testdata.builders.data.PreRegistrationSectionLine;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.testdata.builders.PreRegistrationSectionLineBuilder.aPreRegistrationSectionLine;

public class CompetitionPreRegistrationWebTestData {

    public static List<PreRegistrationSectionLine> buildCompetitionPreRegistrationLines() {
        return getCompetitionPreRegistrationLineBuilders().stream()
                .map(PreRegistrationSectionLineBuilder::build)
                .collect(Collectors.toList());
    }

    private static List<PreRegistrationSectionLineBuilder> getCompetitionPreRegistrationLineBuilders() {
        return asList(
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Terms and conditions"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Your project finances")
                        .withSubSectionName("Your project location"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Application questions")
                        .withQuestionName("Participating Organisation project region"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Application questions")
                        .withQuestionName("Application reference number"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Application questions")
                        .withQuestionName("Have the tasks assigned to your institution changed significantly since the original application?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Application questions")
                        .withQuestionName("Will you, as a UK institution, be employing PhD students as part of this project?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Competition For Pre Registration")
                        .withSectionName("Application questions")
                        .withQuestionName("How much budget is allocated for PhD students employed at your institution on this project?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Terms and conditions"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Your project finances")
                        .withSubSectionName("Your project location"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("Tell us where your organisation is based"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("Participating Organisation project region"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("What EIC call have you been successfully evaluated for?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("Application reference number"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("If this amount has changed please tell us how?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("Have the tasks assigned to your institution changed significantly since the original application?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("If so, how many PhD students will be employed at your institution on this project?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("Will you, as a UK institution, be employing PhD students as part of this project?"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Application questions")
                        .withQuestionName("How much budget is allocated for PhD students employed at your institution on this project?")
                )
                .stream()
                .collect(Collectors.toList());
    }
}
