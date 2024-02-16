package nonozi.freefamilytracking

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded
import android.util.Log

interface ApiInterface {
    @get:GET("/api/index.php")
    val heros: Call<List<GetRequestModel?>?>?

    @POST("/api/index.php")
    fun postHeros(@Body postRequestModel: PostRequestModel?): Call<PostResponseModel?>?


    @POST("/api/getcoordinates.php")
    fun getCoordinates(@Body postRequestMap: PostRequestMap?): Call<List<PostResponseMap?>?>
}
