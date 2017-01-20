package org.eientei.discord;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Alexander Tumin on 2016-11-14
 */
@ConfigurationProperties(prefix = "neetoree")
public class NeetoreeProperties {
    @NotEmpty
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
