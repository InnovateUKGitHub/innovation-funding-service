package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsReadOnlyViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.util.TermsAndConditionsUtil.VIEW_TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
import static org.innovateuk.ifs.util.TermsAndConditionsUtil.VIEW_TERMS_AND_CONDITIONS_OTHER;

@Component
public class TermsAndConditionsReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<TermsAndConditionsReadOnlyViewModel> {

    private ApplicationTermsModelPopulator applicationTermsModelPopulator;
    private ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator;
    private ProcessRoleRestService processRoleRestService;

    public TermsAndConditionsReadOnlyPopulator(ApplicationTermsModelPopulator applicationTermsModelPopulator,
                                               ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator,
                                               ProcessRoleRestService processRoleRestService) {
        this.applicationTermsModelPopulator = applicationTermsModelPopulator;
        this.applicationTermsPartnerModelPopulator = applicationTermsPartnerModelPopulator;
        this.processRoleRestService = processRoleRestService;
    }

    @Override
    public TermsAndConditionsReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {

        ProcessRoleResource processRoleResource = getLeadOrganisation(data);

        return new TermsAndConditionsReadOnlyViewModel(
                data,
                question,
                applicationTermsModelPopulator.populate(data.getUser(), data.getApplication().getId(), question.getId(), null, true),
                applicationTermsPartnerModelPopulator.populate(data.getApplication(), question.getId()),
                settings.isIncludeAssessment(),
                termsAndConditionsTerminology(competition),
                processRoleResource.getOrganisationId()
        );
    }

//    TODO IFS-9200 remove and replace with table, will be lead org for now just so link doesnt break
    private ProcessRoleResource getLeadOrganisation(ApplicationReadOnlyData data) {
        List<ProcessRoleResource> processRoleResourceList = processRoleRestService.findProcessRole(data.getApplicationId()).getSuccess();
        return processRoleResourceList.stream().filter(ProcessRoleResource::isLeadApplicant).findFirst().get();
    }

    private String termsAndConditionsTerminology(CompetitionResource competitionResource) {
        if(FundingType.INVESTOR_PARTNERSHIPS == competitionResource.getFundingType()) {
            return VIEW_TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
        }
        return VIEW_TERMS_AND_CONDITIONS_OTHER;
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(TERMS_AND_CONDITIONS);
    }
}