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

package com.ted.commando.controller;

import com.ted.commando.model.AdminRequest;
import com.ted.commando.service.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.TimeZone;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @Inject
    UserDetailsService userDetailsService;

    @RequestMapping(method = GET)
    public
    @ResponseBody
    AdminRequest getAdminRequest() {
        return userDetailsService.getAdminRequestRequired();
    }

    @RequestMapping(method = POST)
    public
    @ResponseBody
    AdminRequest setAdminRequest(@RequestBody AdminRequest request, HttpServletResponse response) throws Exception {
        if (userDetailsService.setAdminRequest(request)) {
            return getAdminRequest();
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }


    @RequestMapping(value = "tz", method = GET)
    public
    @ResponseBody
    String[] getTimeZones() {
        return TimeZone.getAvailableIDs();
    }


    @RequestMapping(value = "reset", method = GET)
    public
    @ResponseBody
    String resetAdminSettings() {
        userDetailsService.adminReset();
        return "RESET";
    }


}
