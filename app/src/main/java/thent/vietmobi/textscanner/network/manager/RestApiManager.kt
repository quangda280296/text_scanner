package thent.vietmobi.textscanner.network.manager


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import thent.vietmobi.textscanner.constant.Config
import thent.vietmobi.textscanner.network.callback.AdsCallBack

class RestApiManager private constructor() {

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(Config.TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(Config.TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(Config.TIME_OUT.toLong(), TimeUnit.MILLISECONDS)

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }.addNetworkInterceptor(logging)

        val client = httpClient.build()
        mRetrofit = Retrofit.Builder()
            .baseUrl(Config.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    companion object {

        private var mRestApiManager: RestApiManager? = null

        private lateinit var mRetrofit: Retrofit

        val instance: RestApiManager
            get() {
                if (mRestApiManager == null) {
                    mRestApiManager =
                        RestApiManager()
                }
                return mRestApiManager as RestApiManager
            }
    }

    internal fun getAdsManager(): AdsCallBack {
        return mRetrofit.create(AdsCallBack::class.java)
    }
}
