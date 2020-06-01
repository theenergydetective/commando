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

package com.ted.commando.dao.callbackHandler;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Adds Excel export functionality to a row callback handler.
 */
public abstract class ExcelRowCallbackHandler implements RowCallbackHandler {
    final static Logger LOGGER = LoggerFactory.getLogger(ExcelRowCallbackHandler.class);

    //Workbook
    final SXSSFWorkbook wb;

    //fonts
    final Font dataFont;
    final Font headerFont;

    //styles
    final XSSFCellStyle headerStyle;

    final XSSFCellStyle stringDataStyle;
    final XSSFCellStyle usageDataStyle;
    final XSSFCellStyle costDataStyle;
    final XSSFCellStyle integerDataStyle;

    //Sheets
    final SXSSFSheet sh;

    int columnCount;


    /**
     * Creates the workbook and writes the header fields to the workbook
     * @param headerFields
     */
    public ExcelRowCallbackHandler(String headerFields[]) {

        columnCount = headerFields.length;

        //Intiialize the workbook options
        wb = new SXSSFWorkbook(10); //Only need to keep a few rows in memory to keep the overall footprint low.


        //Create fonts
        dataFont = wb.createFont();
        dataFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

        headerFont = wb.createFont();
        headerFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerFont.setBold(true);

        //Create styles
        headerStyle = (XSSFCellStyle) wb.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(41, 98, 255)));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        setSolidBorder(headerStyle);

        stringDataStyle = (XSSFCellStyle) wb.createCellStyle();
        stringDataStyle.setFont(dataFont);
        setSolidBorder(stringDataStyle);

        usageDataStyle = (XSSFCellStyle) wb.createCellStyle();
        usageDataStyle.setFont(dataFont);
        usageDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        usageDataStyle.setAlignment(HorizontalAlignment.RIGHT);
        usageDataStyle.setDataFormat(wb.createDataFormat().getFormat("0.0"));
        setSolidBorder(usageDataStyle);

        costDataStyle = (XSSFCellStyle) wb.createCellStyle();
        costDataStyle.setFont(dataFont);
        costDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        costDataStyle.setAlignment(HorizontalAlignment.RIGHT);
        costDataStyle.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        setSolidBorder(costDataStyle);

        integerDataStyle = (XSSFCellStyle) wb.createCellStyle();
        integerDataStyle.setFont(dataFont);
        integerDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        integerDataStyle.setAlignment(HorizontalAlignment.RIGHT);
        integerDataStyle.setDataFormat(wb.createDataFormat().getFormat("0"));
        setSolidBorder(integerDataStyle);

        sh = wb.createSheet("Billing");
        sh.trackAllColumnsForAutoSizing();


        //Write the header
        int rowNumber = 0;
        Row row = sh.getRow(rowNumber);
        if (row == null) row = sh.createRow(rowNumber);


        //Write Headers
        int columnNumber = 0;
        for (String header: headerFields){
            Cell cell = row.createCell(columnNumber++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);
        }

   }


   private void setSolidBorder(XSSFCellStyle style){
       style.setBorderBottom(BorderStyle.THIN);
       style.setBorderTop(BorderStyle.THIN);
       style.setBorderLeft(BorderStyle.THIN);
       style.setBorderRight(BorderStyle.THIN);

   }


   public void writeToOutputStream(OutputStream outputStream){
        try{
            for (int c=0; c < columnCount; c++){
                sh.autoSizeColumn(c);
            }

            wb.write(outputStream);
            wb.dispose();
        } catch (Exception e){
            LOGGER.error("[writeToOutputStrema] Exception", e);
        }
   }


    /**
     * Writes a single date value to the sheet
     * @param rowNumber
     * @param columnNumber
     * @param value
     */
    public void writeDateCell(int rowNumber, int columnNumber, Date value) {
        try {
            Row row = sh.getRow(rowNumber);
            if (row == null) row = sh.createRow(rowNumber);
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(value);
            cell.setCellStyle(stringDataStyle);
        } catch (Exception ex){
            LOGGER.error("[writeCell] Exception Caught: sheet:{}, row:{} column:{}, value:{}", sh.getSheetName(), rowNumber, columnNumber, value, ex);
        }
    }


    /**
     * Writes a general string to the cell
     * @param rowNumber
     * @param columnNumber
     * @param value
     */
    public void writeStringCell(int rowNumber, int columnNumber, String value) {
        try {
            Row row = sh.getRow(rowNumber);
            if (row == null) row = sh.createRow(rowNumber);
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(value);
            cell.setCellStyle(stringDataStyle);
        } catch (Exception ex){
            LOGGER.error("[writeCell] Exception Caught: sheet:{}, row:{} column:{}, value:{}", sh.getSheetName(), rowNumber, columnNumber, value, ex);
        }
    }


    /**
     * Writes usage data to a cell
     * @param rowNumber
     * @param columnNumber
     * @param value
     */
    public void writeUsageCell(int rowNumber, int columnNumber, BigDecimal value) {
        try {
            Row row = sh.getRow(rowNumber);
            if (row == null) row = sh.createRow(rowNumber);
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(usageDataStyle);
        } catch (Exception ex){
            LOGGER.error("[writeCell] Exception Caught: sheet:{}, row:{} column:{}, value:{}", sh.getSheetName(), rowNumber, columnNumber, value, ex);
        }
    }

    /**
     * Writes cost data to a cell
     * @param rowNumber
     * @param columnNumber
     * @param value
     */
    public void writeCostCell(int rowNumber, int columnNumber, BigDecimal value) {
        try {
            Row row = sh.getRow(rowNumber);
            if (row == null) row = sh.createRow(rowNumber);
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(costDataStyle);
        } catch (Exception ex){
            LOGGER.error("[writeCell] Exception Caught: sheet:{}, row:{} column:{}, value:{}", sh.getSheetName(), rowNumber, columnNumber, value, ex);
        }
    }

    /**
     * Writes integer data to a cell (no decimal places)
     * @param rowNumber
     * @param columnNumber
     * @param value
     */
    public void writeIntegerCell(int rowNumber, int columnNumber, int value) {
        try {
            Row row = sh.getRow(rowNumber);
            if (row == null) row = sh.createRow(rowNumber);
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(value);
            cell.setCellStyle(integerDataStyle);
        } catch (Exception ex){
            LOGGER.error("[writeCell] Exception Caught: sheet:{}, row:{} column:{}, value:{}", sh.getSheetName(), rowNumber, columnNumber, value, ex);
        }
    }
}

