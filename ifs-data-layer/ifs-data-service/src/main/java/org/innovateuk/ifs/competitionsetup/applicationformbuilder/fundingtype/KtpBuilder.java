package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

@Component
public class KtpBuilder implements FundingTypeTemplate {

    private static final Integer MAXIMUM_ASSESSOR_SCORE = 10;

    @Autowired
    private CommonBuilders commonBuilders;

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

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(ASSOCIATE_SALARY_COSTS, ASSOCIATE_DEVELOPMENT_COSTS, KTP_TRAVEL, CONSUMABLES, KNOWLEDGE_BASE, ESTATE_COSTS, ASSOCIATE_SUPPORT, OTHER_COSTS, ADDITIONAL_COMPANY_COSTS, FINANCE, PREVIOUS_FUNDING);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    @Override
    public Competition initialiseProjectSetupColumns(Competition competition) {
        return commonBuilders.addDefaultProjectSetupColumns(competition);
    }

    public static List<QuestionBuilder> ktpDefaultQuestions() {
        return newArrayList(
                impact(),
                innovation(),
                challenge(),
                cohesiveness()
        );
    }

    public static SectionBuilder ktpAssessmentQuestions() {
        return aSection()
                .withName("Score Guidance")
                .withType(SectionType.KTP_ASSESSMENT);
    }

