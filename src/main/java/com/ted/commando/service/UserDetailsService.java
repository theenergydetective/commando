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

@PropertySource(value = "file:/opt/data/commando.user.properties", ignoreResourceNotFound = true)
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    enum ServerProperties {
        USERNAME(""),
        PASSWORD(""),
        ACTIVATION_KEY(""),
        TIMEZONE("America/New_York"),
        SERVER_NAME("");

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
        if (properties.getProperty(ServerProperties.SERVER_NAME.name()).isEmpty()) {
            String publicIp = getServerPublicIp();
            LOGGER.info("[init] Server Property does not exist. Looking up public ip of server: {}", publicIp);
            properties.setProperty(ServerProperties.SERVER_NAME.name(), publicIp);
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

        String adminUsername = properties.getProperty(ServerProperties.USERNAME.name());
        String adminPassword = properties.getProperty(ServerProperties.PASSWORD.name());

        if (!adminUsername.equals(username) || adminPassword.isEmpty()) {
            LOGGER.warn("[loadUserByUsername] Admin not configured: {}", username);
            throw new UsernameNotFoundException("Admin not configured. Invalid user: " + username);
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

    public void setAdminCredentials(String newUserName, String newPassword, String activationKey, String timeZone) {
        //Set the new username and encrypted password
        if (activationKey ==null || activationKey.trim().isEmpty()) activationKey = "";
        properties.setProperty(ServerProperties.USERNAME.name(), newUserName);
        properties.setProperty(ServerProperties.PASSWORD.name(), bCryptPasswordEncoder.encode(newPassword));
        properties.setProperty(ServerProperties.ACTIVATION_KEY.name(), activationKey);
        properties.setProperty(ServerProperties.TIMEZONE.name(), timeZone);
        writePropertyFile();
    }

    public String getActivationKey() {
        return properties.getProperty(ServerProperties.ACTIVATION_KEY.name());
    }

    public String getTimezone() {
        return properties.getProperty(ServerProperties.TIMEZONE.name());
    }

    public String getServerName() {
        return properties.getProperty(ServerProperties.SERVER_NAME.name());
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
        String password = properties.getProperty(ServerProperties.PASSWORD.name());
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminSetup(password == null || password.trim().isEmpty());
        if (adminRequest.getAdminSetup()) {
            adminRequest.setActivationKey(properties.getProperty(ServerProperties.ACTIVATION_KEY.name()));
            adminRequest.setTimezone(properties.getProperty(ServerProperties.TIMEZONE.name()));
            adminRequest.setUsername(properties.getProperty(ServerProperties.USERNAME.name()));
        }
        return adminRequest;
    }

    public boolean setAdminRequest(AdminRequest adminRequest){
        //Only do it if there is no password set.
        String password = properties.getProperty(ServerProperties.PASSWORD.name());
        if (password == null || password.isEmpty()){
            LOGGER.warn("[setAdminRequest] Updating Server Credentials: {}", adminRequest);
            setAdminCredentials(adminRequest.getUsername(), adminRequest.getPassword(), adminRequest.getActivationKey(), getTimezone());
            return true;
        } else {
            LOGGER.warn("[setAdminRequest] PASSWORD RESET ATTEMPT WHEN PASSWORD ALREADY EXISTS.");
            return false;
        }
    }

}
