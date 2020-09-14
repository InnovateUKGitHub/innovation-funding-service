package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aDefaultAssessedQuestion;

@Component
public class ProgrammeTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.PROGRAMME;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setGrantClaimMaximums(commonBuilders.getDefaultGrantClaimMaximums());
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(36);
        return competition;
    }

    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails(),
                                researchCategory(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                publicDescription(),
                                scope()
                        )),
                applicationQuestions()
                        .withQuestions(programmeDefaultQuestions()),
                finances(),
                termsAndConditions()
        );

    }

    public static List<QuestionBuilder> programmeDefaultQuestions() {
        return newArrayList(
                businessOpportunity(),
                potentialMarket(),
                projectExploitation(),
                economicBenefit(),
                technicalApproach(),
                innovation(),
                risks(),
                projectTeam(),
                funding(),
                addingValue()
        );
    }

    private static QuestionBuilder businessOpportunity() {
        return aDefaultAssessedQuestion()
                .withShortName("Business opportunity")
                .withName("What is the business opportunity that your project addresses?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the business opportunity section?")
                                                .withGuidanceAnswer("<p>You should describe:</p><ul class=\"list-bullet\">         <li>the business opportunity you have identified and how you plan to take advantage of it</li><li>the customer needs you have identified and how your project will meet them</li><li>the challenges you expect to face and how you will overcome them</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing business opportunity")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The applicants have a very clear understanding of the business opportunity and the problems that must be overcome to enable successful exploitation. The project is well aligned with these needs."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The applicants have a good idea of the potential market and opportunities. The needs of the customer are central to the project's objectives."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The business opportunity is plausible but not clearly expressed in terms of customer needs."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The business opportunity is unrealistic or poorly defined. The customer's true needs are not well understood and are not linked to the project's objectives."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("There is little or no business drive to the project. The results are not relevant to the target customers or no customer interests are provided.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder potentialMarket() {
        return aDefaultAssessedQuestion()
                .withShortName("Potential market")
                .withName("What is the size of the potential market for your project?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the market opportunity section?")
                                                .withGuidanceAnswer("<p>Describe the size of the potential market for your project including:</p><ul class=\"list-bullet\">         <li>details of your target market for instance how competitive and profitable it is</li><li>the current size of the market with actual and predicted growth rates</li><li>the market share you expect to achieve and the reasons for this estimate</li><li>the wider economic value you expect your project to add to the UK and/or the EEA (European Economic Area)</li></ul><p>Tell us what return on investment you expect your project to achieve. You should base this estimate on relevant industry data and tell us how you have calculated this.</p><p>If you are targeting an undeveloped market you should also:</p><ul class=\"list-bullet\">         <li>describe how you plan to access this market</li><li>estimate its potential size</li><li>explain how you will explore its potential</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing market opportunity")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The market size and dynamics are quantified clearly and to sufficient resolution to be relevant to the project. The market is clearly well understood. The return on investment is clearly stated, quantified and realistic."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The market size and dynamics are described with some quantification relevant to the project. Market understanding is acceptable and the return on investment is achievable."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The market size and dynamics are understood but poorly quantified or stated at a level not really relevant for the project. Return on investment is plausible or badly defined."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The market size is not quantified but there is some understanding. Return on investment is ill defined or unrealistic."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The market is not well defined or is wrong. No sensible return on investment is provided.")
                                                ))
                        )
                );
    }


    private static QuestionBuilder projectExploitation() {
        return aDefaultAssessedQuestion()
                .withShortName("Project exploitation")
                .withName("How will you exploit and market your project?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project exploitation section?")
                                                .withGuidanceAnswer("<p>Describe the potential outputs of the project, such as:</p><ul class=\"list-bullet\">         <li>products or services</li><li>processes</li><li>applications</li></ul><p>Describe how you will exploit these outputs, such as:</p><ul class=\"list-bullet\">         <li>the route to market</li><li>protection of intellectual property rights</li><li>reconfiguration of your organisation's value system</li><li>changes to business models and processes</li><li>any other methods of exploitation and protection</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing project exploitation")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The principle exploitable outputs of the project are identified together with clear and achievable exploitation methods. Dissemination opportunities are also identified and appropriate."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The main exploitable output of the project is identified and a realistic method defined. Some dissemination is also explained."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("An exploitation method is defined but lacking in detail or is only just feasible. Dissemination is mentioned."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The exploitation and dissemination methods described are unrealistic or ill-defined."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The exploitation method is missing or un-feasible and unlikely to succeed.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder economicBenefit() {
        return aDefaultAssessedQuestion()
                .withShortName("Economic benefit")
                .withName("What economic, social and environmental benefits do you expect your project to deliver and when?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the benefits section?")
                                                .withGuidanceAnswer("<p>Describe all the benefits you expect your project to deliver, including:</p><p><strong>Economic</strong> : this is the real impact the project will have on its economic environment. This is not traditional corporate accounting profit and can include cost avoidance. You should identify and quantify any expected benefits to:</p><ul class=\"list-bullet\"><li>users (intermediaries and end users)</li><li>suppliers</li><li>broader industrial markets</li><li>the UK economy</li></ul><p><strong>Social</strong> : quantify any expected social impacts either positive or negative on, for example:</p><ul class=\"list-bullet\"><li>quality of life</li><li>social inclusion or exclusion</li><li>education</li><li>public empowerment</li><li>health and safety</li><li>regulation</li><li>diversity</li><li>government priorities</li></ul><p><strong>Environmental</strong> : show how your project will benefit or have a low impact on the environment. For example, this could include:<p><ul class=\"list-bullet\"><li>careful management of energy consumption</li><li>reductions in carbon emissions</li><li>reducing manufacturing and materials waste</li><li>rendering waste less toxic before disposing of it in a safe and legal manner</li><li>re-manufacturing (cradle to cradle)</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing economic benefits")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("Inside and outside benefits are well defined, realistic and of significantly positive economic, environmental or social impact. Routes to exploit these benefits are also provided."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("Some positive outside benefits are defined and are realistic. Methods of addressing these opportunities are described."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("Some positive outside benefits are described but the methods to exploit these are not obvious. Or the project is likely to have a negative impact but some mitigation or a balance against the internal benefits is proposed."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The project has no outside benefits or is potentially damaging to other stakeholders. No mitigation or exploitation is suggested."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The project is damaging to other stakeholders with no realistic mitigation or balance described.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder technicalApproach() {
        return aDefaultAssessedQuestion()
                .withShortName("Technical approach")
                .withName("What technical approach will you use and how will you manage your project?")
                .withDescription("Describe the areas of work and your objectives. List all resource and management needs. Provide an overview of your technical approach.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the technical approach section?")
                                                .withGuidanceAnswer("<p>You should:</p><ul class=\"list-bullet\">         <li>describe your technical approach including the main objectives of the work</li><li>explain how and why your approach is appropriate</li><li>tell us how you will make sure that the innovative steps in your project are achievable</li><li>describe rival technologies and alternative R&D strategies</li><li>explain why your proposed approach will offer a better outcome</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing technical approach")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project plan is fully described and complete with milestones and timeframes. The plan is realistic and should meet the objectives of the project."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The plan is well described and complete. There is a reasonable chance that it will meet the objectives of the project."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The plan is not completely described or there may be deficiencies in some aspects. More work will be required before the plan can be said to be realistic."),
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
                                                .withGuidanceAnswer("<p>You can include an appendix of additional information to support the technical approach the project will undertake.</p><p>This can include for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li></ul>")

                        )
                );
    }

    private static QuestionBuilder innovation() {
        return aDefaultAssessedQuestion()
                .withShortName("Innovation")
                .withName("What is innovative about your project?")
                .withDescription("Explain how your project is innovative in both a commercial and technical sense.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project innovation section?")
                                                .withGuidanceAnswer("<p>You should show how your project will:</p><ul class=\"list-bullet\">         <li>push boundaries beyond current leading-edge science and technology</li><li>apply existing technologies in new areas</li></ul><p>Explain the novelty of the research in an industrial and/or academic context.</p><p>You should provide evidence that your proposed work is innovative. This could include patent search results, competitor analyses or literature surveys. If relevant, you should also outline your own intellectual property rights.</p>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing innovation")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project is significantly innovative either commercially or technically and will make a substantial contribution to the field. Solid evidence is presented to substantiate the level of innovation."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project will be innovative and relevant to the market. There is high confidence that there is freedom to operate."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project is innovative but there is a lack of presented evidence as to the freedom to operate."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The project lacks sufficient innovation both technically and commercially."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The project is either not innovative or there is no exploitable route due to previous IP.")
                                                )),
                                appendixBuilder ->
                                        appendixBuilder
                                                .withActive(true)
                                                .withWordCount(1)
                                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                                .withGuidanceAnswer("<p>You can include an appendix of additional information to support your answer. This appendix can include graphics describing the innovation or the nature of the problem. You can include evidence of freedom to operate, patent searches or competitor analysis as supporting information.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>not be any longer than 5 sides of A4. Longer appendices will only have the first 5 pages assessed</li></ul>")

                        )
                );
    }

    private static QuestionBuilder risks() {
        return aDefaultAssessedQuestion()
                .withShortName("Risks")
                .withName("What are the risks (technical, commercial and environmental) to your project's success? What is your risk management strategy?")
                .withDescription("We recognise that many of the projects we fund are risky. This is why we need to be sure that you have an adequate plan for managing this risk.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project risks section?")
                                                .withGuidanceAnswer("<p>Please describe your plans for limiting and managing risk. You need to:</p><ul class=\"list-bullet\">         <li>identify the project's main risks and uncertainties</li><li>detail specific technical, commercial, managerial and environmental risks</li><li>list any other uncertainties such as ethical issues associated with the project</li><li>provide a detailed risk analysis</li><li>rate the main risks as high, medium or low</li><li>show how you'll limit the main risks</li><li>identify the project management resources you'll use to minimise operational risk</li><li>include arrangements for managing the project team and its partners</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing risks")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("A thorough risk analysis has been presented across all 3 risk categories. The mitigation and risk management strategies proposed are also appropriate and professional."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("A good risk analysis has been carried out and the management methods and mitigation strategies proposed are realistic."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("Most major risks have been identified but there are some gaps or the mitigation and management is insufficient to properly control the risks."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The risk analysis is poor or misses major areas of risk. The mitigation and management is poor."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The risk analysis is superficial with minimal mitigation or management suggested.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder projectTeam() {
        return aDefaultAssessedQuestion()
                .withShortName("Project team")
                .withName("Does your project team have the skills, experience and facilities to deliver this project?")
                .withDescription("Describe your capability to develop and exploit this technology. Include details of your team's track record in managing research and development projects.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project skills section?")
                                                .withGuidanceAnswer("<p>You should show your project team:</p><ul class=\"list-bullet\"><li>has the right mix of skills and experience to complete the project</li><li>has clear objectives</li><li>would have been formed even without Innovate UK investment</li></ul><p>If you are part of a consortium, describe the benefits of the collaboration, for example, increased knowledge transfer.</p>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing team skills")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The consortium is ideally placed to carry out the project and exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The consortium is strong and contains all the required skills and experience. The consortium is likely to work well."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The consortium has most of the required skills and experience but there are a few gaps. The consortium will need to work hard to maintain a good working relationship."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("There are significant gaps in the consortium or the formation objectives are unclear. There could be some passengers or the balance of work/commitment is poor."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The consortium is not capable of either carrying out the project or exploiting the results.")
                                                )),
                                appendixBuilder ->
                                        appendixBuilder
                                                .withActive(true)
                                                .withWordCount(1)
                                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                                .withGuidanceAnswer("<p>You can include an appendix of additional information to detail the specific expertise and track record of each project partner and subcontractor. Academic collaborators can refer to their research standing.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>include up to half an A4 page per partner describing the skills and experience of the main people who will be working on the project</li></ul>")

                        )
                );
    }

    private static QuestionBuilder funding() {
        return aDefaultAssessedQuestion()
                .withShortName("Funding")
                .withName("What will your project cost?")
                .withDescription("Tell us the total costs of the project and how much funding you need from Innovate UK. Please provide details of your expected project costs along with any supporting information. Please justify any large expenditure in your project.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project cost section?")
                                                .withGuidanceAnswer("<p>You must:</p><ul class=\"list-bullet\"><li>show how your budget is realistic for the scale and complexity of the project</li><li>make sure the funding you need from Innovate UK is within the limit set by this competition</li><li>justify any significant costs in the project, such as subcontractors</li><li>show how much funding there will be from other sources</li><li>provide a realistic budget breakdown</li><li>describe and justify individual work packages</li></ul><p>Find out which <a href=\"https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance\">project costs are eligible.</a></p><p>If your project spans more than one type of research category, you must break down the costs as separate 'work packages'. For example, industrial research or experimental development. </p><p>You can find more information in the  <a href=\"https://www.gov.uk/guidance/innovate-uk-funding-general-guidance-for-applicants#funding-rules\">funding rules section</a> of this website.</p>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing project costs")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project costs are entirely appropriate. Any mix of research and development types (eg industrial research with some work packages of experimental development) is justified and costed correctly."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project costs are appropriate and should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The costs are not appropriate or justified. Any mix of research and development type is not justified.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder addingValue() {
        return aDefaultAssessedQuestion()
                .withShortName("Adding value")
                .withName("How does financial support from Innovate UK and its funding partners add value?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the financial support from Innovate UK section?")
                                                .withGuidanceAnswer("Justify why you're unable to fund the project yourself from commercial resources. Explain the difference this funding will make to your project. For example, will it lower the risk for you or speed up the process of getting your product to market? Tell us why this will benefit the UK.")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing added value")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project will significantly increase the industrial partners' R&D spend during the project and afterwards. The additionality arguments are very strong and believable."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project will increase the industrial partners' commitment to R&D. The additionality arguments are good and justified."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project will improve the industrial partners' commitment to R&D. The additionality arguments are just about acceptable."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("There is not likely to be any improvement to the industrial partner's commitment to R&D. The additionally arguments are poor or not sufficiently justified."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The work should be funded internally and does not deserve state funding.")
                                                ))
                        )
                );
    }

}
