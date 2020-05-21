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

import com.ted.commando.model.EnergyPost;
import com.ted.commando.service.EnergyPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/***
 * Controller that implements the endpoint for dealing with new ECC/MTU3K Activations
 * @author Pete Arvanitis (pete@petecode.com)
 */
@RequestMapping("/api/postData")
@RestController
public class PostDataController {

    final static Logger LOGGER = LoggerFactory.getLogger(PostDataController.class);

    @Inject
    EnergyPostService energyPostService;


    /**
     * Receive and process a single energy post from an ECC or MTU
     *
     * @param energyPost
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/postData",
            consumes = "application/xml",
            method = RequestMethod.POST)
    public void postData(@RequestBody EnergyPost energyPost, HttpServletResponse response) throws Exception {

        try (PrintWriter out = response.getWriter()) {
            LOGGER.info("[postData] Processing Request {}", energyPost);

            if (energyPost == null || energyPost.getGateway() == null) {
                sendFinalResponse(response, 554, "NO DATA", out);
                return;
            }

            //Check the security key
            if (!energyPostService.validateECC(energyPost)) {
                sendFinalResponse(response, HttpServletResponse.SC_FORBIDDEN, "NOT ACTIVATED", out);
                return;
            }

            energyPostService.processEnergyPost(energyPost);

            sendFinalResponse(response, 200, "SUCCESS", out);

        } catch (Exception ex) {
            LOGGER.error("[postData] Critical Exception", ex);
            response.sendError(500);
        }
    }

    private void sendFinalResponse(HttpServletResponse response, int code, String message, PrintWriter out) {
        //response.setContentType("text/xml");
        response.setStatus(code);
        if (out != null) {
            out.write(message);
            out.flush();
            out.close();
        }
    }

}
