package biz.lermitage.batterystats.service.impl

import biz.lermitage.batterystats.conf.LocalAppConf
import biz.lermitage.batterystats.service.ZipCompressor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
class ZipCompressorImpl(private val conf: LocalAppConf) : ZipCompressor {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val dateFormat = SimpleDateFormat("yyyyMMdd")

    override fun compressFinalizedReports() {
        val currDate = dateFormat.format(Date())

        val listFiles = File(conf.battery.reportDir).listFiles { file: File ->
            !file.name.contains(currDate) && file.name.endsWith(".csv")
        }

        listFiles!!.toList().stream().forEach { oldCsvReport: File ->
            val compressedCsvReportPath = oldCsvReport.absolutePath + ".zip"
            logger.info("compress ${oldCsvReport.absolutePath} to $compressedCsvReportPath")
            zip(oldCsvReport.absolutePath, compressedCsvReportPath)

            logger.info("delete ${oldCsvReport.absolutePath}")
            oldCsvReport.delete()
        }
    }

    private fun zip(sourcePath: String, zipPath: String) {
        val zipFile = File(zipPath)
        if (zipFile.exists()) {
            zipFile.delete()
        }
        val fos = FileOutputStream(zipPath)
        fos.use(fun(fos: FileOutputStream) {
            val zipOut = ZipOutputStream(fos)
            val fileToZip = File(sourcePath)
            val fis = FileInputStream(fileToZip)
            fis.use(fun(fis: FileInputStream) {
                val zipEntry = ZipEntry(fileToZip.name)
                zipOut.use(fun(zipOut: ZipOutputStream) {
                    zipOut.putNextEntry(zipEntry)
                    val bytes = ByteArray(1024)
                    var length: Int
                    while (fis.read(bytes).also { length = it } >= 0) {
                        zipOut.write(bytes, 0, length)
                    }
                })
            })
        })
    }
}
