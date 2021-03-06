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

package com.ted.commando.service;

import com.ted.commando.model.AdminRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.ted.commando.service.UserDetailsService.PROP_PATH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {

    @Mock
    Environment env;

    @InjectMocks
    UserDetailsService userDetailsService;

    @Before
    public void setup(){
        reset(env);
        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_USERNAME.name())).thenReturn("ADMIN");
        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_PASSWORD.name())).thenReturn("PASSWORD");
        userDetailsService.init();
    }

    @Test
    public void testValidUser() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("ADMIN");
        assertNotNull(userDetails);
    }

    @Test
    public void testBadUser() {
        try{
            userDetailsService.loadUserByUsername("testUser");
            Assert.fail();
        } catch (UsernameNotFoundException ex){
        }

    }

    @Test
    public void updateAdminCredentialsTest(){

        AdminRequest  adminRequest = new AdminRequest();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("password");
        adminRequest.setDomain("domain");
        adminRequest.setTimezone("America/New_York");
        adminRequest.setActivationKey("testKey");

        userDetailsService.setAdminCredentials(adminRequest);

        //Load Property file.
        Properties properties = new Properties();

        try (InputStream in = new FileInputStream(PROP_PATH)){
            properties.load(in);
        } catch (Exception ex){
            fail();
        }

        assertEquals("testKey", userDetailsService.getActivationKey());
        assertEquals("America/New_York", userDetailsService.getTimezone());
        assertFalse(userDetailsService.getServerName().isEmpty());
    }

    @Test
    public void getPublicIpTest(){
        assertFalse(userDetailsService.getServerPublicIp().isEmpty());
    }


    @Test
    public void getAdminRequestRequiredTest(){
        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_PASSWORD.name())).thenReturn(null);
        userDetailsService.init(); //Force reload of properties
        assertTrue(userDetailsService.getAdminRequestRequired().getAdminSetup());

        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_PASSWORD.name())).thenReturn("FAKEPASS");
        userDetailsService.init(); //Force reload of properties
        assertFalse(userDetailsService.getAdminRequestRequired().getAdminSetup());
    }

    @Test
    public void setAdminRequestTest(){

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setPassword("TESTPASS");
        adminRequest.setUsername("TESTUSER");
        adminRequest.setActivationKey(null);
        adminRequest.setTimezone("America/New_York");

        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_PASSWORD.name())).thenReturn(null);
        userDetailsService.init(); //Force reload of properties
        assertTrue(userDetailsService.setAdminRequest(adminRequest));

        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_PASSWORD.name())).thenReturn("TESTPASS");
        userDetailsService.init(); //Force reload of properties
        assertFalse(userDetailsService.setAdminRequest(adminRequest));
    }

    @Test
    public void getActivationDetailsTest(){
        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_DOMAIN.name())).thenReturn("test.com");
        userDetailsService.init();
        assertEquals("test.com", userDetailsService.getActivationDetails().getDomain());
    }

    @Test
    public void adminResetTest(){
        when(env.getProperty(UserDetailsService.ServerProperties.COMMANDO_PASSWORD.name())).thenReturn("TESTPASSWORD");
        userDetailsService.init();
        userDetailsService.adminReset();
        assertTrue(userDetailsService.getAdminRequestRequired().getAdminSetup());
    }
}
