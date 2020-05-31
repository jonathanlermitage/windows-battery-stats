package biz.lermitage.batterystats.service.impl

import biz.lermitage.batterystats.conf.LocalAppConf
import biz.lermitage.batterystats.model.BatteryInfo
import biz.lermitage.batterystats.service.BatteryWriter
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@Service
class BatteryWriterImpl(private val conf: LocalAppConf) : BatteryWriter {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val batteryInfoCache: MutableList<BatteryInfo> = ArrayList()
    private val batteryInfoChunk = conf.battery.chunk.toInt()

    override fun writeToFile(batteryInfo: BatteryInfo): Boolean {
        batteryInfoCache.add(batteryInfo)

        if (batteryInfoCache.size >= batteryInfoChunk) {

            val batteryInfoCacheMap: MutableMap<String, MutableList<BatteryInfo>> = HashMap()
            batteryInfoCache.forEach { bi: BatteryInfo ->
                val date = bi.datetime.substring(0, 8)
                if (!batteryInfoCacheMap.containsKey(date)) {
                    batteryInfoCacheMap[date] = ArrayList()
                }
                batteryInfoCacheMap[date]!!.add(bi)
            }

            batteryInfoCacheMap.keys.forEach { date: String ->
                val batteryInfoLines = batteryInfoCacheMap[date]!!.stream()
                    .map { t: BatteryInfo -> "${t.datetime};${t.acLineStatus};${t.batteryLifePercent};${t.batteryLifeTime}" }
                    .collect(Collectors.toList())

                val reportFile = File(conf.battery.reportDir, "${conf.battery.reportSuffix}$date.csv")

                if (logger.isDebugEnabled) {
                    logger.debug("write {} cached logs to file -- {}", batteryInfoLines.size, reportFile.absolutePath)
                }
                try {
                    FileUtils.writeLines(reportFile, batteryInfoLines, true)
                    batteryInfoCache.clear()
                    return true
                } catch (ex: IOException) {
                    if (batteryInfoCache.size > batteryInfoChunk * 5) {
                        batteryInfoCache.clear()
                        logger.error("cannot write report file: ${reportFile.absolutePath} since a while, " +
                            "${batteryInfoCache.size} cached battery info lines cleared", ex)
                    } else {
                        logger.error("cannot write report file: ${reportFile.absolutePath}", ex)
                    }
                }
            }
        }
        return false
    }
}
