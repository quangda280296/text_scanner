package thent.vietmobi.textscanner.callback

interface OnHandlerMessageListener {
    fun onMessage(message: String)

    fun onCancel()
}
