package simpledemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;
import javax.swing.JTextField;
import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReadProc;
import java.util.Arrays;
import au.com.bytecode.opencsv.CSVReader;

public class ParseCSV {
	private JTextField t = new JTextField(20);

	public static void main(String[] args) {

		String one = "AB101AA";
		String two = "AB419QJ";
		double x = 0;
		double y = 0;
		double x2 = 0;
		double y2 = 0;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader("/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv"),
					',', '"', 1);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Read CSV line by line and use the string array as you want
		String[] nextLine;
		try {
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					// Verifying the read data here
					// System.out.println(Arrays.toString(nextLine));
					// System.out.println(nextLine[0]);
					if (nextLine[0].matches(one)) {

						x = Double.parseDouble(nextLine[1]);
						// System.out.println(x);
						y = Double.parseDouble(nextLine[2]);
					}

					if (nextLine[0].matches(two)) {

						x2 = Double.parseDouble(nextLine[1]);
						y2 = Double.parseDouble(nextLine[2]);

					}

				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(x);
		System.out.println(y);
		System.out.println(x2);
		System.out.println(y2);

		// String FileName =
		// "/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv";
		// //String FileName2 =
		// "/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv";
		// File file = new File(FileName);
		// //File file2 = new File(FileName2);
		//
		// csv.read(file, new CSVReadProc() {
		// public void procRow(int rowIndex, String... values) {
		// System.out.println(rowIndex + "# " + Arrays.asList(values));
		// try {
		// Scanner inputStream = new Scanner(file);
		// String data = inputStream.next();
		// String[] valuess = data.split(",");
		//
		// System.out.println(valuess[1]);
		//
		//
		//
		// inputStream.close();
		//
		// }
		//
		//
		//
		// catch(Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });

		/*
		 * String one = "AB101AA"; String two = "AB101BW"; double x = 0; double y = 0;
		 * double x2 = 0; double y2 = 0;
		 * 
		 * try { Scanner inputStream = new Scanner(file);
		 * 
		 * while (inputStream.hasNext()) { String data = inputStream.next(); String[]
		 * values = data.split(","); System.out.println(values[0]);
		 * 
		 * 
		 * 
		 * int n = 100; for (int i = 0; i <= n; i++) { // System.out.println(i);
		 * 
		 * String a = values[0];
		 * 
		 * if (a == one) {
		 * 
		 * String b = values[i + 1]; String c = values[i + 2];
		 * 
		 * x = Long.parseLong(b); y = Long.parseLong(c);
		 * 
		 * System.out.println("First longitude is" + x);
		 * System.out.println("First latitude is" + y); } } }
		 */

		/*
		 * Scanner inputStream2 = new Scanner(file2);
		 * 
		 * // while (inputStream2.hasNext()) { String data2 = inputStream.next();
		 * String[] values2 = data2.split(","); int n2 = 100;
		 * 
		 * for (int i2 = 0; i2 <= n2; i2++) {
		 * 
		 * String b = values2[0];
		 * 
		 * if (b == two) { String b2 = values2[i2 + 1]; String c2 = values2[i2 + 2];
		 * System.out.println("Second longitude" + b2);
		 * System.out.println("Second latitude" + c2);
		 * 
		 * x2 = Long.parseLong(b2); y2 = Long.parseLong(c2);
		 * 
		 * } }
		 */
		// inputStream.close();
		/*
		 * double x3 = x - x2; double y3 = y - y2;
		 * 
		 * double j = Math.sqrt((x3 * x3) + (y3 * y3));
		 * 
		 * System.out.println(j);
		 */

	}

}