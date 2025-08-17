package com.publicis.recipes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class HibernateSearchConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(HibernateSearchConfig.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Hibernate Search is temporarily disabled for compatibility reasons");
    }
}