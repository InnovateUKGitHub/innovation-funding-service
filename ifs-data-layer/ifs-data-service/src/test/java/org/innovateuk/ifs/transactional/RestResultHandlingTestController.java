package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

@Controller
@RequestMapping("/rest-result-handling-test-controller")
public class RestResultHandlingTestController {

    @GetMapping
    public @ResponseBody RestResult<ResultObject> get() {
        return restSuccess(new ResultObject("Hello!"));
    }

    @GetMapping("/bad-request")
    public @ResponseBody RestResult<ResultObject> badRequest() {
        return restFailure(badRequestError("Error!"));
    }
}
