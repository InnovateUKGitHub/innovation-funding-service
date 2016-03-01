package com.worth.ifs.fileentry;

import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.profiling.ProfileExecution;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rav on 01/03/2016.
 *
 */
@Controller
@RequestMapping("/fileentry")
public class FileEntryController {
    @ProfileExecution
    @RequestMapping
    public String applicationForm(@ModelAttribute("form") ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                  HttpServletRequest request) throws Exception {
        return "application-form";
    }
}
