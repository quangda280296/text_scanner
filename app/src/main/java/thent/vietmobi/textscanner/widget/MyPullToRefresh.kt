package thent.vietmobi.textscanner.widget

import `in`.srain.cube.views.ptr.*
import `in`.srain.cube.views.ptr.indicator.PtrIndicator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import thent.vietmobi.textscanner.R
import android.widget.RelativeLayout
import com.pnikosis.materialishprogress.ProgressWheel
import kotlin.math.min

class MyPullToRefresh : PtrClassicFrameLayout {
    private var onRefreshBegin: OnRefreshBegin? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context?, attrs: AttributeSet?, defStyle: Int
    ) : super(context, attrs, defStyle)

    fun setOnRefreshBegin(
        view: View?, headerView: PullToRefreshHeader?, onRefreshBegin: OnRefreshBegin
    ) {
        this.onRefreshBegin = onRefreshBegin
        if (headerView != null) {
            this.headerView = headerView
            this.addPtrUIHandler(headerView)
        } else {
            val defaultHeader = PtrClassicDefaultHeader(context)
            this.headerView = defaultHeader
            this.addPtrUIHandler(defaultHeader)
        }
        this.setPtrHandler(object : PtrHandler {
            override fun checkCanDoRefresh(
                frame: PtrFrameLayout, content: View?, header: View?
            ): Boolean {
                return try {
                    (headerView!!.currentPosY < 1.5 * frame.headerHeight && PtrDefaultHandler.checkContentCanBePulledDown(
                        frame, view, header
                    ))
                } catch (e: Exception) {
                    true
                }
            }

            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                onRefreshBegin.refresh()
            }
        })
    }

    fun refresh() {}
    interface OnRefreshBegin {
        fun refresh()
    }

    class PullToRefreshHeader : RelativeLayout, PtrUIHandler {
        private var progressWheel: ProgressWheel? = null
        private var layoutParams: LayoutParams? = null
        var currentPosY = 0
            private set

        constructor(context: Context?) : super(context) {
            initViews()
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
            initViews()
        }

        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
                : super(context, attrs, defStyleAttr) {
            initViews()
        }

        private fun initViews() {
            val header: View =
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this)
            progressWheel = header.findViewById(R.id.progress_wheel)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setLayoutParams(layoutParams)
        }

        override fun onUIReset(frame: PtrFrameLayout?) {
            progressWheel!!.visibility = View.VISIBLE
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setLayoutParams(layoutParams)
        }

        override fun onUIRefreshPrepare(frame: PtrFrameLayout?) {}
        override fun onUIRefreshBegin(frame: PtrFrameLayout?) {
            progressWheel!!.spin()
        }

        override fun onUIRefreshComplete(frame: PtrFrameLayout?) {
            progressWheel!!.visibility = View.GONE
        }

        override fun onUIPositionChange(
            frame: PtrFrameLayout?, isUnderTouch: Boolean, status: Byte, ptrIndicator: PtrIndicator
        ) {
            currentPosY = ptrIndicator.currentPosY
            val percent = min(1f, ptrIndicator.currentPercent)
            if (status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                progressWheel!!.progress = percent
            }
            if (status == PtrFrameLayout.PTR_STATUS_PREPARE || status == PtrFrameLayout.PTR_STATUS_LOADING) {
                if (ptrIndicator.currentPercent > 1f) {
                    layoutParams?.bottomMargin =
                        ptrIndicator.currentPosY - ptrIndicator.headerHeight
                    layoutParams?.topMargin = ptrIndicator.headerHeight - ptrIndicator.currentPosY
                    setLayoutParams(layoutParams)
                }
            }
            invalidate()
        }
    }
}
