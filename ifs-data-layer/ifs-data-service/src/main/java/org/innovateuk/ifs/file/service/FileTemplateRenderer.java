package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.Map;

/**
 *
 **/
public interface FileTemplateRenderer {
    ServiceResult<String> renderTemplate(String templatePath, Map<String, Object> templateReplacements);
}
