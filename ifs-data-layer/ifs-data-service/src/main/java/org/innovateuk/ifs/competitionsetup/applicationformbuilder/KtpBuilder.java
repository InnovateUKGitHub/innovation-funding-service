package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class KtpBuilder implements FundingTypeTemplate {

    @Override
    public FundingType type() {
        return FundingType.KTP;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                ktpAssessmentQuestions()
                        .withQuestions(ktpDefaultQuestions())
        );
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        return competition;
    }

    public static List<QuestionBuilder> ktpDefaultQuestions() {
        return newArrayList(
                impact(),
                innovation(),
                challenge(),
                cohesiveness()
        );
    }
}
