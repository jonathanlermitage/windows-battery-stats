package biz.lermitage.batterystats.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static biz.lermitage.batterystats.core.Kernel32.UNKNOWN;

@SuppressWarnings("WeakerAccess")
public class BatteryUtils {

    @NotNull
    @Contract(pure = true)
    public static List<String> readWindowsBatteryStatus(String... batteryFields) throws IOException {
        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        int status = Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        if (status == -2) {
            throw new IOException("Battery: cannot invoke Kernel32");
        } else if (status == -1) {
            throw new IOException("Battery: error");
        }

        List<String> retrievedBatteryFields = new ArrayList<>();
        boolean fieldFound = false;
        for (String field : batteryFields) {
            field = field.trim();
            if (field.equalsIgnoreCase(Kernel32.FIELD_ACLINESTATUS)) {
                retrievedBatteryFields.add(batteryStatus.getACLineStatusString());
                fieldFound = true;
            }
            if (field.equalsIgnoreCase(Kernel32.FIELD_BATTERYFLAG)) {
                retrievedBatteryFields.add(batteryStatus.getBatteryFlagString());
                fieldFound = true;
            }
            if (field.equalsIgnoreCase(Kernel32.FIELD_BATTERYLIFEPERCENT)) {
                retrievedBatteryFields.add(batteryStatus.getBatteryLifePercent());
                fieldFound = true;
            }
            if (field.equalsIgnoreCase(Kernel32.FIELD_BATTERYLIFETIME)) {
                retrievedBatteryFields.add(batteryStatus.getBatteryLifeTime());
                fieldFound = true;
            }
            if (field.equalsIgnoreCase(Kernel32.FIELD_BATTERYFULLLIFETIME)) {
                retrievedBatteryFields.add(batteryStatus.getBatteryFullLifeTime());
                fieldFound = true;
            }
        }
        if (!fieldFound) {
            throw new IOException("Battery: field(s) not found");
        }

        return retrievedBatteryFields.stream()
            .map(s -> s == null ? "" : s.replace(UNKNOWN, "").trim())
            .collect(Collectors.toList());
    }
}
