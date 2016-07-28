package com.worth.ifs.controller;

import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ValidationHandlerMethodArgumentResolverTest {

    @Test
    public void testCreateNewValidationHandler() throws Exception {

        ValidationHandlerMethodArgumentResolver resolver = new ValidationHandlerMethodArgumentResolver();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAttribute("some attribute", "Some value");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult("target", "name");
        bindingResult.addError(new ObjectError("name", "An error message"));

        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + "myBindingResult", bindingResult);

        ValidationHandler validationHandler = (ValidationHandler)
                resolver.resolveArgument(new MethodParameter(getClass().getMethod("aMethod"), 0), mavContainer,
                    new ServletWebRequest(new MockHttpServletRequest()), null);

        assertTrue(validationHandler.hasErrors());
        assertEquals("An error message", validationHandler.getAllErrors().get(0).getDefaultMessage());
        assertNull(ReflectionTestUtils.getField(validationHandler, "bindingResultTarget"));
    }

    @Test
    public void testCreateNewValidationHandlerWithAutoAttachmentToBindingResultTarget() throws Exception {

        ValidationHandlerMethodArgumentResolver resolver = new ValidationHandlerMethodArgumentResolver();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        TestForm bindingResultForm = new TestForm();
        mavContainer.addAttribute("bindingResultForm", bindingResultForm);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult("target", "name");
        bindingResult.addError(new ObjectError("name", "An error message"));

        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + "myBindingResult", bindingResult);

        ValidationHandler validationHandler = (ValidationHandler)
                resolver.resolveArgument(new MethodParameter(getClass().getMethod("aMethod"), 0), mavContainer,
                        new ServletWebRequest(new MockHttpServletRequest()), null);

        assertTrue(validationHandler.hasErrors());
        assertEquals("An error message", validationHandler.getAllErrors().get(0).getDefaultMessage());
        assertEquals(bindingResultForm, ReflectionTestUtils.getField(validationHandler, "bindingResultTarget"));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateExceptionIfBindingResultNotPrecedingValidationHandler() throws Exception {

        ValidationHandlerMethodArgumentResolver resolver = new ValidationHandlerMethodArgumentResolver();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAttribute("some attribute", "Some value");

        resolver.resolveArgument(new MethodParameter(getClass().getMethod("aMethod"), 0), mavContainer,
                new ServletWebRequest(new MockHttpServletRequest()), null);

    }

    @SuppressWarnings("unused")
    public void aMethod() {

    }

    public class TestForm extends BaseBindingResultTarget {

    }
}
