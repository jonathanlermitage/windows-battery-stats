package biz.lermitage.batterystats.service.impl

import biz.lermitage.batterystats.conf.LocalAppConf
import biz.lermitage.batterystats.service.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BatteryCollectorImpl(private val conf: LocalAppConf,
                           private val batteryReader: BatteryReader,
                           private val batteryWriter: BatteryWriter,
                           private val timeService: TimeService,
                           private val zipCompressor: ZipCompressor) : BatteryCollector {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun collectInfinite() {
        val pauseDuration = conf.battery.pause.seconds * 1_000
        logger.info(conf.toString())
        while (true) {
            val reportWritten = batteryWriter.writeToFile(batteryReader.read())
            if (reportWritten && timeService.rotateDay()) {
                zipCompressor.compressFinalizedReports()
            }
            Thread.sleep(pauseDuration)
        }
    }
}
