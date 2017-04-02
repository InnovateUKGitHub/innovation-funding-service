package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.form.FindAssessorsFilterForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.form.OverviewAssessorsFilterForm;
import org.innovateuk.ifs.management.model.InviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to inviting assessors to a Competition.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementInviteAssessorsController {

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private InviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Autowired
    private InviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Autowired
    private InviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @GetMapping
    public String assessors(@PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%s/assessors/find", competitionId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) FindAssessorsFilterForm filterForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam MultiValueMap<String, String> queryParams) {
        CompetitionResource competition = competitionService.getById(competitionId);

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_FIND, queryParams);

        model.addAttribute("model", inviteAssessorsFindModelPopulator.populateModel(competition, page, filterForm.getInnovationArea(), originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/find";
    }

    @PostMapping(value = "/find", params = {"add"})
    public String addInviteFromFindView(Model model,
                                        @PathVariable("competitionId") long competitionId,
                                        @RequestParam("add") String email,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam Optional<Long> innovationArea) {
        inviteUser(email, competitionId).getSuccessObjectOrThrowException();

        return redirectToFind(competitionId, page, innovationArea);
    }

    @PostMapping(value = "/find", params = {"remove"})
    public String removeInviteFromFindView(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam("remove") String email,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam Optional<Long> innovationArea) {
        deleteInvite(email, competitionId).getSuccessObjectOrThrowException();

        return redirectToFind(competitionId, page, innovationArea);
    }

    private String redirectToFind(long competitionId, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/competition/{competitionId}/assessors/find")
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/invite")
    public String invite(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam MultiValueMap<String, String> queryParams) {
        if (form.getInvites().isEmpty()) {
            form.getInvites().add(new InviteNewAssessorsRowForm());
        }

        CompetitionResource competition = competitionService.getById(competitionId);

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_INVITE, queryParams);

        model.addAttribute("model", inviteAssessorsInviteModelPopulator.populateModel(competition, page, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/invite";
    }

    @PostMapping(value = "/invite", params = {"remove"})
    public String removeInviteFromInviteView(Model model,
                                             @PathVariable("competitionId") long competitionId,
                                             @RequestParam(name = "remove") String email,
                                             @RequestParam(defaultValue = "0") int page,
                                             @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        deleteInvite(email, competitionId);
        return redirectToInvite(competitionId, page);
    }

    @PostMapping(value = "/invite", params = {"addNewUser"})
    public String addNewUserToInviteView(Model model,
                                         @PathVariable("competitionId") long competitionId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                         @RequestParam MultiValueMap<String, String> queryParams) {
        form.getInvites().add(new InviteNewAssessorsRowForm());
        form.setVisible(true);

        return invite(model, competitionId, form, page, queryParams);
    }

    @PostMapping(value = "/invite", params = {"removeNewUser"})
    public String removeNewUserFromInviteView(Model model,
                                              @PathVariable("competitionId") long competitionId,
                                              @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                              @RequestParam(name = "removeNewUser") int position,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam MultiValueMap<String, String> queryParams) {
        form.getInvites().remove(position);
        form.setVisible(true);

        return invite(model, competitionId, form, page, queryParams);
    }

    @PostMapping(value = "/invite", params = {"inviteNewUsers"})
    public String inviteNewsUsersFromInviteView(Model model,
                                                @PathVariable("competitionId") long competitionId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam MultiValueMap<String, String> queryParams,
                                                @Valid @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                                @SuppressWarnings("unused") BindingResult bindingResult,
                                                ValidationHandler validationHandler) {
        form.setVisible(true);

        return validationHandler.failNowOrSucceedWith(
                () -> invite(model, competitionId, form, page, queryParams),
                () -> {
                    RestResult<Void> restResult = competitionInviteRestService.inviteNewUsers(
                            newInviteFormToResource(form, competitionId), competitionId
                    );

                    return validationHandler.addAnyErrors(restResult)
                            .failNowOrSucceedWith(
                                    () -> invite(model, competitionId, form, page, queryParams),
                                    () -> redirectToInvite(competitionId, page)
                            );
                }
        );
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/competition/{competitionId}/assessors/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/overview")
    public String overview(Model model,
                           @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) OverviewAssessorsFilterForm filterForm,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam MultiValueMap<String, String> queryParams) {
        CompetitionResource competition = competitionService.getById(competitionId);

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_OVERVIEW, queryParams);

        model.addAttribute("model", inviteAssessorsOverviewModelPopulator.populateModel(
                competition,
                page,
                filterForm.getInnovationArea(),
                filterForm.getStatus(),
                filterForm.getCompliant(),
                originQuery
        ));
        model.addAttribute("originQuery", originQuery);

        return "assessors/overview";
    }

    private ServiceResult<CompetitionInviteResource> inviteUser(String email, long competitionId) {
        return competitionInviteRestService.inviteUser(new ExistingUserStagedInviteResource(email, competitionId)).toServiceResult();
    }

    private ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return competitionInviteRestService.deleteInvite(email, competitionId).toServiceResult();
    }

    private NewUserStagedInviteListResource newInviteFormToResource(InviteNewAssessorsForm form, long competitionId) {
        List<NewUserStagedInviteResource> invites = form.getInvites().stream()
                .map(newUserInvite -> new NewUserStagedInviteResource(
                        newUserInvite.getEmail(),
                        competitionId,
                        newUserInvite.getName(),
                        form.getSelectedInnovationArea()
                ))
                .collect(Collectors.toList());

        return new NewUserStagedInviteListResource(invites);
    }
}
