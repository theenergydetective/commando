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

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.inject.Inject;


@Configuration
public class OAuthResourceConfig extends ResourceServerConfigurerAdapter {

    @Inject
    TokenStore authTokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("commandoAPI").tokenStore(authTokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/version").permitAll()
                .antMatchers(HttpMethod.GET, "/api/admin").permitAll()
                .antMatchers(HttpMethod.GET, "/api/admin/tz").permitAll()
                .antMatchers(HttpMethod.POST, "/api/admin").permitAll()
                .antMatchers(HttpMethod.POST, "/api/activate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/postData").permitAll()

                //Lock down everything else
                .antMatchers("/api/**").access("#oauth2.hasScope('api')");

    }
}
