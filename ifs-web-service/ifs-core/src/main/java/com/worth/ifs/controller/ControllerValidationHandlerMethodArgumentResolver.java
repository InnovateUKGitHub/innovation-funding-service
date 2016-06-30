/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worth.ifs.controller;

import org.springframework.core.MethodParameter;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;

import static com.worth.ifs.controller.ControllerValidationHandler.newBindingResultHandler;

/**
 * Wraps a BindingResult in a helper that provides consistent ways to work with API validation as well as front-end
 * validation and binding errors.  The developer is able to add one of these as a method parameter after a
 * @ModelAttribute parameter that would classically have BindingErrors associated with it
 */
public class ControllerValidationHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return ControllerValidationHandler.class.isAssignableFrom(paramType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		ModelMap model = mavContainer.getModel();

		if (!model.isEmpty()) {

            int lastIndex = model.size() - 1;
            String lastKey = new ArrayList<>(model.keySet()).get(lastIndex);

            if (lastKey.startsWith(BindingResult.MODEL_KEY_PREFIX)) {

                // create a new ControllerValidationHandler that wraps a BindingResult
                BindingResult bindingResult = (BindingResult) model.get(lastKey);
                ControllerValidationHandler controllerValidationHandler = newBindingResultHandler(bindingResult);

                // if the BindingResult was targeting a @ModelAttribute that is also a BindingResultTarget,
                // get this ControllerValidationHandler to automatically set any errors it'll encounter on the
                // BindingResultTarget upon error
                int secondToLastIndex = model.size() - 2;
                String secondToLastKey = new ArrayList<>(model.keySet()).get(secondToLastIndex);
                Object bindingResultTarget = model.get(secondToLastKey);

                if (bindingResultTarget instanceof BindingResultTarget) {
                    controllerValidationHandler.setBindingResultTarget((BindingResultTarget) bindingResultTarget);
                }

                return controllerValidationHandler;
			}
		}

		throw new IllegalStateException(
				"A ControllerValidationHandler argument is expected to be declared immediately after a BindingResult " +
						"for a model attribute to which they apply, or the model attribute itself: " + parameter.getMethod());
	}

}
