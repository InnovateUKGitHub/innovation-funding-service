package com.worth.ifs.application.form.validation;

import java.time.DateTimeException;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * This is responsible for validating the input application start date.
 * It does so by performing the validate method on the request.
 */
public class ApplicationStartDateValidator implements Validator {

	@Override
	public void validate(Object target, Errors errors) {
		HttpServletRequest req = (HttpServletRequest) target;
		
		String day = req.getParameter("application.startDate.dayOfMonth");
		String month = req.getParameter("application.startDate.monthValue");
		String year = req.getParameter("application.startDate.year");
		
		if(StringUtils.isEmpty(day) && StringUtils.isEmpty(month) && StringUtils.isEmpty(year)) {
			return;
		}
		
		LocalDate startDate;
		
		try {
			startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
		} catch (NumberFormatException | DateTimeException e) {
			 reject(errors);
			 return;
		}

		if (startDate.isBefore(LocalDate.now())) {
			 reject(errors);
        }
	}

	private void reject(Errors errors) {
		 errors.reject("application.startDate.invalid", "Please enter a future date.");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return HttpServletRequest.class.isAssignableFrom(clazz);
	}

}
