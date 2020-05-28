package biz.lermitage.batterystats.service

import biz.lermitage.batterystats.model.BatteryInfo

interface BatteryReader {

    fun read(): BatteryInfo
}
