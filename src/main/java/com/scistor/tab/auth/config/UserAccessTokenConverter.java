package com.scistor.tab.auth.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

/**
 * @author Wei Xing
 */
public class UserAccessTokenConverter extends DefaultAccessTokenConverter {
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map response = super.convertAccessToken(token, authentication);
        response.put("principal", authentication.getPrincipal());
        return response;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Object principal = map.get("principal");
        map.remove("principal");
        OAuth2Authentication authentication = super.extractAuthentication(map);

        return new OAuth2Authentication(authentication.getOAuth2Request(),
                new UsernamePasswordAuthenticationToken(principal, "N/A", authentication.getAuthorities()));
    }
}
