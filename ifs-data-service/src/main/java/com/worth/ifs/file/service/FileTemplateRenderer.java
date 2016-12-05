package com.worth.ifs.file.service;

import com.worth.ifs.commons.service.ServiceResult;

import java.util.Map;

/**
 *
 **/
public interface FileTemplateRenderer {
    ServiceResult<String> renderTemplate(String templatePath, Map<String, Object> templateReplacements);
}
