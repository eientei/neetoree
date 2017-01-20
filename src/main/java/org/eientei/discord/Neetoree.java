package org.eientei.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.security.auth.login.LoginException;

/**
 * Created by Alexander Tumin on 2016-11-14
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableSpringDataWebSupport
@EnableJpaRepositories(basePackageClasses = Message.class)
@EnableConfigurationProperties(NeetoreeProperties.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
public class Neetoree {
    private final NeetoreeProperties properties;
    private final MessageAdminRepository adminRepository;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Neetoree.class, args);
    }

    @Autowired
    public Neetoree(NeetoreeProperties properties, MessageAdminRepository adminRepository, PageableHandlerMethodArgumentResolver resolver) {
        this.properties = properties;
        this.adminRepository = adminRepository;
        resolver.setMaxPageSize(10000);
    }

    @Bean
    public JDA jda() throws LoginException, RateLimitedException {
        JDA jda = new JDABuilder(AccountType.BOT).setToken(properties.getToken()).buildAsync();
        jda.addEventListener(new MessagePersister(adminRepository, jda));
        jda.addEventListener(new CommandDispatcher(jda));
        return jda;
    }

    @Bean
    public WebSecurityConfigurerAdapter securityConfigurerAdapter() {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http
                        .httpBasic().disable()
                        .csrf().disable();
            }
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
