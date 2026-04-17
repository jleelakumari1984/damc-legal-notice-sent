package com.notices.domain.config.templates;

import org.thymeleaf.templateresolver.FileTemplateResolver;

public class TemplateResolver extends FileTemplateResolver {

    public TemplateResolver(String templateLocation) {
        super();
        setPrefix(templateLocation);
        setSuffix(".html");
        setCharacterEncoding("UTF-8");
        setCacheable(false);
    }
}
