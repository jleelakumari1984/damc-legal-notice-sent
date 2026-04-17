package com.notices.domain.config.templates;

public class StringTemplateResolver extends org.thymeleaf.templateresolver.StringTemplateResolver {

    public StringTemplateResolver() {
        super();
        setCacheable(false);
    }
}
