package com.example.Controller;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class ExcelConverter {

    public static void convertToExcel(TableView<?> tableView, String filePath) {
        if (filePath != null) {
            try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(filePath)) {
                Sheet sheet = workbook.createSheet("Лист1");
                ObservableList<?> items = tableView.getItems();

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tableView.getColumns().size(); i++) {
                    TableColumn<?, ?> column = tableView.getColumns().get(i);
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(column.getText());
                }

                for (int i = 0; i < items.size(); i++) {
                    Object item = items.get(i);
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < tableView.getColumns().size(); j++) {
                        row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString());
                    }
                }

                for (int i = 0; i < tableView.getColumns().size(); i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(fileOut);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void convertToExcelForSupply(TableView<?> tableView, String filePath, LocalDate date, String supplier, String itemNumber, String responsiblePerson) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Отчет");

            // Запись информации о приемке товара
            Row infoRow1 = sheet.createRow(0);
            Cell infoCell1 = infoRow1.createCell(0);
            infoCell1.setCellValue("Дата:");
            Cell infoCell2 = infoRow1.createCell(1);
            infoCell2.setCellValue(date.toString());

            Row infoRow2 = sheet.createRow(1);
            Cell infoCell3 = infoRow2.createCell(0);
            infoCell3.setCellValue("Поставщик:");
            Cell infoCell4 = infoRow2.createCell(1);
            infoCell4.setCellValue(supplier);

            Row infoRow3 = sheet.createRow(2);
            Cell infoCell5 = infoRow3.createCell(0);
            infoCell5.setCellValue("Номер накладной:");
            Cell infoCell6 = infoRow3.createCell(1);
            infoCell6.setCellValue(itemNumber);

            Row infoRow4 = sheet.createRow(3);
            Cell infoCell7 = infoRow4.createCell(0);
            infoCell7.setCellValue("Ответственный за приемку:");
            Cell infoCell8 = infoRow4.createCell(1);
            infoCell8.setCellValue(responsiblePerson);


            ObservableList<?> items = tableView.getItems();

            // Создание заголовков столбцов
            Row headerRow = sheet.createRow(5);
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                TableColumn<?, ?> column = tableView.getColumns().get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(column.getText());
            }

            // Заполнение данными
            for (int i = 0; i < items.size(); i++) {
                Row row = sheet.createRow(i + 6);
                for (int j = 0; j < tableView.getColumns().size(); j++) {
                    row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString());
                }
            }

            // Автоматическое изменение ширины столбцов для лучшего отображения
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertToExcelForDispatch(TableView<?> tableView, String filePath, LocalDate date, String customer, String itemNumber, String responsiblePerson) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Отчет");

            // Запись информации о приемке товара
            Row infoRow1 = sheet.createRow(0);
            Cell infoCell1 = infoRow1.createCell(0);
            infoCell1.setCellValue("Дата:");
            Cell infoCell2 = infoRow1.createCell(1);
            infoCell2.setCellValue(date.toString());

            Row infoRow2 = sheet.createRow(1);
            Cell infoCell3 = infoRow2.createCell(0);
            infoCell3.setCellValue("Покупатель:");
            Cell infoCell4 = infoRow2.createCell(1);
            infoCell4.setCellValue(customer);

            Row infoRow3 = sheet.createRow(2);
            Cell infoCell5 = infoRow3.createCell(0);
            infoCell5.setCellValue("Номер накладной:");
            Cell infoCell6 = infoRow3.createCell(1);
            infoCell6.setCellValue(itemNumber);

            Row infoRow4 = sheet.createRow(3);
            Cell infoCell7 = infoRow4.createCell(0);
            infoCell7.setCellValue("Ответственный за приемку:");
            Cell infoCell8 = infoRow4.createCell(1);
            infoCell8.setCellValue(responsiblePerson);


            ObservableList<?> items = tableView.getItems();

            // Создание заголовков столбцов
            Row headerRow = sheet.createRow(5);
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                TableColumn<?, ?> column = tableView.getColumns().get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(column.getText());
            }

            // Заполнение данными
            for (int i = 0; i < items.size(); i++) {
                Row row = sheet.createRow(i + 6);
                for (int j = 0; j < tableView.getColumns().size(); j++) {
                    row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString());
                }
            }

            // Автоматическое изменение ширины столбцов для лучшего отображения
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void convertToExcelForMovement(TableView<?> tableView, String filePath, LocalDate date, String itemNumber, String responsiblePerson) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Отчет");

            // Запись информации о приемке товара
            Row infoRow1 = sheet.createRow(0);
            Cell infoCell1 = infoRow1.createCell(0);
            infoCell1.setCellValue("Дата:");
            Cell infoCell2 = infoRow1.createCell(1);
            infoCell2.setCellValue(date.toString());

            Row infoRow3 = sheet.createRow(1);
            Cell infoCell5 = infoRow3.createCell(0);
            infoCell5.setCellValue("Номер накладной:");
            Cell infoCell6 = infoRow3.createCell(1);
            infoCell6.setCellValue(itemNumber);

            Row infoRow4 = sheet.createRow(2);
            Cell infoCell7 = infoRow4.createCell(0);
            infoCell7.setCellValue("Ответственный за приемку:");
            Cell infoCell8 = infoRow4.createCell(1);
            infoCell8.setCellValue(responsiblePerson);


            ObservableList<?> items = tableView.getItems();

            // Создание заголовков столбцов
            Row headerRow = sheet.createRow(4);
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                TableColumn<?, ?> column = tableView.getColumns().get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(column.getText());
            }

            // Заполнение данными
            for (int i = 0; i < items.size(); i++) {
                Row row = sheet.createRow(i + 5);
                for (int j = 0; j < tableView.getColumns().size(); j++) {
                    row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString());
                }
            }

            // Автоматическое изменение ширины столбцов для лучшего отображения
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}