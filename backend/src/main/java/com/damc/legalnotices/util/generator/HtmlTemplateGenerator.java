package com.damc.legalnotices.util.generator;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.config.templates.StringTemplateResolver;
import com.damc.legalnotices.config.templates.TemplateResolver;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class HtmlTemplateGenerator {

    private final LocationProperties appConfig;

    public String generate(Map<String, Object> props, String templateName) {
        Context context = new Context();
        props.put("tfg", new TemplateFormatGenerator());
        context.setVariables(props);
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new TemplateResolver(appConfig.getTemplateLocation()));

        String orderHtml = templateEngine.process(templateName, context);

        return orderHtml;

    }

    public String generateString(Map<String, Object> props, String htmlContent) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        Context scontext = new Context();
        props.put("tfg", new TemplateFormatGenerator());
        scontext.setVariables(props);
        String subject = templateEngine.process(htmlContent, scontext);
        return subject;
    }
}
