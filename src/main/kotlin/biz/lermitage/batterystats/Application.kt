package biz.lermitage.batterystats

import biz.lermitage.batterystats.conf.LocalAppConf
import biz.lermitage.batterystats.service.BatteryCollector
import biz.lermitage.batterystats.ui.HideToSystemTray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties(LocalAppConf::class)
class Application : CommandLineRunner {

    @Autowired
    private lateinit var batteryCollector: BatteryCollector

    override fun run(vararg args: String?) {
        HideToSystemTray()
        batteryCollector.collectInfinite()
    }
}

fun main(args: Array<String>) {
    val builder = SpringApplicationBuilder(Application::class.java)
    builder.headless(false).run(*args)
}
