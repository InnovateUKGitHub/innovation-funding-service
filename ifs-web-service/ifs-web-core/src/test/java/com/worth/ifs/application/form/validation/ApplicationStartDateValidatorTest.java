package com.worth.ifs.application.form.validation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ApplicationStartDateValidatorTest {

	private ApplicationStartDateValidator validator;

	@Before
	public void setUp() {
		validator = new ApplicationStartDateValidator();
	}

	@Test
	public void testDoAllowInFuture() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = req("11","3","2020");

		validator.validate(request, errors);

		verifyNoMoreInteractions(errors);
	}

	@Test
	public void testDoNotAllowInPast() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = req("11","3","1985");

		validator.validate(request, errors);

		verify(errors).rejectValue("application.startDate", "validation.project.start.date.not.in.future");
	}

	@Test
	public void testDoNotAllowNonNumeric() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = req("abc","3","2020");

		validator.validate(request, errors);

		verify(errors).rejectValue("application.startDate", "validation.project.start.date.is.valid.date");
	}

	@Test
	public void testDoNotAllowInvalidDate() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = req("30","2","2020");

		validator.validate(request, errors);

		verify(errors).rejectValue("application.startDate", "validation.project.start.date.is.valid.date");
	}

	@Test
	public void testDoNotComplainAboutEmptyFields() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = req("","","");

		validator.validate(request, errors);

		verifyNoMoreInteractions(errors);
	}

	@Test
	public void testDoNotComplainAboutAbsentFields() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = mock(HttpServletRequest.class);

		validator.validate(request, errors);

		verifyNoMoreInteractions(errors);
	}

	@Test
	public void testDoNotAllowPartlyEmptyFields() {
		Errors errors = mock(Errors.class);
		HttpServletRequest request = req("","3","2020");

		validator.validate(request, errors);

		verify(errors).rejectValue("application.startDate", "validation.project.start.date.is.valid.date");
	}

	@Test
	public void testSupportsHttpServletRequestAndSubclasses() {
		assertTrue(validator.supports(HttpServletRequest.class));
		assertTrue(validator.supports(MockHttpServletRequest.class));
		assertFalse(validator.supports(Object.class));
	}

	private HttpServletRequest req(String day, String month, String year) {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter("application.startDate.dayOfMonth")).thenReturn(day);
		when(req.getParameter("application.startDate.monthValue")).thenReturn(month);
		when(req.getParameter("application.startDate.year")).thenReturn(year);
		return req;
	}
}
