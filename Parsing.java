package simpledemo;

import java.io.File;
import java.util.Scanner;

public class Parsing {

	public static void main(String[] args) {
		String FileName = "/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv";
		File file = new File(FileName);

		try {
			Scanner inputStream = new Scanner(file);
			while (inputStream.hasNext()) {
				String data = inputStream.next();
				String[]values=data.split(",");
				System.out.println(values[0]);
			}
		}

		catch (Exception e) {
			e.printStackTrace();

		}
	}
}
