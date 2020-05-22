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

import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.MeasuringTransmittingUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

/***
 * Controller used for accessing and manipulating MTU data
 * @author Pete Arvanitis (pete@petecode.com)
 */
@RequestMapping("/api/mtu")
@RestController
public class MeasuringTransmittingUnitController {

    final static Logger LOGGER = LoggerFactory.getLogger(MeasuringTransmittingUnitController.class);

    @Inject
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;


    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    List<MeasuringTransmittingUnit> getMTUs(){
        return measuringTransmittingUnitDAO.findAll();
    }

    @RequestMapping(value="{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    MeasuringTransmittingUnit getMTU(@PathVariable("id") String id){
        return measuringTransmittingUnitDAO.findOne(id);
    }


    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    MeasuringTransmittingUnit setMTU(@RequestBody MeasuringTransmittingUnit mtu){
        measuringTransmittingUnitDAO.updateSettings(mtu);
        return measuringTransmittingUnitDAO.findOne(mtu.getId());
    }

}
