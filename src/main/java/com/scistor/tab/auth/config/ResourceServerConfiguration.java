package com.scistor.tab.auth.config;

import com.scistor.tab.auth.model.Authority;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author Wei Xing
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends
        ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(null);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users").hasAuthority(Authority.ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET, "/users").hasAuthority(Authority.ADMIN.getAuthority())
                .antMatchers("/users").authenticated()
                .antMatchers("/groups").hasAuthority(Authority.ADMIN.getAuthority())
                .anyRequest().authenticated();
    }

}
