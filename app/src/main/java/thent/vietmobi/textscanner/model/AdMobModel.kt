package thent.vietmobi.textscanner.model

class AdMobModel {
    var admob: AdmobBean? = null

    constructor(admob: AdmobBean?) {
        this.admob = admob
    }

    class AdmobBean {
        var banner: String? = null
        var popup: String? = null

        constructor(banner: String?, popup: String?) {
            this.banner = banner
            this.popup = popup
        }
    }
}