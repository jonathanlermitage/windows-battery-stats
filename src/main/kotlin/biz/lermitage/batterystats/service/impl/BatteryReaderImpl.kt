package biz.lermitage.batterystats.service.impl

import biz.lermitage.batterystats.core.BatteryUtils
import biz.lermitage.batterystats.core.Kernel32
import biz.lermitage.batterystats.model.BatteryInfo
import biz.lermitage.batterystats.service.BatteryReader
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class BatteryReaderImpl : BatteryReader {

    private val datetimeFormat = SimpleDateFormat("yyyyMMdd HHmmss")

    override fun read(): BatteryInfo {
        // on AC and battery but not charging yet or full already: [Online, 100%, ]
        // on AC and battery charging: [Online, 90%, ]
        // not on AC, just before unplug: [Offline, 99%, ]
        // not on AC since a while: [Offline, 88%, 12:45:12]
        val batteryStatus = BatteryUtils.readWindowsBatteryStatus(
            Kernel32.FIELD_ACLINESTATUS,
            Kernel32.FIELD_BATTERYLIFEPERCENT,
            Kernel32.FIELD_BATTERYLIFETIME)

        val datetimeFormatted = datetimeFormat.format(Date())

        return BatteryInfo(datetimeFormatted, batteryStatus[0], batteryStatus[1], batteryStatus[2])
    }
}
