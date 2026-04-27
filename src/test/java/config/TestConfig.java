package config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:config.properties")
public interface TestConfig extends Config {
    String baseUrl();

}
