package biz.lermitage.batterystats.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
@ConfigurationProperties("localapp")
class LocalAppConf {

    val battery = Battery()

    class Battery {
        lateinit var chunk: Integer
        lateinit var pause: Duration
        lateinit var reportDir: String
        lateinit var reportSuffix: String

        override fun toString(): String {
            return "Battery(chunk=$chunk, pause=$pause, reportDir='$reportDir', reportSuffix='$reportSuffix')"
        }
    }

    override fun toString(): String {
        return "LocalAppConf(battery=$battery)"
    }
}
