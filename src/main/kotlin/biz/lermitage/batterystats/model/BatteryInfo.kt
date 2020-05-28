package biz.lermitage.batterystats.model

data class BatteryInfo(
    val datetime: String,
    val acLineStatus: String,
    val batteryLifePercent: String,
    val batteryLifeTime: String)
