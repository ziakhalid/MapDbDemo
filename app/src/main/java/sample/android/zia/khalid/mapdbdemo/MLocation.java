package sample.android.zia.khalid.mapdbdemo;


public class MLocation {

	private int id;
	private String latitude;
	private String longitude;
	private String address;

	public MLocation() {
	}

	public MLocation(int id, String latitude, String longitude, String address) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}

	public MLocation(String latitude, String longitude, String address) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
