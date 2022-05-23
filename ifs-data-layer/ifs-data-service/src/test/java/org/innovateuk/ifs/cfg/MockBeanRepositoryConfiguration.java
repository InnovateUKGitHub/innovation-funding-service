package org.innovateuk.ifs.cfg;

import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.competition.repository.*;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.form.repository.*;
import org.innovateuk.ifs.granttransfer.repository.EuActionTypeRepository;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.grantofferletter.template.repository.GolTemplateRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockBeanRepositoryConfiguration {

    @MockBean
    private InnovationAreaRepository innovationAreaRepository;

    @MockBean
    private InnovationSectorRepository innovationSectorRepository;

    @MockBean
    private FileEntryRepository fileEntryRepository;

    @MockBean
    private FormValidatorRepository formValidatorRepository;

    @MockBean
    private MilestoneRepository milestoneRepository;

    @MockBean
    private FileTypeRepository fileTypeRepository;

    @MockBean
    private CompetitionThirdPartyConfigRepository competitionThirdPartyConfigRepository;

    @MockBean
    private GuidanceRowRepository guidanceRowRepository;

    @MockBean
    private GolTemplateRepository golTemplateRepository;

    @MockBean
    private CompetitionTypeRepository competitionTypeRepository;

    @MockBean
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @MockBean
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @MockBean
    private OrganisationTypeRepository organisationTypeRepository;

    @MockBean
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @MockBean
    private CompetitionRepository competitionRepository;

    @MockBean
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @MockBean
    private ApplicationRepository applicationRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @MockBean
    private AddressTypeRepository addressTypeRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private FormInputRepository formInputRepository;

    @MockBean
    private ResearchCategoryRepository researchCategoryRepository;

    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private OrganisationRepository organisationRepository;

    @MockBean
    private EuGrantTransferRepository euGrantTransferRepository;

    @MockBean
    private EuActionTypeRepository euActionTypeRepository;

    @MockBean
    private ProcessRoleRepository processRoleRepository;

    @MockBean
    private UserRepository userRepository;

}
