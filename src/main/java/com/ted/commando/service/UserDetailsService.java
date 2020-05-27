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

import com.ted.commando.model.ActivationDetails;
import com.ted.commando.model.AdminRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/***
 * This is used by the OAuth server to authenticate users. At this time, the server only supports a single
 * 'Admin' user that is configured on setup.
 *
 * The username and BCrypted password is stored in a property file on the server.
 *
 * @author Pete Arvanitis (pete@petecode.com)
 */

@PropertySource(value = "file:/opt/commando/data/commando.user.properties", ignoreResourceNotFound = true)
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {


    enum ServerProperties {
        COMMANDO_USERNAME("admin"),
        COMMANDO_PASSWORD(""),
        COMMANDO_ACTIVATION_KEY(""),
        COMMANDO_TIMEZONE("America/New_York"),
        COMMANDO_DOMAIN("");

        final String defaultValue;

        ServerProperties(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsService.class);
    static final String PROP_PATH = "/opt/data/commando.user.properties";


    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Inject
    public Environment env;

    private Properties properties = new Properties();

    @PostConstruct
    void init() {
        LOGGER.debug("[init] Loading property file");
        for (ServerProperties property : ServerProperties.values()) {
            loadProperty(property);
        }

        //If the server name does not exist. Try to look up the ip address of the server.
        if (properties.getProperty(ServerProperties.COMMANDO_DOMAIN.name()).isEmpty()) {
            String publicIp = getServerPublicIp();
            LOGGER.info("[init] Server Property does not exist. Looking up public ip of server: {}", publicIp);
            properties.setProperty(ServerProperties.COMMANDO_DOMAIN.name(), publicIp);
            writePropertyFile();
        }

    }

    //Forces default values even if the env file exists
    private void loadProperty(ServerProperties property) {
        String value = env.getProperty(property.name());
        LOGGER.debug("[loadProperty] Loaded:{} value:{}", property.name(), value);
        if (value == null || value.isEmpty()) value = property.defaultValue;
        properties.put(property.name(), value);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.trace("[loadUserByUsername]]: {}", username);

        String adminUsername = properties.getProperty(ServerProperties.COMMANDO_USERNAME.name());
        String adminPassword = properties.getProperty(ServerProperties.COMMANDO_PASSWORD.name());

        if (username == null || !adminUsername.toLowerCase().equals(username.toLowerCase()) || adminPassword.isEmpty()) {
            if (adminPassword.isEmpty()){
                LOGGER.warn("[loadUserByUsername] ***ADMIN NOT CONFIGURED***");
            }
            LOGGER.warn("[loadUserByUsername] Invalid User: {}", username);
            throw new UsernameNotFoundException("Invalid user: " + username);
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_BASIC"));

        org.springframework.security.core.userdetails.User authUser
                = new org.springframework.security.core.userdetails.User(adminUsername, adminPassword, authorities);

        LOGGER.trace("[loadUserByUsername] LOADED USER: {}", authUser);
        return authUser;
    }


    private void writePropertyFile() {

        LOGGER.info("[writePropertyFile] Updating Properties file. {}", PROP_PATH);

        try (FileOutputStream fileOutputStream = new FileOutputStream(PROP_PATH)) {
            properties.store(fileOutputStream, null);
        } catch (Exception ex) {
            LOGGER.error("[updateAdminCredentials] Error writing {}", PROP_PATH, ex);
        }
    }

    public void setAdminCredentials(AdminRequest adminRequest) {
        if (adminRequest != null) {
            //Set the new username and encrypted password
            properties.setProperty(ServerProperties.COMMANDO_USERNAME.name(), adminRequest.getUsername().trim().toLowerCase());
            properties.setProperty(ServerProperties.COMMANDO_PASSWORD.name(), bCryptPasswordEncoder.encode(adminRequest.getPassword()));
            properties.setProperty(ServerProperties.COMMANDO_ACTIVATION_KEY.name(), adminRequest.getActivationKey());
            properties.setProperty(ServerProperties.COMMANDO_TIMEZONE.name(), adminRequest.getTimezone());
            properties.setProperty(ServerProperties.COMMANDO_DOMAIN.name(), adminRequest.getDomain());
            writePropertyFile();
        }
    }

    public String getActivationKey() {
        return properties.getProperty(ServerProperties.COMMANDO_ACTIVATION_KEY.name());
    }

    public String getTimezone() {
        return properties.getProperty(ServerProperties.COMMANDO_TIMEZONE.name());
    }

    public String getServerName() {
        return properties.getProperty(ServerProperties.COMMANDO_DOMAIN.name());
    }

    public String getServerPublicIp() {
        //Try public ip
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String publicIp = in.readLine(); //you get the IP as a String
            publicIp = publicIp.trim();
            return publicIp;
        } catch (Exception ex) {
            LOGGER.error("Error getting public IP", ex);
            return "";
        }
    }

    public AdminRequest getAdminRequestRequired(){
        String password = properties.getProperty(ServerProperties.COMMANDO_PASSWORD.name());
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminSetup(password == null || password.trim().isEmpty());

        if (adminRequest.getAdminSetup()) {
            adminRequest.setDomain(properties.getProperty(ServerProperties.COMMANDO_DOMAIN.name()));
            adminRequest.setActivationKey(properties.getProperty(ServerProperties.COMMANDO_ACTIVATION_KEY.name()));
            adminRequest.setTimezone(properties.getProperty(ServerProperties.COMMANDO_TIMEZONE.name()));
            adminRequest.setUsername(properties.getProperty(ServerProperties.COMMANDO_USERNAME.name()));
        }
        return adminRequest;
    }

    public boolean setAdminRequest(AdminRequest adminRequest){
        //Only do it if there is no password set.
        String password = properties.getProperty(ServerProperties.COMMANDO_PASSWORD.name());
        if (password == null || password.isEmpty()){
            LOGGER.warn("[setAdminRequest] Updating Server Credentials: {}", adminRequest);
            setAdminCredentials(adminRequest);
            return true;
        } else {
            LOGGER.warn("[setAdminRequest] PASSWORD RESET ATTEMPT WHEN PASSWORD ALREADY EXISTS.");
            return false;
        }
    }

    public ActivationDetails getActivationDetails() {
        ActivationDetails activationDetails = new ActivationDetails();
        activationDetails.setDomain(properties.getProperty(ServerProperties.COMMANDO_DOMAIN.name()));
        activationDetails.setActivationKey(properties.getProperty(ServerProperties.COMMANDO_ACTIVATION_KEY.name()));
        activationDetails.setTimezone(properties.getProperty(ServerProperties.COMMANDO_TIMEZONE.name()));
        return activationDetails;
    }

    public void adminReset(){
        LOGGER.warn("[adminReset] The ADMIN settings are being reset.");
        properties.setProperty(ServerProperties.COMMANDO_PASSWORD.name(), "");
        writePropertyFile();
    }

}
