package org.innovateuk.ifs.cfg;

import org.innovateuk.ifs.assessment.period.mapper.AssessmentPeriodMapperImpl;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapperImpl;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapperImpl;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapperImpl;
import org.innovateuk.ifs.competition.mapper.*;
import org.innovateuk.ifs.competitionsetup.mapper.CompetitionDocumentMapperImpl;
import org.innovateuk.ifs.file.mapper.FileEntryMapperImpl;
import org.innovateuk.ifs.file.mapper.FileTypeMapperImpl;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapperImpl;
import org.innovateuk.ifs.form.mapper.*;
import org.innovateuk.ifs.granttransfer.mapper.EuActionTypeMapper;
import org.innovateuk.ifs.granttransfer.mapper.EuActionTypeMapperImpl;
import org.innovateuk.ifs.granttransfer.mapper.EuGrantTransferMapper;
import org.innovateuk.ifs.granttransfer.mapper.EuGrantTransferMapperImpl;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapperImpl;
import org.innovateuk.ifs.project.grantofferletter.template.mapper.GolTemplateMapperImpl;
import org.innovateuk.ifs.user.mapper.UserMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    EuActionTypeMapper euActionTypeMapper() {
        return new EuActionTypeMapperImpl();
    }

    @Bean
    EuGrantTransferMapper euGrantTransferMapper() { return new EuGrantTransferMapperImpl(); }

    @Bean
    FormInputMapperImpl formInputMapper() {
        return new FormInputMapperImpl();
    }

    @Bean
    FileEntryMapperImpl fileEntryMapper() { return new FileEntryMapperImpl(); }

    @Bean
    FormValidatorMapperImpl formValidatorMapper() {
        return new FormValidatorMapperImpl();
    }

    @Bean
    QuestionMapperImpl questionMapper() {
        return new QuestionMapperImpl();
    }

    @Bean
    SectionMapperImpl sectionMapper() {
        return new SectionMapperImpl();
    }

    @Bean
    CompetitionMapperImpl competitionMapper() {
        return new CompetitionMapperImpl();
    }

    @Bean
    UserMapperImpl userMapper() {
        return new UserMapperImpl();
    }

    @Bean
    InnovationAreaMapperImpl innovationAreaMapper() {
        return new InnovationAreaMapperImpl();
    }

    @Bean
    GrantClaimMaximumMapperImpl grantClaimMaximumMapper() {
        return new GrantClaimMaximumMapperImpl();
    }

    @Bean
    CompetitionDocumentMapperImpl competitionDocumentMapper() {
        return new CompetitionDocumentMapperImpl();
    }

    @Bean
    FileTypeMapperImpl fileTypeMapper() {
        return new FileTypeMapperImpl();
    }

    @Bean
    GolTemplateMapperImpl golTemplateMapper() {
        return new GolTemplateMapperImpl();
    }

    @Bean
    CompetitionThirdPartyConfigMapperImpl competitionThirdPartyConfigMapper() {
        return new CompetitionThirdPartyConfigMapperImpl();
    }

    @Bean
    GuidanceRowMapperImpl guidanceRowMapper() {
        return new GuidanceRowMapperImpl();
    }

    @Bean
    MultipleChoiceOptionMapperImpl multipleChoiceOptionMapper() {
        return new MultipleChoiceOptionMapperImpl();
    }

    @Bean
    InnovationSectorMapperImpl innovationSectorMapper() {
        return new InnovationSectorMapperImpl();
    }

    @Bean
    ResearchCategoryMapperImpl researchCategoryMapper() {
        return new ResearchCategoryMapperImpl();
    }

    @Bean
    MilestoneMapperImpl milestoneMapper() {
        return new MilestoneMapperImpl();
    }

    @Bean
    AssessmentPeriodMapperImpl assessmentPeriodMapper() {
        return new AssessmentPeriodMapperImpl();
    }

    @Bean
    CompetitionTypeMapperImpl competitionTypeMapper() {
        return new CompetitionTypeMapperImpl();
    }

    @Bean
    CompetitionFunderMapperImpl competitionFunderMapper() {
        return new CompetitionFunderMapperImpl();
    }

    @Bean
    OrganisationTypeMapperImpl organisationTypeMapper() {
        return new OrganisationTypeMapperImpl();
    }

    @Bean
    GrantTermsAndConditionsMapperImpl grantTermsAndConditionsMapper() {
        return new GrantTermsAndConditionsMapperImpl();
    }

}
