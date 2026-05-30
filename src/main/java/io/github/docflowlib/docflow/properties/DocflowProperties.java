package io.github.docflowlib.docflow.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "docflow")
public class DocflowProperties {

    private String defaultErrorSchema;
    private Security security = new Security();

    private String title;
    private String description;
    private String version = "1.0.0";

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getDefaultErrorSchema() {
        return defaultErrorSchema;
    }
    public void setDefaultErrorSchema(String defaultErrorSchema) {
        this.defaultErrorSchema = defaultErrorSchema;
    }

    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }

    public static class Security {
        private boolean enabled = false;
        private String schemeName = "Bearer Authentication";
        private String bearerFormat = "JWT";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getSchemeName() { return schemeName; }
        public void setSchemeName(String schemeName) { this.schemeName = schemeName; }

        public String getBearerFormat() { return bearerFormat; }
        public void setBearerFormat(String bearerFormat) { this.bearerFormat = bearerFormat; }
    }
}
