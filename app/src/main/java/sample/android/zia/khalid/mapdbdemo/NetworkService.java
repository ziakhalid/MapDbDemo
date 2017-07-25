package sample.android.zia.khalid.mapdbdemo;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkService {

//	http://111.118.178.163/amrs_igl_api/webservice.asmx/tracking?imei=32432423&lat=23.2343196868896&lon=76.2342300415039&accuracy=98.34&dir=we

	//	@GET("tracking/")
	@GET("tracking")
	Call<List<MResponse>> sendLocation(
		@Query("imei") String imei,
		@Query("lat") String lat,
		@Query("lon") String lon,
		@Query("accuracy") String accuracy,
		@Query("dir") String dir
	);

}
