package com.demo.service.Channel;

import com.demo.Model.Channel.CustumerCateg;
import com.demo.Model.Channel.ProductCateg;
import com.demo.Model.Channel.ResellerCateg;
import com.demo.Model.Channel.SalesData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceReader {

    private static final CharsetEncoder WIN1252_ENCODER =
            Charset.forName("windows-1252").newEncoder();

    public List<SalesData> readExcelFile(InputStream inputStream) throws Exception {
        List<SalesData> dataList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;

                try {
                    if (row.getRowNum() % 1000 == 0) {
                        System.out.println("Lecture ligne Excel : " + row.getRowNum());
                    }

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

                    if (isEmptySalesData(data)) {
                        System.out.println("Ligne vide ignorée : " + (row.getRowNum() + 1));
                        continue;
                    }

                    dataList.add(data);

                } catch (Exception e) {
                    System.out.println("Ligne ignorée à cause d'une erreur : " + (row.getRowNum() + 1));
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }

        System.out.println("Nombre total de lignes valides lues : " + dataList.size());
        return dataList;
    }

    public List<CustumerCateg> readExcelFile2(InputStream inputStream) throws Exception {
        List<CustumerCateg> dataList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;

                try {
                    CustumerCateg data = new CustumerCateg();

                    data.setName(getCellValue(row.getCell(0)));
                    data.setCategory(getCellValue(row.getCell(1)));

                    if (data.getName() == null && data.getCategory() == null) {
                        continue;
                    }

                    dataList.add(data);

                } catch (Exception e) {
                    System.out.println("Ligne CustumerCateg ignorée : " + (row.getRowNum() + 1));
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }

        return dataList;
    }

    public List<ResellerCateg> readExcelFileReseller(InputStream inputStream) throws Exception {
        List<ResellerCateg> dataList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;

                try {
                    ResellerCateg data = new ResellerCateg();

                    data.setResellerName(getCellValue(row.getCell(0)));
                    data.setChannel(getCellValue(row.getCell(1)));
                    data.setResellerTypeName(getCellValue(row.getCell(2)));

                    if (data.getResellerName() == null && data.getChannel() == null && data.getResellerTypeName() == null) {
                        continue;
                    }

                    dataList.add(data);

                } catch (Exception e) {
                    System.out.println("Ligne ResellerCateg ignorée : " + (row.getRowNum() + 1));
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }

        return dataList;
    }

    public List<ProductCateg> readExcelFileProduct(InputStream inputStream) throws Exception {
        List<ProductCateg> dataList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;

                try {
                    ProductCateg data = new ProductCateg();

                    data.setProductSubSub(getCellValue(row.getCell(0)));
                    data.setProductType(getCellValue(row.getCell(1)));

                    if (data.getProductSubSub() == null && data.getProductType() == null) {
                        continue;
                    }

                    dataList.add(data);

                } catch (Exception e) {
                    System.out.println("Ligne ProductCateg ignorée : " + (row.getRowNum() + 1));
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }

        return dataList;
    }

    private String getCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        String value;

        try {
            switch (cell.getCellType()) {
                case STRING:
                    value = cell.getStringCellValue();
                    break;

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = cell.getLocalDateTimeCellValue().toString();
                    } else {
                        value = BigDecimal.valueOf(cell.getNumericCellValue())
                                .stripTrailingZeros()
                                .toPlainString();
                    }
                    break;

                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;

                case FORMULA:
                    value = getFormulaValue(cell);
                    break;

                default:
                    value = cell.toString();
            }
        } catch (Exception e) {
            value = cell.toString();
        }

        value = cleanText(value);

        return value == null || value.isEmpty() ? null : value;
    }

    private String getFormulaValue(Cell cell) {
        try {
            return cell.getStringCellValue();
        } catch (Exception ignored) {
        }

        try {
            return BigDecimal.valueOf(cell.getNumericCellValue())
                    .stripTrailingZeros()
                    .toPlainString();
        } catch (Exception ignored) {
        }

        try {
            return String.valueOf(cell.getBooleanCellValue());
        } catch (Exception ignored) {
        }

        return cell.toString();
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return "";
        }

        try {
            value = Normalizer.normalize(value, Normalizer.Form.NFKC);
        } catch (Exception ignored) {
        }

        StringBuilder cleaned = new StringBuilder();

        for (int i = 0; i < value.length(); ) {
            int codePoint = value.codePointAt(i);
            String character = new String(Character.toChars(codePoint));

            if (canEncodeWin1252(character)) {
                cleaned.append(character);
            } else {
                cleaned.append(" ");
            }

            i += Character.charCount(codePoint);
        }

        return cleaned.toString()
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean canEncodeWin1252(String value) {
        try {
            return WIN1252_ENCODER.canEncode(value);
        } catch (Exception e) {
            return false;
        }
    }

    private BigDecimal getNumericValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            }

            if (cell.getCellType() == CellType.FORMULA) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            }

            if (cell.getCellType() == CellType.STRING) {
                String value = cleanText(cell.getStringCellValue());

                if (value == null || value.isEmpty()) {
                    return null;
                }

                value = value
                        .replace(" ", "")
                        .replace(",", ".");

                return new BigDecimal(value);
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private boolean isEmptySalesData(SalesData data) {
        return data.getReseller() == null
                && data.getResellerType() == null
                && data.getSecondReseller() == null
                && data.getRegion() == null
                && data.getSubsidiary() == null
                && data.getEndCustomer() == null
                && data.getEndCustomerIndustry() == null
                && data.getProdSubdinary() == null
                && data.getProdSubdinarySubdinary() == null
                && data.getLicense() == null
                && data.getYear() == null
                && data.getMonth() == null
                && data.getRevenue() == null
                && data.getLicenceQuantity() == null
                && data.getDiscountRate() == null
                && data.getBeforeDiscount() == null;
    }
}