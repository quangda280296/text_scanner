package thent.vietmobi.textscanner.model

import thent.vietmobi.textscanner.constant.Constant


open class ItemPDF() {
    var isPDF: Boolean = false
    var name: String? = null
    var dataKB: String? = null
    var path: String? = null
    var createAt: String? = null
    var nameEndPoint: String? = null
    var selected: Boolean = false
    var password: String? = null
    var isPassword = false

    constructor(
        name: String?, dataKB: String?, path: String?,
        createAt: String?, isPDF: Boolean
    ) : this() {
        this.name = name
        this.dataKB = dataKB
        this.path = path
        this.createAt = createAt
        this.isPDF = isPDF
        nameEndPoint = if (isPDF) {
            name!!.replace(Constant.pdfExtension, "")
        } else {
            name!!.replace(Constant.txtExtension, "")
        }
    }

    constructor(
        name: String?, dataKB: String?, path: String?,
        createAt: String?, isPDF: Boolean, password: String?
    ) : this() {
        this.name = name
        this.dataKB = dataKB
        this.path = path
        this.createAt = createAt
        this.password = password
        this.isPDF = isPDF
        nameEndPoint = if (isPDF) {
            name!!.replace(Constant.pdfExtension, "")
        } else {
            name!!.replace(Constant.txtExtension, "")
        }
        isPassword = true
    }

    operator fun compareTo(other: ItemPDF): Int {
        return name!!.compareTo(other.name!!)
    }
}