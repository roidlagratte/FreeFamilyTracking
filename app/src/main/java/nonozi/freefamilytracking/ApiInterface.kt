package nonozi.freefamilytracking

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @get:GET("/api/index.php")
    val heros: Call<List<GetRequestModel?>?>?

    @POST("/api/index.php")
    fun postHeros(@Body postRequestModel: PostRequestModel?): Call<PostResponseModel?>?
}