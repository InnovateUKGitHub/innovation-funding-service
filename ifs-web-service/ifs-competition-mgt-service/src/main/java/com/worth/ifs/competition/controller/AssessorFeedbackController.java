package com.worth.ifs.competition.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.worth.ifs.util.RedirectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.worth.ifs.application.service.AssessorFeedbackService;
import com.worth.ifs.filter.CookieFlashMessageFilter;

/**
 * This controller is to submit the assessor feedback.
 */
@Controller
public class AssessorFeedbackController {
	
	@Autowired
	private AssessorFeedbackService assessorFeedbackService;
	
	@Autowired
	private CookieFlashMessageFilter cookieFlashMessageFilter;
	
	@RequestMapping(method = RequestMethod.POST, value = "/competition/{competitionId}/assessorfeedback")
    public String assessorFeedbackCheck(Model model, HttpServletResponse response, @PathVariable("competitionId") Long competitionId){
		
		if(!assessorFeedbackService.feedbackUploaded(competitionId)) {
			cookieFlashMessageFilter.setFlashMessage(response, "feedbackNotUploadedForAllApplications");
			return "redirect:/competition/" + competitionId;
		}
		
		model.addAttribute("competitionId", competitionId);
		
		return "assessor-feedback-confirmation";
    }
	
	@RequestMapping(method = RequestMethod.POST, value = "/competition/{competitionId}/assessorfeedbacksubmit")
    public String submitAssessorFeedback(HttpServletRequest request, HttpServletResponse response, @PathVariable("competitionId") Long competitionId){
		
		if(!assessorFeedbackService.feedbackUploaded(competitionId)) {
			cookieFlashMessageFilter.setFlashMessage(response, "feedbackNotUploadedForAllApplications");
			return "redirect:/competition/" + competitionId;
		}
		
		assessorFeedbackService.submitAssessorFeedback(competitionId);

		return RedirectUtils.redirectToProjectSetupManagementService(request, "competition/" + competitionId + "/status/");
    }
	
}
