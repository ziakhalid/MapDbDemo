package sample.android.zia.khalid.mapdbdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {


	private static final String PREFS_THRESHOLD_VALUE = "location_threshold_value";
	TextView txtLatLong;
	public static final String TAG = "MainActivity";
	private NetworkService networkService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtLatLong = (TextView) findViewById(R.id.txt_lat_long);
		networkService = ApiUtils.getNetworkService();

		Observable.interval(20, 20, TimeUnit.SECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<Long>() {
				@Override
				public void onSubscribe(@NonNull Disposable d) {

				}

				@Override
				public void onNext(@NonNull Long aLong) {
					Toast.makeText(MainActivity.this, "This happnes every mint :)", Toast.LENGTH_SHORT).show();
					Log.e("zia", "This happnes every mint :)");
					sendAllLocationToServer();
				}

				@Override
				public void onError(@NonNull Throwable e) {

				}

				@Override
				public void onComplete() {

				}
			});


		LocationManager locationManager = (LocationManager)
			getSystemService(Context.LOCATION_SERVICE);


		LocationListener locationListener = new MyLocationListener();

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

	private void sendAllLocationToServer() {
//		http://111.118.178.163/amrs_igl_api/webservice.asmx/tracking?imei=32432423&lat=23.2343196868896&lon=76.2342300415039&accuracy=98.34&dir=we

		List<MLocation> locations = getAllLocation();
		if (locations != null && locations.size() > 1) {
			for (int i = 0; i < locations.size(); i++) {

				final MLocation mLocation = locations.get(i);
				Call<List<MResponse>> call = networkService
					.sendLocation("32432423", mLocation.getLatitude(), mLocation.getLongitude(), "98.34", "we");
				call.enqueue(new Callback<List<MResponse>>() {
					@Override
					public void onResponse(Call<List<MResponse>> call, Response<List<MResponse>> response) {
						Log.e("zia", "khalid");
						if(response != null && response.body().size()>0){
							if(response.body().get(0).equals("success")){
								deleteLocation(mLocation);
							}
						}

					}

					@Override
					public void onFailure(Call<List<MResponse>> call, Throwable t) {
						Log.e("zia", "failed");

					}
				});


			}
		}


	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			txtLatLong.setText("");
			Toast.makeText(
				getBaseContext(),
				"MLocation changed: Lat: " + loc.getLatitude() + " Lng: "
					+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
			String longitude = "" + loc.getLongitude();
			Log.v(TAG, longitude);
			String latitude = "" + loc.getLatitude();
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

	private void putInfoToDb(String latitude, String longitude, String address) {

		SQLiteDatabase db = LocationDBHelper.getInstance(MainActivity.this).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(LocationDBHelper.LocationEntry.COLUMN_NAME_LATITUDE, latitude);
		values.put(LocationDBHelper.LocationEntry.COLUMN_NAME_LOGITUDE, longitude);
		values.put(LocationDBHelper.LocationEntry.COLUMN_NAME_ADDRESS, address);

		long newRowId = db.insert(LocationDBHelper.LocationEntry.TABLE_NAME, null, values);
		db.close();

	}

	public List<MLocation> getAllLocation() {

		SQLiteDatabase db = LocationDBHelper.getInstance(MainActivity.this).getWritableDatabase();


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

		db.close();
		// return contact list
		return mLocationsList;
	}

	public void deleteLocation(MLocation location) {

		SQLiteDatabase db = LocationDBHelper.getInstance(MainActivity.this).getWritableDatabase();
		db.delete(LocationDBHelper.LocationEntry.TABLE_NAME, LocationDBHelper.LocationEntry._ID + " = ?",
			new String[] { String.valueOf(location.getId()) });
		db.close();
	}


}
