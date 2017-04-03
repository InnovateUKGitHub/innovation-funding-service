package org.innovateuk.ifs.competition.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.innovateuk.ifs.util.RedirectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.innovateuk.ifs.application.service.AssessorFeedbackService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;

/**
 * This controller is to submit the assessor feedback.
 */
@Controller
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
public class AssessorFeedbackController {
	
	@Autowired
	private AssessorFeedbackService assessorFeedbackService;
	
	@Autowired
	private CookieFlashMessageFilter cookieFlashMessageFilter;
	
	@PostMapping("/competition/{competitionId}/assessorfeedback")
    public String assessorFeedbackCheck(Model model, HttpServletResponse response, @PathVariable("competitionId") Long competitionId){
		
		if(!assessorFeedbackService.feedbackUploaded(competitionId)) {
			cookieFlashMessageFilter.setFlashMessage(response, "feedbackNotUploadedForAllApplications");
			return "redirect:/competition/" + competitionId + "/applications";
		}
		
		model.addAttribute("competitionId", competitionId);
		
		return "assessor-feedback-confirmation";
    }
	
	@PostMapping("/competition/{competitionId}/assessorfeedbacksubmit")
    public String submitAssessorFeedback(HttpServletRequest request, HttpServletResponse response, @PathVariable("competitionId") Long competitionId){
		
		if(!assessorFeedbackService.feedbackUploaded(competitionId)) {
			cookieFlashMessageFilter.setFlashMessage(response, "feedbackNotUploadedForAllApplications");
			return "redirect:/competition/" + competitionId + "/applications";
		}
		
		assessorFeedbackService.submitAssessorFeedback(competitionId);

		return RedirectUtils.redirectToProjectSetupManagementService(request, "competition/" + competitionId + "/status/");
    }
	
}
