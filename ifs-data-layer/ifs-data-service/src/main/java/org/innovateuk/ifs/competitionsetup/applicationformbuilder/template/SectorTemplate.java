package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aDefaultAssessedQuestion;

@Component
public class SectorTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.SECTOR;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setGrantClaimMaximums(commonBuilders.getStateAidGrantClaimMaxmimums());
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(36);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        QuestionBuilder scopeQuestion = scope();
            scopeQuestion.getFormInputs().stream()
                    .filter(fi -> fi.getType().equals(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE))
                    .findFirst()
                    .ifPresent(fi -> fi.withActive(false));
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails(),
                                researchCategory(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                publicDescription(),
                                scopeQuestion
                        )),
                applicationQuestions()
                        .withQuestions(sectorDefaultQuestions()),
                finances(),
                termsAndConditions()
        );

    }

    private static List<QuestionBuilder> sectorDefaultQuestions() {
        return newArrayList(
                needOrChallenge(),
                approachAndInnovation(),
                teamAndResources(),
                marketAwareness(),
                outcomes(),
                widerImpacts(),
                projectManagement(),
                risks(),
                additionality(),
                costsAndValueForMoney()
        );
    }

    private static QuestionBuilder needOrChallenge() {
        return aDefaultAssessedQuestion()
                .withShortName("Need or challenge")
                .withName("What is the business need, technological challenge or market opportunity behind your innovation?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the need or challenge section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">the main motivation for the project; the business need, technological challenge or market opportunity</li><li class=\"li2\">the nearest current state-of-the-art (including those near-market/in development) and its limitations</li><li class=\"li2\">any work you have already done to respond to this need, for example is the project focused on developing an existing capability or building a new one?</li><li class=\"li2\">the wider economic, social, environmental, cultural and/or political challenges which are influential in creating the opportunity (for example, incoming regulations). Our <a href=\"http://www.slideshare.net/WebadminTSB/innovate-uk-horizons-sustainable-economy-framework\">Horizons tool</a> can help.</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing need or challenge")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("There is a compelling business motivation for the project. There is a clear understanding of the nearest state-of-the-art available.  The applicant has shown, if applicable, how the project will build on previous relevant work. Any wider factors influencing this opportunity are identified."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("There is a good motivation for the project. There is a good awareness of the nearest state-of-the-art and wider factors influencing the opportunity."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project motivation is good but there is a lack of understanding of the nearest state-of-the-art or wider factors influencing this opportunity."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("Project motivation is poorly defined or not relevant to the applicant or team.  References to the current state-of-the-art are not offered or are not relevant."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("There is little or no business drive to the project.  References to the current state-of-the-art are not offered or are not relevant.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder approachAndInnovation() {
        return aDefaultAssessedQuestion()
                .withShortName("Approach and innovation")
                .withName("What approach will you take and where will the focus of the innovation be?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the approach and innovation section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">how you will respond to the need, challenge or opportunity identified</li><li class=\"li2\">how you will improve on the nearest current state-of-the-art identified</li><li class=\"li2\">where the focus of the innovation will be in the project (application of existing technologies in new areas, development of new technologies for existing areas or a totally disruptive approach) and the freedom you have to operate</li><li class=\"li2\">how this project fits with your current product or service lines or offerings</li><li class=\"li2\">how it will make you more competitive</li><li class=\"li2\">the nature of the outputs you expect from the project (for example, report, demonstrator, know-how, new process, product or service design) and how these will help you to target the need, challenge or opportunity identified</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing approach and innovation")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The approach addresses the need, challenge or opportunity identified in Q1. The main innovations and risks are identified. Evidence is presented to show how the innovation and project outputs will differentiate from those of competitors.  The project is significantly innovative either commercially or technically and will make a substantial contribution to the field.  Solid evidence is presented to substantiate the level of innovation and freedom to operate."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project addresses the need or challenge and the main innovations and risks are highlighted.  Evidence shows that the proposed development is innovative and that the applicant has the freedom to operate. It is demonstrated how the project outputs will differentiate from those of competitors."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project may address the need or challenge identified in Q1 and the innovations are highlighted. The level of innovation or freedom to operate is not strongly backed up with evidence. The main risks are not fully identified. Innovation focus is plausible and shows a link to improvements in competitiveness and/or productivity."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The approach is poorly defined with an unconvincing link to the need or challenge identified in Q1. Improvement in competiveness and/or productivity is not very convincing."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The approach is not well defined or inconsistent with the need or challenge identified in Q1.  There is no identification of how this will improve competitiveness.")
                                                )),
                                appendixBuilder ->
                                        appendixBuilder
                                                .withActive(true)
                                                .withWordCount(1)
                                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                                .withGuidanceAnswer("<p>You can submit up to 2 pages to provide graphics, diagrams or an image to explain your innovation.</p>")

                        )
                );
    }

    private static QuestionBuilder teamAndResources() {
        return aDefaultAssessedQuestion()
                .withShortName("Team and resources")
                .withName("Who is in the project team and what are their roles?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the team and resources section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">the roles, skills and relevant experience of all members of the project team in relation to the approach you will be taking</li><li class=\"li2\">the resources, equipment and facilities needed for the project and how you will access them</li><li class=\"li2\">details of any vital external parties, including sub-contractors, who you will need to work with to successfully carry out the project</li><li class=\"li2\">(if collaborative) the current relationships between project partners and how these will change as a result of the project</li><li class=\"li2\">highlight any gaps in the team that will need to be filled</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing team and resources")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The applicant or consortium is well placed to carry out the project and exploit the results.  There is a clear plan  to obtain all the resources, equipment and facilities they will need.  There is strong evidence that the consortium will work well."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The applicant or consortium makes sense given the approach described in Q2.  The applicant indicates how access will be obtained to all the resources, equipment and facilities they will need. The consortium is likely to work well."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The applicant or consortium has most, but not all, of the required skills and experience required.  It is unclear whether or not the consortium will work well together."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("There are significant gaps in the consortium with little or no information about how these will be filled.  There may be some partners with little relevance to the project activities."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The applicant or consortium will not be capable of either carrying out the project or exploiting the results.")
                                                )),
                                appendixBuilder ->
                                        appendixBuilder
                                                .withActive(true)
                                                .withWordCount(1)
                                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                                .withGuidanceAnswer("<p>You can submit up to 4 pages to describe the skills and experience of the main people working on the project.</p>")

                        )
                );
    }

    private static QuestionBuilder marketAwareness() {
        return aDefaultAssessedQuestion()
                .withShortName("Market awareness")
                .withName("What does the market you are targeting look like?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the market awareness section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">the markets (domestic and/or international) you will be targeting in the project and any other potential markets</li><li class=\"li2\">for the target markets, describe:</li><ul><li class=\"li2\">the size of the target markets for the project outcomes, backed up by references where available</li><li class=\"li2\">the structure and dynamics of the market (such as customer segmentation), together with predicted growth rates within clear timeframes</li><li class=\"li2\">the main supply or value chains and business models in operation (and any barriers to entry)</li><li class=\"li2\">the current UK position in targeting this market</li></ul><li class=\"li2\">for highly innovative projects, where the market may be unexplored, explain:</li><ul><li class=\"li2\">what the route to market could or might be</li><li class=\"li2\">what its size might be</li><li class=\"li2\">how the project will look to explore the market potential</li></ul><li class=\"li2\">briefly describe the size and main features of any other markets not already listed</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing market awareness")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The target market size, drivers and dynamics are fully quantified and evidenced. Where the market is new or unexplored, possible routes are identified based on precedents. Relevant secondary markets are substantiated and described in brief."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("There is a good awareness of the target market’s drivers and dynamics. The market size is quantified with some evidence. For a new market, a good attempt is made at describing the possible routes to market and estimating the market size. Relevant secondary markets are described showing good awareness."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The general market size and dynamics are understood but the addressable market is poorly quantified.  Secondary markets are mentioned but little information is offered."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("Some information about the general market is offered but the extent of the addressable market for the project is not described.  Secondary markets are barely mentioned."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The market is poorly defined or is irrelevant to the motivations of the project.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder outcomes() {
        return aDefaultAssessedQuestion()
                .withShortName("Outcomes and route to market")
                .withName("How are you going to grow your business and increase your productivity into the long term as a result of the project?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the outcomes and route to market section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">your current position in the markets and supply or value chains outlined (will you be extending or establishing your market position?)</li><li class=\"li2\">your target customers and/or end users, and the value to them (why would they use or buy it?)</li><li class=\"li2\">your route to market</li><li class=\"li2\">how you are going to profit from the innovation (increased revenues or cost reduction)</li><li class=\"li2\">how the innovation will impact your productivity and growth (in the short and long-term)</li><li class=\"li2\">how you will protect and exploit the outputs of the project, for example through know-how, patenting, designs, changes to business model</li><li class=\"li2\">your strategy for targeting the other markets identified during or after the project</li><li class=\"li2\">for any research organisation activity in the project, describe your plans to spread project research outputs over a reasonable timescale</li><li class=\"li2\">if you expect to use the results generated from the project in further research activities, describe how</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing outcomes and route to market")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("Target customers are identified along with the value proposition to them. The routes to market and how profit, productivity and growth will increase is identified and evidenced. The exploitation and/or dissemination of the main project outputs is outlined."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("Target customers are identified along with the value proposition to them. The routes to market and how profit, productivity and growth will increase is outlined with some evidence. The exploitation and/or dissemination of the main project outputs is outlined."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("Target customer types are described but the value proposition to them is less clear. There is some information about how profit, productivity or growth increases may be achieved at some point."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("There is some information about the target customer types but there is little about the value proposition or how profit, productivity or growth will be affected."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The applicant provides little or no information about the target customers.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder widerImpacts() {
        return aDefaultAssessedQuestion()
                .withShortName("Wider impacts")
                .withName("What impact might this project have outside the project team?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the wider impacts section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe and where possible measure:</p><ul><li class=\"li2\">the economic benefits from the project to external parties (customers, others in the supply chain, broader industry and the UK economy) such as productivity increases and import substitution</li><li class=\"li2\">any expected social impacts, either positive or negative on, for example:</li><ul><li class=\"li2\">quality of life</li><li class=\"li2\">social inclusion or exclusion</li><li class=\"li2\">jobs (safeguarded, created, changed, displaced)</li><li class=\"li2\">education</li><li class=\"li2\">public empowerment</li><li class=\"li2\">health and safety</li><li class=\"li2\">regulations</li><li class=\"li2\">diversity</li></ul><li class=\"li2\">any expected impact on government priorities</li><li class=\"li2\">any expected environmental impacts, either positive or negative</li><li class=\"li2\">identify any expected regional impacts of the project</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing wider impacts")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The positive impact on others outside of the team is understood (such as supply chain partners, customers, broader industry). Social, economic and/or environmental impacts are considered. Expected regional impacts are described with compelling evidence to justify claims. Any possible negative impacts are fully mitigated where appropriate."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("There is good awareness of how the project may impact others outside of the team. Expected regional impacts are described. Any possible negative impacts are partially mitigated where appropriate."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("There  is basic awareness of how the project could impact some others outside the project. Some relevant stakeholders are not considered.  Little mitigation is offered where there may be negative impacts."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The applicant provides some information about possible impacts but significant gaps remain."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("There is no information about how the project might impact others or the project would be detrimental to other UK interests.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder projectManagement() {
        return aDefaultAssessedQuestion()
                .withShortName("Project management")
                .withName("How will you manage the project effectively?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project management section?")
                                                .withGuidanceAnswer("<p>You should describe or explain:</p>\r\n<ul>\r\n <li class=\"li2\">the main work packages of the project, indicating the relevant research category, the lead partner assigned to each and the total cost of each one</li>\r\n <li class=\"li2\">your approach to project management, identifying any major tools and mechanisms that will be used for a successful and innovative project outcome.</li>\r\n <li class=\"li2\">the management reporting lines</li>\r\n <li class=\"li2\">your project plan in enough detail to identify any links or dependencies between work packages or milestones</li>\r\n</ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing project management")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project work packages are outlined with the research category, lead partner and total cost provided for each one.  The approach to project management is described.  The plan is designed to meet the objectives of the project in a realistic and efficient way.  Any links or dependencies between work packages or milestones are identified."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project work packages are outlined with the research category, lead partner and total cost provided for each one.  The approach to project management is stated.  The plan seems appropriate to the project objectives.  Any links or dependencies between work packages or milestones are identified."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project work packages are outlined but there are some details missing.  The plan seems reasonable but not tailored to the objectives of the project."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The plan has serious deficiencies or major missing aspects. The plan has little chance of meeting the objectives of the project."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The plan is totally unrealistic or fails to meet the objectives of the project.")
                                                )),
                                appendixBuilder ->
                                        appendixBuilder
                                                .withActive(true)
                                                .withWordCount(1)
                                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                                .withGuidanceAnswer("<p>You can submit a project plan or Gantt chart of up to 2 pages.</p>")

                        )
                );
    }

    private static QuestionBuilder risks() {
        return aDefaultAssessedQuestion()
                .withShortName("Risks")
                .withName("What are the main risks for this project?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the risks section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">the main risks and uncertainties of the project, including the technical, commercial, managerial and environmental risks, (providing a risk register if appropriate)</li><li class=\"li2\">how will these risks be mitigated</li><li class=\"li2\">any project inputs that are critical to completion (such as resources, expertise, data sets)</li><li class=\"li2\">any output likely to be subject to regulatory requirements, certification, ethical issues, etc, and how will you manage this?</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing risks")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The key risks and uncertainties of the project are considered and mitigated.  Critical inputs to the project are identified.  Relevant constraints or conditions on the project outputs (regulatory requirements, certification or ethical issues) are identified. The risk analysis is appropriate and professional."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The key risks and uncertainties of the project are considered with appropriate mitigations. Relevant constraints or conditions on the project outputs are identified."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("Most major risks have been identified but there are some gaps or the mitigation and management is insufficient to properly control the risks."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The risk analysis is poor or misses major areas of risk. The mitigation and management is poor."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The risk analysis is superficial with minimal mitigation or management suggested.")
                                                )),
                                appendixBuilder ->
                                        appendixBuilder
                                                .withActive(true)
                                                .withWordCount(1)
                                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                                .withGuidanceAnswer("<p>You can submit a risk register of up to 2 pages.</p>")

                        )
                );
    }

    private static QuestionBuilder additionality() {
        return aDefaultAssessedQuestion()
                .withShortName("Additionality")
                .withName("Describe the impact that an injection of public funding would have on this project.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the additionality section?")
                                                .withGuidanceAnswer("<p class=\"p1\">You should describe or explain:</p><ul><li class=\"li2\">if this project could go ahead in any form without public funding and if so, the difference the public funding would make (such as faster to market, more partners and reduced risk)</li><li class=\"li2\">the likely impact of the project on the business of the partners involved</li><li class=\"li2\">why you are not able to wholly fund the project from your own resources or other forms of private-sector funding (what would happen if the application is unsuccessful)</li><li class=\"li2\">how this project would change the nature of R&amp;D activity the partners would undertake (and related spend)</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing additionality")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("There is a compelling case for the positive difference funding will make. Alternative sources of support are described with an explanation of why they are discounted or used in conjunction with the grant funding.  The project will significantly increase the industrial partners’ R&D spend during the project and afterwards."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The arguments for public funding are good and justified. The project will significantly increase the industrial partners’ commitment to R&D."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The public funding arguments are acceptable but the difference made by the grant will be modest. The project will improve the industrial partners’ commitment to R&D."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The funding arguments are poor or not sufficiently justified. There is not likely to be any improvement to the industrial partner’s commitment to R&D."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("There is no justification for public funding and no reason why the applicant should not fund the work.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder costsAndValueForMoney() {
        return aDefaultAssessedQuestion()
                .withShortName("Costs and value for money")
                .withName("How much will the project cost and how does it represent value for money for the team and the taxpayer?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the costs and value for money section?")
                                                .withGuidanceAnswer("<p>You should describe or explain:</p>\r\n<ul>\r\n <li class=\"li2\">the total project cost and the grant being requested in terms of the project goals</li>\r\n <li class=\"li2\">how the partners will finance their contributions to the project</li>\r\n <li class=\"li2\">how this project represents value for money for you and the taxpayer and how it compares to what you would spend your money on otherwise?</li>\r\n <li class=\"li2\">the balance of costs and grant across the project partners</li>\r\n <li class=\"li2\">any sub-contractor costs and why they are critical to the project</li>\r\n</ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing costs and value for money")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project costs are entirely appropriate and represent excellent value for money compared to alternative approaches outlined (including doing nothing). The partners have a clear idea of how they will finance their contribution. The balance of costs and grants between partners, and use of subcontractors is justified and reasonable for the proposed project."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project costs are appropriate and should be sufficient to successfully complete the project. The balance of costs and grants between partners, and use of subcontractors seems reasonable The project represents good value for money compared to alternative outlined approaches (including doing nothing)."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The public funding arguments are acceptable but the difference made by the grant will be modest. The project will improve the industrial partners’ commitment to R&D."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The project costs seem too high or too low given the proposed project. The split of costs and grants between partners is unbalanced, or inappropriate use is being made of subcontractors."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The costs are not appropriate or justified. The balance between partners and subcontractors is not justified.")
                                                ))
                        )
                );
    }

}
