package com.test.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UtilityClass {

	public void getDetailsBySubject(Connection connection, String subject) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String sql = "select email_id from travel_details where subject = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, subject);
			resultSet = statement.executeQuery();
			// if (resultSet.next()) {
			while (resultSet.next()) {
				System.out.println("Printing Value : " + resultSet.getString("email_id"));
			}
			// }else {
			// System.out.println("!! No Emails available for thie Subject !!");
			// System.exit(0);
			// }
			System.out.println("PLease enter email id to fetch the details based on the email availability");
			Scanner scan = new Scanner(System.in);
			String email = scan.nextLine();
			if (email != null) {
				getDetailsByEmail(connection, email);
			}

		} catch (Exception ex) {

		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("<!! ERROR in CATCH - X !!>");
				e.printStackTrace();
			}
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e1) {
					System.out.println("<!! ERROR in CATCH - Y !!>");
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e2) {
					System.out.println("<!! ERROR in CATCH - Z !!>");
				}
		}
	}

	public void getDetailsByEmail(Connection connection, String email) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String sql = "select user_id as userId, location as location, ticket_number as ticketNumber from travel_details where email_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, email);
			resultSet = statement.executeQuery();

			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			// workbook object
			XSSFWorkbook workbook = new XSSFWorkbook();

			// spreadsheet object
			XSSFSheet spreadsheet = workbook.createSheet(" Student Data ");

			// creating a row object
			XSSFRow row;

			// This data needs to be written (Object[])
			Map<String, Object[]> customerData = new TreeMap<String, Object[]>();

			customerData.put("1", new Object[] { "User Id", "Location", "Ticket Number" });

			// if(resultSet.next()) {
			while (resultSet.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) {
						// System.out.print(", ");
					}
					String columnValue = resultSet.getString(i);
					System.out.println(rsmd.getColumnName(i) + " : " + columnValue);
					// System.out.println( resultSet.getString(1));
					// System.out.println( resultSet.getString(2));
					// System.out.println( resultSet.getString(3));
					customerData.put("2",
							new Object[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) });
				}
			}

			Set<String> keyid = customerData.keySet();
			int rowid = 0;

			// writing the data into the sheets...
			for (String key : keyid) {
				row = spreadsheet.createRow(rowid++);
				Object[] objectArr = customerData.get(key);
				int cellid = 0;

				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
					cell.setCellValue((String) obj);
				}
			}

			// .xlsx is the format for Excel Sheets...
			// writing the workbook into the file...
			FileOutputStream out = new FileOutputStream(
					new File("C:/BT144UAT/OBPM_Retro/SIMigrationProject/GFGsheet.xlsx"));
			workbook.write(out);
			System.out.println("GFGsheet.xlsx file has been generated");
			out.close();

			System.out.println("Pess Y to read Excel sheet and display on console... :)");
			Scanner scan = new Scanner(System.in);
			String readExcel = scan.nextLine();
			String filePath = "C:\\BT144UAT\\OBPM_Retro\\SIMigrationProject\\GFGsheet.xlsx";
			if (null != readExcel && readExcel.equalsIgnoreCase("Y")) {
				readExcelSheet(filePath);
			} else {
				System.out.println("Sorry.... you have not entered Y to read excel sheet...");
			}

		} catch (Exception ex) {
			System.out.println("!! EXCEPTION in CATCH : " + ex.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("<!! ERROR in CATCH - X !!>");
				e.printStackTrace();
			}
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e1) {
					System.out.println("<!! ERROR in CATCH - Y !!>");
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e2) {
					System.out.println("<!! ERROR in CATCH - Z !!>");
				}
		}
	}

	public void readExcelSheet(String filePath) {
		System.out.println();
		try {
			File file = new File(filePath); // creating a new file instance
			FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
			// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING: // field that represents string cell type
						System.out.print(cell.getStringCellValue() + "\t\t\t");
						break;
					case Cell.CELL_TYPE_NUMERIC: // field that represents number cell type
						System.out.print(cell.getNumericCellValue() + "\t\t\t");
						break;
					default:
					}
				}
				System.out.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
