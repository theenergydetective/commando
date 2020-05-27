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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostDataControllerTest {

    @Mock
    EnergyPostService energyPostService;

    @InjectMocks
    PostDataController postDataController;

    @Before
    public void setup() {
        reset(energyPostService);
    }

    @Test
    public void PostDataTest() throws Exception{
        EnergyPost energyPost = new EnergyPost();
        energyPost.setGateway("TESTECC");
        energyPost.setSecurityKey("123456");
        energyPost.setMtuList(new ArrayList<>());
        energyPost.setSpyderList(new ArrayList<>());

        for (int i=0; i < 4; i++){
            EnergyMTUPost mtuPost = new EnergyMTUPost();
            mtuPost.setCumulativePostList(new ArrayList<>());
            energyPost.getMtuList().add(mtuPost);
            mtuPost.setMtuSerial("TESTMTU" + i);
            mtuPost.setMtuTypeOrdinal(0);
            for (int h=0; h < 15; h++){
                EnergyCumulativePost cumulativePost = new EnergyCumulativePost();
                mtuPost.getCumulativePostList().add(cumulativePost);
                cumulativePost.setPowerFactor(1.0);
                cumulativePost.setTimestamp(1589515200L);
                cumulativePost.setVoltage(120.0);
                cumulativePost.setWatts(1000.0 + (1000.0 * h));
            }
        }

        for (int i=0; i < 32; i++){
            EnergyMTUPost mtuPost = new EnergyMTUPost();
            mtuPost.setCumulativePostList(new ArrayList<>());
            energyPost.getSpyderList().add(mtuPost);
            mtuPost.setMtuSerial("TESTMTU" + i);
            mtuPost.setMtuTypeOrdinal(0);
            for (int h=0; h < 15; h++){
                EnergyCumulativePost cumulativePost = new EnergyCumulativePost();
                mtuPost.getCumulativePostList().add(cumulativePost);
                cumulativePost.setPowerFactor(1.0);
                cumulativePost.setTimestamp(1589515200L);
                cumulativePost.setVoltage(120.0);
                cumulativePost.setWatts(1000.0 + (1000.0 * h));
            }
        }

        //Test bad ecc
        when(energyPostService.validateECC(any())).thenReturn(false);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(servletResponse.getWriter()).thenReturn(printWriter);

        postDataController.postData(null, servletResponse);
        verify(servletResponse).setStatus(554);

        reset(servletResponse);
        postDataController.postData(energyPost, servletResponse);
        verify(servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);

        //Validate good ECC
        when(energyPostService.validateECC(any())).thenReturn(true);
        postDataController.postData(energyPost, servletResponse);
        verify(servletResponse).setStatus(200);
        verify(energyPostService).processEnergyPost(energyPost);

        //Verify processing exception
        doThrow(new RuntimeException()).when(energyPostService).processEnergyPost(any());
        postDataController.postData(energyPost, servletResponse);
        verify(servletResponse).sendError(500);
    }

}
