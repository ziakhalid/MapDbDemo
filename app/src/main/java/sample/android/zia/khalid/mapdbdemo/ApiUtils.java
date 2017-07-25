package sample.android.zia.khalid.mapdbdemo;


public class ApiUtils {

	public static final String BASE_URL = "http://111.118.178.163/amrs_igl_api/webservice.asmx/";

	public static NetworkService getNetworkService() {
		return RetrofitClient.getClient(BASE_URL).create(NetworkService.class);
	}
}
