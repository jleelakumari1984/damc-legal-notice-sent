package com.damc.legalnotices.config.templates;

public class StringTemplateResolver extends org.thymeleaf.templateresolver.StringTemplateResolver {

    public StringTemplateResolver() {
        super();
        setCacheable(false);
    }
}
