package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.populator.util.FinanceLinksUtil;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestoneViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.BreakdownTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class ApplicationProcurementMilestoneViewModelPopulator {
    public ApplicationProcurementMilestoneViewModel populate(ApplicationResource application, ApplicationProcurementMilestoneRestService applicationProcurementMilestoneRestService) {
        return viewModel(application, applicationProcurementMilestoneRestService);
    }

    private ApplicationProcurementMilestoneViewModel viewModel(ApplicationResource application, ApplicationProcurementMilestoneRestService applicationProcurementMilestoneRestService) {
        List<ApplicationProcurementMilestoneResource> applicationProcurementMilestoneResources = applicationProcurementMilestoneRestService.getByApplicationIdAndOrganisationId(application.getId(), application.getLeadOrganisationId()).getSuccess();

        return new ApplicationProcurementMilestoneViewModel(applicationProcurementMilestoneResources);
    }
}