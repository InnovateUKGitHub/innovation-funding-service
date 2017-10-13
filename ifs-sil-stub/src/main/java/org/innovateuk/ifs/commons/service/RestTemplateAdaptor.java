package org.innovateuk.ifs.commons.service;

import org.springframework.stereotype.Component;

// TODO qqRP This project is dependent on ifs-commons which has a RestTemplateAdaptor component.
// TODO qqRP This class shadows it so we don't need to worry about its dependencies. This is a short term solution.
// TODO qqRP Long term solution is to make the RestTemplateAdaptor not a component in ifs-common.
// TODO qqRP Projects Where it needs to be component would declare it.
@Component
public class RestTemplateAdaptor  {

}
