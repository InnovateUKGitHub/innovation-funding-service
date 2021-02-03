package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.populator.CompetitionOverviewPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.service.ContentGroupRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * This controller will handle all requests that are related to a competition.
 */
@Controller
@RequestMapping("/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionController.class)
@PreAuthorize("permitAll")
public class CompetitionController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionOverviewPopulator overviewPopulator;

    @Autowired
    private ContentGroupRestService contentGroupRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @GetMapping("overview")
    public String competitionOverview(final Model model,
                                      @PathVariable("competitionId") final long competitionId,
                                      UserResource loggedInUser) {
        final PublicContentItemResource publicContentItem = publicContentItemRestService.getItemByCompetitionId(competitionId).getSuccess();
        model.addAttribute("model", overviewPopulator.populateViewModel(publicContentItem, loggedInUser != null));
        return "competition/overview";
    }

    @GetMapping("download/{contentGroupId}")
    public ResponseEntity<ByteArrayResource> getFileDetails(@PathVariable("competitionId") final long competitionId,
                                                            @PathVariable("contentGroupId") final long contentGroupId) {
        final ByteArrayResource resource = contentGroupRestService.getFileAnonymous(contentGroupId).getSuccess();
        final FileEntryResource fileDetails = contentGroupRestService.getFileDetailsAnonymous(contentGroupId).getSuccess();
        return getFileResponseEntity(resource, fileDetails);
    }

    @GetMapping("info/terms-and-conditions")
    public String termsAndConditions(@PathVariable("competitionId") final long competitionId, Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        GrantTermsAndConditionsResource termsAndConditions = competition.getTermsAndConditions();
        model.addAttribute("model", new CompetitionTermsViewModel(competitionId));
        return "competition/info/" + termsAndConditions.getTemplate();
    }

    @GetMapping("info/state-aid-terms-and-conditions")
    public String stateAidTermsAndConditions(@PathVariable("competitionId") final long competitionId, Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competition.getFundingRules() == FundingRules.SUBSIDY_CONTROL && !competition.isExpressionOfInterest()) {
            GrantTermsAndConditionsResource termsAndConditions = competition.getOtherFundingRulesTermsAndConditions();
            model.addAttribute("model", new CompetitionTermsViewModel(competitionId));
            return "competition/info/" + termsAndConditions.getTemplate();
        }
        return "redirect:/competition/setup/" + competitionId;
    }

    @GetMapping("info/terms-and-conditions/full")
    public @ResponseBody ResponseEntity<ByteArrayResource> additionalTerms(@PathVariable("competitionId") final long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return getFileResponseEntity(competitionRestService.downloadTerms(competitionId).getSuccess(), competition.getCompetitionTerms());
    }

}