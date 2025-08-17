package com.publicis.recipes.config;

/*
 * Hibernate Search Configuration - Temporarily disabled for compatibility
 * This will be re-enabled once we resolve the Jakarta EE vs Java EE compatibility issues
 */

/*
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.transaction.annotation.Transactional;

// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;

/**
 * Configuration class for Hibernate Search.
 *
 * This class handles the initialization and configuration of Hibernate Search
 * with Lucene backend for full-text search capabilities.
 *
 * Features:
 * - Automatic index creation on application startup
 * - Mass indexing for existing data
 * - Lucene backend configuration
 *
 * @author Recipe Management Team
 * @version 1.0.0
 */
// @Configuration - Temporarily disabled
public class HibernateSearchConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(HibernateSearchConfig.class);

    // @PersistenceContext
    // private EntityManager entityManager;

    /**
     * Initializes Hibernate Search indexes when the application is ready.
     * This method is called automatically after the Spring context is fully initialized.
     *
     * @param event the application ready event
     */
    @Override
    // @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Hibernate Search temporarily disabled
        logger.info("Hibernate Search is temporarily disabled for compatibility reasons");
    }
}