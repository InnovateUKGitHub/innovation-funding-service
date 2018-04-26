package org.innovateuk.ifs.commons.validation.matchers;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.ModelResultMatchers;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.util.AssertionErrors.assertTrue;

/*
 * Contains extra Mockito ResultMatcher functions for attribute field error message verification.
 */

public class ExtendedModelResultMatchers extends ModelResultMatchers {
    ExtendedModelResultMatchers() {

    }

    public ResultMatcher attributeHasFieldErrorMessage(final String name, final String fieldName, final String message) {
        return new ResultMatcher() {
            public void match(MvcResult mvcResult) throws Exception {
                ModelAndView mav = getModelAndView(mvcResult);
                BindingResult result = getBindingResult(mav, name);
                assertTrue("No errors for attribute '" + name + "'", result.hasErrors());
                boolean hasFieldErrors = result.hasFieldErrors(fieldName);
                assertTrue("No errors for field '" + fieldName + "' of attribute '" + name + "'", hasFieldErrors);
                String code = result.getFieldError(fieldName).getDefaultMessage();
                assertTrue("Expected error code '" + message + "' but got '" + message + "'", message.equals(message));
            }
        };
    }

    /**
     * Assert the given model attribute field(s) have no errors.
     */
    public ResultMatcher attributeHasNoFieldErrors(final String name, final String... fieldNames) {
        return new ResultMatcher() {
            public void match(MvcResult mvcResult) throws Exception {
                ModelAndView mav = getModelAndView(mvcResult);
                BindingResult result = getBindingResult(mav, name);
                assertTrue("Errors for attribute '" + name + "'", result.hasErrors());
                for (final String fieldName : fieldNames) {
                    boolean hasFieldErrors = !result.hasFieldErrors(fieldName);
                    assertTrue("Errors for field '" + fieldName + "' of attribute '" + name + "'", hasFieldErrors);
                }
            }
        };
    }

    private ModelAndView getModelAndView(MvcResult mvcResult) {
        ModelAndView mav = mvcResult.getModelAndView();
        assertTrue("No ModelAndView found", mav != null);
        return mav;
    }

    private BindingResult getBindingResult(ModelAndView mav, String name) {
        BindingResult result = (BindingResult) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
        assertTrue("No BindingResult for attribute: " + name, result != null);
        return result;
    }
}
