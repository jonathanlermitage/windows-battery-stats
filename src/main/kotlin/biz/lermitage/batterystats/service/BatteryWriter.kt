package biz.lermitage.batterystats.service

import biz.lermitage.batterystats.model.BatteryInfo

interface BatteryWriter {

    fun writeToFile(batteryInfo: BatteryInfo): Boolean
}
