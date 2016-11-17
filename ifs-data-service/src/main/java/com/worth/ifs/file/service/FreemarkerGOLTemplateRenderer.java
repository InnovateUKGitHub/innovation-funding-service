package com.worth.ifs.file.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static com.worth.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.commons.service.ServiceResult.*;

/**
 * A Grant Offer Letter Template Service (a service that can process a template file in order to produce a Grant Offer Letter string) based
 * on Freemarker
 */
@Component
public class FreemarkerGOLTemplateRenderer implements GOLTemplateRenderer {

    @Autowired
    private Configuration configuration;
    private static final Log LOG = LogFactory.getLog(FreemarkerGOLTemplateRenderer.class);

    @Override
    public ServiceResult<String> renderTemplate(String templatePath, Map<String, Object> templateReplacements) {

        return handlingErrors(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE), () -> {

            Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);

            try {
                return getStringServiceResult(templatePath, replacementsWithCommonObjects);
            } catch (IOException | TemplateException e) {
                LOG.error("Error rendering GOL template " + templatePath, e);
                return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE));
            }
        });
    }

    private ServiceResult<String> getStringServiceResult(String templatePath, Map<String, Object> replacementsWithCommonObjects) throws IOException, TemplateException {
        Template temp = configuration.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        temp.process(replacementsWithCommonObjects, writer);
        return serviceSuccess(writer.getBuffer().toString());
    }

}
