package com.demo.service;

import com.demo.Model.CustumerCateg;
import com.demo.Model.ProductCateg;
import com.demo.Model.ResellerCateg;
import com.demo.Model.SalesData;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceReader {


    public List<SalesData> readExcelFile(InputStream inputStream) throws Exception {
             List<SalesData> dataList = new ArrayList<>();
            Workbook workbook = new XSSFWorkbook(inputStream);
             Sheet sheet = workbook.getSheetAt(0);

             for (Row row : sheet) {
             if (row.getRowNum() == 0) continue; // Ignorer l'en-tête
                 SalesData data = new SalesData();

                 data.setReseller(getCellValue(row.getCell(0)));
                 data.setResellerType(getCellValue(row.getCell(1)));
                 data.setSecondReseller(getCellValue(row.getCell(2)));
                 data.setRegion(getCellValue(row.getCell(3)));
                 data.setSubsidiary(getCellValue(row.getCell(4)));
                 data.setEndCustomer(getCellValue(row.getCell(5)));
                 data.setEndCustomerIndustry(getCellValue(row.getCell(6)));
                 data.setProdSubdinary(getCellValue(row.getCell(7)));
                 data.setProdSubdinarySubdinary(getCellValue(row.getCell(8)));
                 data.setLicense(getCellValue(row.getCell(9)));
                 data.setYear(getNumericValue(row.getCell(10)));
                 data.setMonth(getCellValue(row.getCell(11)));
                 data.setRevenue(getNumericValue(row.getCell(12)));
                 data.setLicenceQuantity(getNumericValue(row.getCell(13)));
                 data.setDiscountRate(getNumericValue(row.getCell(14)));
                 data.setBeforeDiscount(getNumericValue(row.getCell(15)));

             dataList.add(data);
         }

         workbook.close();
         return dataList;
    }
    public List<CustumerCateg> readExcelFile2(InputStream inputStream) throws Exception {
        List<CustumerCateg> dataList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Ignorer l'en-tête
            CustumerCateg data = new CustumerCateg();

            data.setName(getCellValue(row.getCell(0)));
            data.setCategory(getCellValue(row.getCell(1)));


            dataList.add(data);
        }

        workbook.close();
        return dataList;
    }
    public List<ResellerCateg> readExcelFileReseller(InputStream inputStream) throws Exception {
        List<ResellerCateg> dataList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Ignorer l'en-tête
            ResellerCateg data = new ResellerCateg();

            data.setResellerTypeName(getCellValue(row.getCell(2)));
            data.setChannel(getCellValue(row.getCell(1)));
            data.setResellerName(getCellValue(row.getCell(0)));


            dataList.add(data);
        }

        workbook.close();
        return dataList;
    }

    public List<ProductCateg> readExcelFileProduct(InputStream inputStream) throws Exception {
        List<ProductCateg> dataList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Ignorer l'en-tête
            ProductCateg data = new ProductCateg();

            data.setProductSubSub(getCellValue(row.getCell(0)));
            data.setProductType(getCellValue(row.getCell(1)));


            dataList.add(data);
        }

        workbook.close();
        return dataList;
    }

    private String getCellValue(Cell cell) {
        return cell == null ? null : cell.toString().trim();
    }

    private BigDecimal getNumericValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        try {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } catch (Exception e) {
            return null;
        }
    }
}