    public static QuestionBuilder innovation() {
        return aQuestion()
                .withShortName("Innovation")
                .withName("Innovation")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(false)
                .withMultipleStatuses(false)
                .withAssessorMaximumScore(MAXIMUM_ASSESSOR_SCORE)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.KTP_ASSESSMENT)
                .withFormInputs(defaultKtpAssessedQuestionFormInputs("innovation", innovationGuidanceRows()));
    }

    public static QuestionBuilder cohesiveness() {
        return aQuestion()
                .withShortName("Cohesiveness")
                .withName("Cohesiveness")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(false)
                .withMultipleStatuses(false)
                .withAssessorMaximumScore(MAXIMUM_ASSESSOR_SCORE)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.KTP_ASSESSMENT)
                .withFormInputs(defaultKtpAssessedQuestionFormInputs("cohesiveness", cohesivenessGuidanceRows()));
    }

    public static QuestionBuilder challenge() {
        return aQuestion()
                .withShortName("Challenge")
                .withName("Challenge")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(false)
                .withMultipleStatuses(false)
                .withAssessorMaximumScore(MAXIMUM_ASSESSOR_SCORE)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.KTP_ASSESSMENT)
                .withFormInputs(defaultKtpAssessedQuestionFormInputs("challenge", challengeGuidanceRows()));
    }

    public static QuestionBuilder impact() {
        return aQuestion()
                .withShortName("Impact")
                .withName("Impact")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(false)
                .withMultipleStatuses(false)
                .withAssessorMaximumScore(MAXIMUM_ASSESSOR_SCORE)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.KTP_ASSESSMENT)
                .withFormInputs(defaultKtpAssessedQuestionFormInputs("impact", impactGuidanceRows()));
    }

    private static List<GuidanceRowBuilder> impactGuidanceRows() {
        return newArrayList(
                aGuidanceRow()
                        .withSubject("9,10")
                        .withJustification("The application demonstrates a well-defined, realistic and positive impact on the business partner’s financial position (i.e. profit, turn-over, productivity and cost savings) and embeds new capabilities within the organisation, which will develop a culture of and capacity for ongoing innovation. There is potential for impact (economic or societal) beyond the partnership."),
                aGuidanceRow()
                        .withSubject("7,8")
                        .withJustification("The application demonstrates a well-defined, realistic and positive impact on the business partner’s financial position (i.e. profit, turn-over, productivity and cost savings) and embeds new capabilities within the organisation, which will develop a culture of and capacity for ongoing innovation. There is potential for impact (economic or societal) beyond the partnership."),
                aGuidanceRow()
                        .withSubject("5,6")
                        .withJustification("The application has realistic and defined impact on the business partner’s financial position and embeds realistic and defined new capabilities within the organisation."),
                aGuidanceRow()
                        .withSubject("3,4")
                        .withJustification("The application has unrealistic or ill-defined impact on the business partner’s financial position or fails to embed new capabilities within the organisation."),
                aGuidanceRow()
                        .withSubject("1,2")
                        .withJustification("The application fails to demonstrate realistic or feasible impact on the business partner’s financial position and does not embed new capabilities within the organisation.")
        );
    }

    private static List<GuidanceRowBuilder> cohesivenessGuidanceRows() {
        return newArrayList(
                aGuidanceRow()
                        .withSubject("9,10")
                        .withJustification("The application is clearly cohesive and easily demonstrates an outstanding balance between the various expectations of a KTP project.  The application gives a very high level of confidence that the project team will work well together, that the project will exceed its goals and the partners will gain in ways above and beyond the defined outcomes of the project."),
                aGuidanceRow()
                        .withSubject("7,8")
                        .withJustification("The application is ill-defined or only demonstrates a moderate to low level of cohesiveness between the individual elements, so the project is not likely to deliver it goals."),
                aGuidanceRow()
                        .withSubject("5,6")
                        .withJustification("The application is ill-defined or only demonstrates a moderate to low level of cohesiveness between the individual elements, so the project is not likely to deliver it goals."),
                aGuidanceRow()
                        .withSubject("3,4")
                        .withJustification("The application is ill-defined or only demonstrates a moderate to low level of cohesiveness between the individual elements, so the project is not likely to deliver it goals."),
                aGuidanceRow()
                        .withSubject("1,2")
                        .withJustification("The application fails to demonstrate any cohesiveness between the individual elements of the application.")
        );
    }

    private static List<GuidanceRowBuilder> challengeGuidanceRows() {
        return newArrayList(
                aGuidanceRow()
                        .withSubject("9,10")
                        .withJustification("The application clearly demonstrates that the project challenges the practices of the business partner and the markets it operates in. The application will clearly demonstrate that the knowledge base partner will be stretched in their translational thinking to deliver the project. The application will demonstrate that the associate will be at the centre of managing and delivering such a project and will get excellent technical and commercial exposure."),
                aGuidanceRow()
                        .withSubject("7,8")
                        .withJustification("The application demonstrates a realistic and well-defined challenge for the business partner in that its practices or processes will change. The application demonstrates that the knowledge base partner needs to deliver creative solutions and that the associate will be stretched in technical and commercial terms."),
                aGuidanceRow()
                        .withSubject("5,6")
                        .withJustification("The application demonstrates a realistic and well-defined challenge for the business partner, but it will not lead to a significant change of its practices. The application demonstrates that the knowledge base partner needs to deliver solutions based on current knowledge, without being cutting-edge solutions, and that the associate will be expected to show above-average technical and commercial skills."),
                aGuidanceRow()
                        .withSubject("3,4")
                        .withJustification("The application has an unrealistic, ill-defined or low level of challenge for either the knowledge base partner, the associate or the business partner."),
                aGuidanceRow()
                        .withSubject("1,2")
                        .withJustification("The application is clearly cohesive and easily demonstrates an outstanding balance between the various expectations of a KTP project.  The application gives a very high level of confidence that the project team will work well together, that the project will exceed its goals and the partners will gain in ways above and beyond the defined outcomes of the project.")
        );
    }

    private static List<GuidanceRowBuilder> innovationGuidanceRows() {
        return newArrayList(
                aGuidanceRow()
                        .withSubject("9,10")
                        .withJustification("The application demonstrates high likelihood that the business partner will become a leader in the field or best in class.  It will bring unique, innovative new products or services to market. The project will provide an opportunity for the business partner to do something new for itself or create a new commercial opportunity. The application demonstrates the business partner’s commitment to ongoing innovation."),
                aGuidanceRow()
                        .withSubject("7,8")
                        .withJustification("The application demonstrates well defined, substantial and realistic goals regarding the business partner’s product or market position and demonstrates that the project will lead to a new commercial opportunity and improve the innovation culture within the organisation."),
                aGuidanceRow()
                        .withSubject("5,6")
                        .withJustification("The application has realistic, defined goals regarding the business partner’s product or market position and indicates that the project will lead to a new commercial opportunity."),
                aGuidanceRow()
                        .withSubject("3,4")
                        .withJustification("The application has unrealistic or ill-defined goals regarding the business partner’s product or market position or fails to demonstrate that the project will lead to either a new commercial opportunity or an innovative culture."),
                aGuidanceRow()
                        .withSubject("1,2")
                        .withJustification("The application fails to demonstrate realistic or feasible innovation in the business partner’s product or market position and does not demonstrate a commitment to ongoing innovation.")
        );
    }

    public static List<FormInputBuilder> defaultKtpAssessedQuestionFormInputs(String questionName, List<GuidanceRowBuilder> guidanceRows) {
        return newArrayList(aFormInput()
                        .withType(FormInputType.ASSESSOR_SCORE)
                        .withScope(FormInputScope.ASSESSMENT)
                        .withGuidanceAnswer("Your score should be base of the following")
                        .withGuidanceTitle("Guidance for assessing " + questionName)
                        .withActive(true),
                aFormInput()
                        .withType(FormInputType.TEXTAREA)
                        .withScope(FormInputScope.ASSESSMENT)
                        .withGuidanceAnswer("Your score should be base of the following")
                        .withGuidanceTitle("Guidance for assessing " + questionName)
                        .withActive(true)
                        .withGuidanceRows(guidanceRows)
        );
    }
}
