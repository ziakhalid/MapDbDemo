package sample.android.zia.khalid.mapdbdemo;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class LocationDBHelper extends SQLiteOpenHelper{

	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "MLocation.db";
	private static LocationDBHelper locationDBHelper;

	public static final String SQL_CREATE_ENTRIES =
		"CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
			LocationEntry._ID + " INTEGER PRIMARY KEY," +
			LocationEntry.COLUMN_NAME_LATITUDE + " TEXT," +
			LocationEntry.COLUMN_NAME_LOGITUDE + " TEXT," +
			LocationEntry.COLUMN_NAME_ADDRESS + " TEXT)";

	public static final String SQL_DELETE_ENTRIES =
		"DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;


	public static class LocationEntry implements BaseColumns {
		public static final String TABLE_NAME = "location";
		public static final String COLUMN_NAME_LATITUDE = "latitude";
		public static final String COLUMN_NAME_LOGITUDE = "longitude";
		public static final String COLUMN_NAME_ADDRESS = "address";
	}

	public static LocationDBHelper getInstance(Context context){
		if(locationDBHelper == null){
			locationDBHelper = new LocationDBHelper(context);
		}
		return locationDBHelper;
	}

	public LocationDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
