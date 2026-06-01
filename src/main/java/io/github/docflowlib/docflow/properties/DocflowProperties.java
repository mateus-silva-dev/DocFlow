package io.github.docflowlib.docflow.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for DocFlow automation engine.
 *
 * <p>This class binds configuration keys prefixed with {@code docflow}
 * from application properties or YAML files into Java fields.</p>
 *
 * @author Mateus Silva
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "docflow")
public class DocflowProperties {

    /** Fully qualified name of the default global error schema class. */
    private String defaultErrorSchema;

    /** Security configuration parameters for smart OpenAPI inference. */
    private Security security = new Security();

    /** Global title for the generated OpenAPI documentation. */
    private String title;

    /** Global description for the generated OpenAPI documentation. */
    private String description;

    /** Global version for the generated OpenAPI documentation. */
    private String version = "1.0.0";

    /**
     * Gets the global documentation title.
     *
     * @return the documentation title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the global documentation title.
     *
     * @param title the documentation title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the global documentation description.
     *
     * @return the documentation description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the global documentation description.
     *
     * @param description the documentation description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the global documentation version.
     *
     * @return the documentation version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the global documentation version.
     *
     * @param version the documentation version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the fully qualified class name of the default global error schema.
     *
     * @return the default error schema class name
     */
    public String getDefaultErrorSchema() {
        return defaultErrorSchema;
    }

    /**
     * Sets the fully qualified class name of the default global error schema.
     *
     * @param defaultErrorSchema the default error schema class name to set
     */
    public void setDefaultErrorSchema(String defaultErrorSchema) {
        this.defaultErrorSchema = defaultErrorSchema;
    }

    /**
     * Gets the nested security configuration object.
     *
     * @return the security settings configuration
     */
    public Security getSecurity() { return security; }

    /**
     * Sets the nested security configuration object.
     *
     * @param security the security settings configuration to set
     */
    public void setSecurity(Security security) { this.security = security; }

    /**
     * Nested security properties for configuring OpenAPI authentication contracts.
     */
    public static class Security {

        /** Flag indicating whether global security schema definitions are enabled. */
        private boolean enabled = false;

        /** Name of the OpenAPI security schema definition. */
        private String schemeName = "Bearer Authentication";

        /** Format of the security token type. */
        private String bearerFormat = "JWT";

        /**
         * Checks whether the security module integration is enabled.
         *
         * @return {@code true} if security module is enabled, {@code false} otherwise
         */
        public boolean isEnabled() { return enabled; }

        /**
         * Sets whether the security module integration should be enabled.
         *
         * @param enabled {@code true} to enable security features, {@code false} to disable
         */
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        /**
         * Gets the configured security scheme name for the Swagger interface.
         *
         * @return the security scheme name
         */
        public String getSchemeName() { return schemeName; }

        /**
         * Sets the security scheme name for the Swagger interface.
         *
         * @param schemeName the security scheme name to set
         */
        public void setSchemeName(String schemeName) { this.schemeName = schemeName; }

        /**
         * Gets the security token format type.
         *
         * @return the token bearer format
         */
        public String getBearerFormat() { return bearerFormat; }

        /**
         * Sets the security token format type.
         *
         * @param bearerFormat the token bearer format to set
         */
        public void setBearerFormat(String bearerFormat) { this.bearerFormat = bearerFormat; }
    }
}
