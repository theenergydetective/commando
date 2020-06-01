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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ted.commando.model.BillingFormParameters;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.service.AuthorizationService;
import com.ted.commando.service.DailyEnergyDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/dailyEnergyData")
@RestController
public class DailyEnergyDataController {
    final static Logger LOGGER = LoggerFactory.getLogger(DailyEnergyDataService.class);

    @Inject
    DailyEnergyDataService dailyEnergyDataService;

    @Inject
    AuthorizationService authorizationService;

    @RequestMapping(value="{mtu}", method = GET)
    public
    @ResponseBody
    List<DailyEnergyData> getRecords(@PathVariable("mtu") String mtu, @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
        return dailyEnergyDataService.findByIdDate(mtu, startDate, endDate);
    }

    @RequestMapping(method = POST)
    public
    @ResponseBody
    DailyEnergyData updateRecord(@RequestBody DailyEnergyData dailyEnergyData) {
        return dailyEnergyDataService.update(dailyEnergyData);
    }



    @RequestMapping(value="export", method = POST)
    public
    @ResponseBody
    void exportData(HttpServletRequest request, HttpServletResponse response) {
        //Get the request object from the parameters
        ObjectMapper objectMapper = new ObjectMapper();
        BillingFormParameters billingFormParameters = parseFormParameters(request);
        if (billingFormParameters != null){
            //Validate the token
            if (authorizationService.isAuthorized(billingFormParameters.getAccessToken())){

                //Setup the filename and response headers for download
                StringBuilder fileName = new StringBuilder("Export-")
                        .append(System.currentTimeMillis())
                        .append(".xlsx");


                try {
                    LOGGER.debug("[exportData] Setting response headers. Filename: {}", fileName);
                    response.setHeader("Content-Description", "File Transfer");
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Transfer-Encoding", "binary");
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName.toString() + "\"");
                    response.setStatus(200);

                    switch (billingFormParameters.getExportType()){
                        case DAY:{
                            dailyEnergyDataService.writeDailyData(billingFormParameters, response.getOutputStream());
                            break;
                        }
                        default:
                            dailyEnergyDataService.writeBillingCycleData(billingFormParameters, response.getOutputStream());
                    }

                    response.flushBuffer();
                    return;
                } catch (IOException ioex){
                    LOGGER.error("[exportData] IO Exception Caught: ", ioex);
                }

            }
        }

        //Send an error if we failed out somewhere
        try {
            response.sendError(401);
        } catch (IOException e) {
            LOGGER.error("[exportData] Error returning response code", e);
        }

    }



    /**
     * Parses the BillingFormParameters from the http post.
     * @param request
     * @return
     */
    protected BillingFormParameters parseFormParameters(HttpServletRequest request){
        //Get the request object from the parameters
        ObjectMapper objectMapper = new ObjectMapper();
        BillingFormParameters billingFormParameters = null;

        try {
            String jsonData = request.getParameter("formData");
            LOGGER.debug("[parseFormParameters] JSONDATA: {}", jsonData);
            billingFormParameters = objectMapper.readValue(jsonData, BillingFormParameters.class);
            LOGGER.debug("[parseFormParameters] Parsed Parameters: {}", billingFormParameters);
        } catch (JsonProcessingException e) {
            LOGGER.error("[exportData] Error parsing JSON", e);
        }
        return billingFormParameters;

    }


}
