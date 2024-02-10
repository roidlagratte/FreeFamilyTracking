package nonozi.freefamilytracking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitBuilder private constructor() {

    var BASE_URL_POST = "https://nonozi2.ddns.net"
    var BASE_URL_GET = "https://nonozi2.ddns.net"
    private var builder: Retrofit.Builder? = null
    public var apiInterface: ApiInterface? = null

    companion object {
        var instance: RetrofitBuilder? = null
            get() {
                if(field == null) {
                    field = RetrofitBuilder()
                }
                return field
            }
            private set
    }

    fun build(url: String?) {
        builder = Retrofit.Builder()
        builder!!.baseUrl(url.toString())
        builder!!.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder!!.build()
        apiInterface = retrofit.create(ApiInterface::class.java)
    }
    fun callApi(): ApiCalls {
        return ApiCalls(apiInterface!!)
    }
}