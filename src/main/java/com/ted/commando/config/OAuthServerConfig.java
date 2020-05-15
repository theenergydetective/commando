/*
 * Copyright (c) 2020. Energy, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.ted.commando.config;

import com.ted.commando.service.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.inject.Inject;


@Configuration
@EnableAuthorizationServer
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthServerConfig.class);

    @Inject
    private AuthenticationManager authenticationManager;

    private TokenStore memoryTokenStore = new InMemoryTokenStore();

    @Bean
    public TokenStore authTokenStore() {
        return memoryTokenStore;
    }


    @Inject
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws
            Exception {

        AuthenticationManager customAM = new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return authenticationManager.authenticate(authentication);
            }
        };
        endpoints
                .authenticationManager(customAM)
                .userDetailsService(userDetailsService)
                .tokenStore(authTokenStore());
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("web")
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("USER")
                .scopes("api")
                .resourceIds("commandoAPI")
                .secret("{noop}commandoAPI")
                .accessTokenValiditySeconds(24 * 365 * 60 * 60);
    }

}
