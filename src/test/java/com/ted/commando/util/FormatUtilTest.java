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

package com.ted.commando.util;

import com.ted.commando.controller.PostDataController;
import com.ted.commando.model.EnergyCumulativePost;
import com.ted.commando.model.EnergyMTUPost;
import com.ted.commando.model.EnergyPost;
import com.ted.commando.service.EnergyPostService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormatUtilTest {


    @Test
    public void parseStringDateTest(){

        String date = "2020-02-28";
        TimeZone NYTZ = TimeZone.getTimeZone("America/New_York");
        TimeZone LATZ = TimeZone.getTimeZone("America/Las_Angeles");

        assertEquals(1582866000L, FormatUtil.parseStringDate(date, NYTZ).longValue());
        assertEquals(1582848000L, FormatUtil.parseStringDate(date, LATZ).longValue());

    }

}