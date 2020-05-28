package biz.lermitage.batterystats.service.impl

import biz.lermitage.batterystats.service.TimeService
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class TimeServiceImpl : TimeService {

    private val dateFormat = SimpleDateFormat("yyyyMMdd")
    private var prevDate = ""

    override fun rotateDay(): Boolean {
        val currDate = dateFormat.format(Date())
        if (prevDate == "") {
            prevDate = currDate
            return true
        }
        return if (currDate == prevDate) {
            false
        } else {
            prevDate = currDate
            true
        }
    }
}
