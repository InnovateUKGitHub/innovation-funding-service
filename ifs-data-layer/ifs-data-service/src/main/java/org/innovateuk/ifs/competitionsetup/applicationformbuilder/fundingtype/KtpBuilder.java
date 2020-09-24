package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
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
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {

      competitionTypeSections.addAll(newArrayList(
                ktpAssessmentQuestions()
                        .withQuestions(ktpDefaultQuestions()))
      );

      return competitionTypeSections;
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
