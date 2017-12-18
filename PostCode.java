package simpledemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class PostCode extends Coordinate {

	private static ArrayList<PostCode> postcodes = null;

	private String postcode;

	public PostCode(double lat, double lon) {
		super(lat, lon);
	}

	public PostCode(String postcode, double lat, double lon) {
		super(lat, lon);
		this.postcode = postcode;
	}

	public String getPostcode() {
		return postcode;
	}

	@Override
	public String toString() {
		return postcode + "\t" + super.toString();
	}

	public static ArrayList<PostCode> getPostcodes() {
		if (postcodes == null) {
			postcodes = new ArrayList<>();
			System.out.println("Postcodes Loading");
			try {
				// BufferedReader reader = new BufferedReader(new FileReader(new
				// File("/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv")));
				BufferedReader reader = new BufferedReader(new FileReader(new File("./postcodeData/eh.csv")));

				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] data = line.split(",");
					PostCode pc = new PostCode(data[0], Double.parseDouble(data[1]), Double.parseDouble(data[2]));
					postcodes.add(pc);
				}
				reader.close();
			} catch (FileNotFoundException e) {

			} catch (IOException e) {

			}

		}
		System.out.println("" + postcodes.size() + " Postcodes Loaded");
		return postcodes;
	}

	public static void main(String[] args) {
		System.out.println(getPostcodes().size());
	}
}
