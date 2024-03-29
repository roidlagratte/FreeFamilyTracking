package nonozi.freefamilytracking

import android.util.Log
import retrofit2.Call

class ApiCalls(private val apiInterface: ApiInterface) {
    val heros: Call<List<GetRequestModel?>?>?
        get() = apiInterface.heros

    fun postHeros(postRequestModel: PostRequestModel?): Call<PostResponseModel?>? {
        return apiInterface.postHeros(postRequestModel)
    }

    fun getCoordinates(postRequestMap: PostRequestMap?): Call<List<PostResponseMap?>?> {
        return apiInterface.getCoordinates(postRequestMap)
    }



}