package thent.vietmobi.textscanner.network.callback

import io.reactivex.Observable
import retrofit2.http.GET
import thent.vietmobi.textscanner.model.AdMobModel

interface AdsCallBack {

    @GET("text-scanner.php")
    fun getAds(): Observable<AdMobModel>
}