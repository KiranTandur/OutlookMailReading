package com.test.users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseDMLOperations {

	public void insertValues(Map<String, String> mapValue, Date mailDate) {
		System.out.println("Inside insertValues()");
		System.out.println("Map Value : " + mapValue);
		System.out.println("User ID : " + mapValue.get("userId"));
		PreparedStatement statement = null;
		Connection connection = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");  
		String stringDate=null;
		  java.sql.Timestamp timeStampDate = null;
		try {
			stringDate = formatter.format(mailDate);
			 Date date = (Date) formatter.parse(stringDate);
		     timeStampDate = new Timestamp(date.getTime());
			System.out.println(stringDate);
		} catch (Exception e) {
			System.out.println("Failed in Parsing String Date");
		}
		java.sql.Date sqlPackageDate = new java.sql.Date(mailDate.getTime());
		System.out.println("Post Parse 2: "+sqlPackageDate);
		System.out.println("Post Parse 3: "+timeStampDate);
		try {
			connection = GetDbConnection.getConnection();
			if (connection != null) {
				System.out.println("Inserting Records into the table");
				String sql = "INSERT IGNORE INTO user_details(subject, user_id, location, ticket_number, time) VALUES (?, ?, ?, ?, ?)";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mapValue.get("subject"));
				statement.setString(2, mapValue.get("userId"));
				statement.setString(3, mapValue.get("location"));
				statement.setString(4, mapValue.get("ticket number"));
				statement.setTimestamp(5, timeStampDate);
				statement.execute();
			} else {
				System.out.println("DB Connetion returning Null.. Please check..");
			}
		} catch (Exception ex) {
			System.out.println("!! Exception in Catch : " + ex.getMessage());
		}
	}

	public void saveUserDetailsToExcel(Connection connection) {
		System.out.println("Inside saveUserDetailsToExcel");
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		// String fileName = new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new
		// Date());
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		try {
			String sql = "select user_id as userId, location as location, ticket_number as ticketNumber, time as dateTime from user_details order by time desc";
			statement = connection.prepareStatement(sql);
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

			customerData.put("1", new Object[] { "User Id", "Location", "Ticket Number", "Time Stamp" });

			System.out.println(resultSet.getFetchSize());
			JSONArray jsonArray = convertResultSetToJsonArray(resultSet);//Json Array
			System.out.println("Json Array : "+ jsonArray);
			
			
			int temp2 = 0;
			String temp = "";
			int k = 2;
			
			for(int i = 0; i < jsonArray.length(); i++) {
				System.out.println("Hi.. Testing here");
				System.out.println("Printing Row No : "+temp);
				System.out.println("Printing Index No : "+temp2);
				temp = "" + k;
				customerData.put(temp, new Object[] { jsonArray.getJSONObject(temp2).get("userid"), jsonArray.getJSONObject(temp2).get("location"),
						jsonArray.getJSONObject(temp2).get("ticketnumber"), jsonArray.getJSONObject(temp2).get("datetime").toString() });
				temp2++;
				k++;
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
					new File("C:/BT144UAT/OBPM_Retro/SIMigrationProject/GFGsheet_" + fileName + ".xlsx"));
			workbook.write(out);
			System.out.println("GFGsheet_"+fileName+".xlsx file has been generated");
			out.close();

			/*System.out.println("Pess Enter Excel Sheet file name to read... :)");
			Scanner scan = new Scanner(System.in);
			String excelFileName = scan.nextLine();
			String excelFilePath = "C:\\BT144UAT\\OBPM_Retro\\SIMigrationProject\\"+excelFileName+".xlsx";*/
			
			String excelFilePath = "C:\\BT144UAT\\OBPM_Retro\\SIMigrationProject\\GFGsheet_"+fileName+".xlsx";
			System.out.println("Excel Sheet FIle Path : "+excelFilePath);
		
			//System.out.println("Pess Y to read excel file... :)");
			//String readExcel = scan.nextLine();
			readExcelSheet(excelFilePath);
		/*	if (null != readExcel && readExcel.equalsIgnoreCase("Y")) {
				readExcelSheet(excelFilePath);
			} else {
				System.out.println("Sorry.... you have not entered Y to read excel sheet...");
			}*/

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

	public static JSONArray convertResultSetToJsonArray(ResultSet resultSet) throws Exception {
		System.out.println("*** Inside convertResultSetToJsonArray ***");
		JSONArray jsonArray = new JSONArray();
		while (resultSet.next()) {
			int columns = resultSet.getMetaData().getColumnCount();
			JSONObject obj = new JSONObject();
			for (int i = 0; i < columns; i++)
				obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));

			jsonArray.put(obj);
		}
		return jsonArray;
	}

}
