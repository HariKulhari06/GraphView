package com.hari.graphlibrary

import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class BarChartView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), ChartInterface {
    private var spaceBetweenBar = DEFAULT_BAR_SPACE
    private var barWidth = DEFAULT_BAR_SPACE
    private var maxValue = DEFAULT_MAX_VALUE
    private var barFillAnimation = true

    private var isBarAdded = false

    private lateinit var parentLayout: LinearLayout
    private var barClickListener: OnBarClickListener? = null


    private val barChartModels = mutableListOf<BarChatData>()


    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.BarChartView, 0, 0)

        spaceBetweenBar = typedArray.getDimensionPixelSize(
            R.styleable.BarChartView_spaceBetweenBar,
            DEFAULT_BAR_SPACE
        )

        barWidth = typedArray.getDimensionPixelSize(
            R.styleable.BarChartView_barWidth,
            DEFAULT_BAR_WIDTH
        )

        maxValue = typedArray.getInteger(
            R.styleable.BarChartView_barMaxValue,
            DEFAULT_MAX_VALUE
        )

        barFillAnimation = typedArray.getBoolean(R.styleable.BarChartView_barFillAnimation, true)

        typedArray.recycle()
        setUp()
    }

    private fun setUp() {
        parentLayout = LinearLayout(context)
        parentLayout.orientation = LinearLayout.HORIZONTAL
        parentLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        parentLayout.gravity = Gravity.BOTTOM
        if (barFillAnimation) {
            parentLayout.layoutTransition = LayoutTransition()
        }
        this.addView(parentLayout)
    }

    override fun setData(data: List<BarChatData>) {
        data.forEach { barChatData ->
            setData(barChartModels.size, barChatData)
        }
    }

    override fun setData(position: Int, data: BarChatData) {
        barChartModels.add(position, data)
        if (parentLayout.height == 0) {
            getDimension(true, parentLayout) { dimenstion ->
                createBarChart(position, dimenstion, data)
            }
        } else {
            createBarChart(position, parentLayout.height, data)
        }
    }

    override fun setListener(listener: OnBarClickListener) {
        barClickListener = listener
    }

    private fun createBarChart(position: Int, dimenstion: Int, data: BarChatData) {
        val view = LayoutInflater.from(context).inflate(R.layout.bar_vertical, parentLayout, false)
        updateUi(position, dimenstion, null, data, view)
    }

    private fun updateUi(
        position: Int,
        dimension: Int,
        initData: BarChatData?,
        data: BarChatData,
        view: View
    ) {
        val layoutParamsBar = view.layoutParams as MarginLayoutParams

        val dimensionBar: Int = dimension * data.barValue / maxValue

        val layout = view.findViewById<LinearLayout>(R.id.linear_bar)
        val anim = ValueAnimator.ofInt(
            if (initData == null) 0 else dimension * initData.barValue / maxValue,
            dimensionBar
        )

        anim.addUpdateListener { valueAnimator ->
            val height = valueAnimator.animatedValue as Int
            val layoutParams = layout.layoutParams
            layoutParams.height = height
            layout.layoutParams = layoutParams
        }

        if (barFillAnimation) {
            anim.duration = 500
        } else {
            anim.duration = 0
        }
        anim.start()

        view.setOnClickListener {
            barClickListener?.onClickBar(data)
        }

        view.layoutParams.width = barWidth

        if(initData ==null){
            if (isBarAdded) {
                layoutParamsBar.leftMargin = spaceBetweenBar
            }
            if (position == -1) {
                parentLayout.addView(view)
            } else if (position <= parentLayout.childCount) {
                parentLayout.addView(view, position)
            }
        }
        isBarAdded = true
    }

    private fun getDimension(heightRequested: Boolean, view: View, listener: (Int) -> Unit) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (heightRequested) {
                    listener(view.height)
                } else {
                    listener(view.width)
                }
            }
        })
    }


    companion object {
        private const val DEFAULT_BAR_SPACE = 20
        private const val DEFAULT_BAR_WIDTH = 40
        private const val DEFAULT_MAX_VALUE = 100
    }

    interface OnBarClickListener {
        fun onClickBar(data: BarChatData)
    }

}