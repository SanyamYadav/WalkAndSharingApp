package simpledemo;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Scanner;
import java.util.*;
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.util.Scanner;
import au.com.bytecode.opencsv.CSVReader;

public class Matching {
	public static void main(String[] args) {

		Scanner scanner;
		scanner = new Scanner(System.in).useDelimiter("\\n");
		Connection c = null;
		Statement stmt = null;

		try {

			c = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false", "root",
					"Fatehjiom.!7");

			System.out.println("Name: ");
			String user = scanner.nextLine();

			System.out.println("Starting Postcode: ");
			String start_postcode = scanner.nextLine();

			System.out.println("Ending Postcode: ");
			String end_postcode = scanner.nextLine();
			System.out.println("Date_and_time: ");
			String dateAndTime = scanner.nextLine();

			// String one = "AB101AA";
			// String two = "AB419QJ";
			double x = 0;
			double y = 0;
			double x2 = 0;
			double y2 = 0;

			CSVReader reader = null;
			try {
				reader = new CSVReader(
						new FileReader("/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv"), ',', '"',
						1);

				// Read CSV line by line and use the string array as you want
				String[] nextLine;

				PreparedStatement stat = c.prepareStatement("SELECT * from main");
				ResultSet results = stat.executeQuery();

				while (results.next()) {
					String db_postcode_start = results.getString("start_point");
					System.out.println(db_postcode_start + "    yyyyyy ");
					while ((nextLine = reader.readNext()) != null) {
						// if (results != null) {
						if (db_postcode_start.matches(nextLine[0])) {
							Double p = Double.parseDouble(nextLine[1]);
							// System.out.println(x);
							Double t = Double.parseDouble(nextLine[2]);
							System.out.println("Start Match Latitude" + p);

							System.out.println("End Match Longitude " + t);
							System.out.println("start_postcode " + start_postcode);
							System.out.println("db_postcode_start " + db_postcode_start);

							if ((start_postcode.substring(0, 2).equals(db_postcode_start.substring(0, 2)))&&((nextLine = reader.readNext()) != null) &&(start_postcode.matches(nextLine[0])) ) {
								//|| (start_postcode.matches(nextLine[0]))

								Double p2 = Double.parseDouble(nextLine[1]);
								// System.out.println(x);
								Double t2 = Double.parseDouble(nextLine[2]);
								System.out.println(" Second Start Match Latitude" + p2);
								System.out.println("Second End Match Longitude " + t2);

								// Double p2 = Double.parseDouble(nextLine[1]);
								// System.out.println(x);
								// Double t2 = Double.parseDouble(nextLine[2]);

								// System.out.println(" Second Start Match Latitude" + p2);

								// System.out.println("Second End Match Longitude " + t2);

								double g = Math.sqrt(((p - t) * (p - t)) + ((p - t) * (p - t)));
								System.out.println("The shortest distance" + g);

							}

						}
					}
				}
			}

			catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			/*
			 * reader = new CSVReader(new FileReader(
			 * "/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv"), ',',
			 * '"', 1); // String db_postcode_start = results.getString("start_point"); //
			 * System.out.println(db_postcode_start + " yyyyyy "); while ((nextLine =
			 * reader.readNext()) != null) { // if (results != null) { if
			 * (start_postcode.matches(nextLine[0])) { Double p =
			 * Double.parseDouble(nextLine[1]); // System.out.println(x); Double t =
			 * Double.parseDouble(nextLine[2]);
			 * System.out.println(" Second Start Match Latitude" + p);
			 * 
			 * System.out.println("Second End Match Longitude " + t);
			 * 
			 * 
			 * double h=Math.sqrt((p-t)*(p-t)+(p-t)*(p-t));
			 * 
			 * }
			 */
			// Algorithms

			System.out.println(x);
			System.out.println(y);
			System.out.println(x2);
			System.out.println(y2);

			java.util.Date date = null;

			// java.util.Date utilStartDate = jDateChooserStart.getDate();
			// java.sql.Date sqlStartDate = new java.sql.Date(utilStartDate.getTime());

			Date sqlStartDate = null;
			Date sqlStartTime = null;

			// String s = "03/24/2013 21:54";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
			try {
				date = simpleDateFormat.parse(dateAndTime);
				sqlStartDate = new java.sql.Date(date.getTime());
				// sqlStartTime = new java.sql.Time(date.getTime());

				System.out.println("date : " + simpleDateFormat.format(date));
			} catch (ParseException ex) {
				System.out.println("Exception " + ex);
			}

			System.out.println(user);
			System.out.println(start_postcode);
			System.out.println(end_postcode);
			System.out.println(sqlStartDate);

			String a = "INSERT into main (user, start_point,end_point, date, time) VALUES (?, ?, ?, ?, ?);";
			PreparedStatement ps = c.prepareStatement(a);
			ps.setString(1, user);
			ps.setString(2, start_postcode);
			ps.setString(3, end_postcode);
			ps.setDate(4, sqlStartDate);
			ps.setDate(5, sqlStartTime);
			ps.execute();

		}

		catch (

		Exception e9) {
			e9.printStackTrace();
		}

	}

}
