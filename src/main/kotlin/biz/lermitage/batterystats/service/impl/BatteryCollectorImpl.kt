package biz.lermitage.batterystats.service.impl

import biz.lermitage.batterystats.conf.LocalAppConf
import biz.lermitage.batterystats.service.BatteryCollector
import biz.lermitage.batterystats.service.BatteryReader
import biz.lermitage.batterystats.service.BatteryWriter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BatteryCollectorImpl(private val conf: LocalAppConf,
                           private val batteryReader: BatteryReader,
                           private val batteryWriter: BatteryWriter) : BatteryCollector {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun collectInfinite() {
        val pauseDuration = conf.battery.pause.seconds * 1_000
        logger.info(conf.toString())
        while (true) {
            batteryWriter.writeToFile(batteryReader.read())
            Thread.sleep(pauseDuration)
        }
    }
}
