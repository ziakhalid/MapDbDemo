package sample.android.zia.khalid.mapdbdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


	TextView txtLatLong;
	public static final String TAG = "MainActivity";
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtLatLong = (TextView) findViewById(R.id.txt_lat_long);

		LocationManager locationManager = (LocationManager)
			getSystemService(Context.LOCATION_SERVICE);

		db = LocationDBHelper.getInstance(MainActivity.this).getWritableDatabase();
		LocationListener locationListener = new MyLocationListener();
//		locationManager.requestLocationUpdates(
//			LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			txtLatLong.setText("");
			Toast.makeText(
				getBaseContext(),
				"MLocation changed: Lat: " + loc.getLatitude() + " Lng: "
					+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
			String longitude = "Longitude: " + loc.getLongitude();
			Log.v(TAG, longitude);
			String latitude = "Latitude: " + loc.getLatitude();
			Log.v(TAG, latitude);

        /*------- To get city name from coordinates -------- */
			String cityName = null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(),
					loc.getLongitude(), 1);
				if (addresses.size() > 0) {
					System.out.println(addresses.get(0).getLocality());
					cityName = addresses.get(0).getLocality();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
				+ cityName;
			txtLatLong.setText(s);
			putInfoToDb(latitude, longitude, cityName);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	private void putInfoToDb(String latitude, String longitude, String address){

		ContentValues values = new ContentValues();
		values.put(LocationDBHelper.LocationEntry.COLUMN_NAME_LATITUDE, latitude);
		values.put(LocationDBHelper.LocationEntry.COLUMN_NAME_LOGITUDE, longitude);
		values.put(LocationDBHelper.LocationEntry.COLUMN_NAME_ADDRESS, address);

		long newRowId = db.insert(LocationDBHelper.LocationEntry.TABLE_NAME, null, values);

	}

	public List<MLocation> getAllLocation() {
		List<MLocation> mLocationsList = new ArrayList<MLocation>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + LocationDBHelper.LocationEntry.TABLE_NAME;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				MLocation mLocation = new MLocation();
				mLocation.setId(Integer.parseInt(cursor.getString(0)));
				mLocation.setLatitude(cursor.getString(1));
				mLocation.setLongitude(cursor.getString(2));
				mLocation.setAddress(cursor.getString(3));
				// Adding contact to list
				mLocationsList.add(mLocation);
			} while (cursor.moveToNext());
		}

		// return contact list
		return mLocationsList;
	}

	public void deleteLocation(Location location) {
		db.delete(LocationDBHelper.LocationEntry.TABLE_NAME, KEY_ID + " = ?",
			new String[] { String.valueOf(contact.getID()) });
		db.close();
	}

}
