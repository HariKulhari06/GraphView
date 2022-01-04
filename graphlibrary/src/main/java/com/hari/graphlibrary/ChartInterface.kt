package com.hari.graphlibrary


interface ChartInterface {
    fun setData(data: List<BarChatData>)
    fun setData(position: Int, data: BarChatData)
    fun setListener(listener: BarChartView.OnBarClickListener)
}