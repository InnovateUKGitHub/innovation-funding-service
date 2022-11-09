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
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with EOI Decision")
                        .withSectionName("Project details")
                        .withQuestionName("Work programme"),
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
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with Evidence Enabled")
                        .withSectionName("Project details")
                        .withQuestionName("Work programme"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with Evidence Enabled")
                        .withSectionName("Terms and conditions"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with Evidence Enabled")
                        .withSectionName("Your project finances")
                        .withSubSectionName("Your project location"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with Evidence Enabled")
                        .withSectionName("Application questions")
                        .withQuestionName("Tell us where your organisation is based"),
                aPreRegistrationSectionLine()
                        .withCompetitionName("Horizon Europe Guarantee Pre Registration Competition with Evidence Enabled")
                        .withSectionName("Application questions")
                        .withQuestionName("Participating Organisation project region")
                )
                .stream()
                .collect(Collectors.toList());
    }
}
