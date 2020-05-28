package biz.lermitage.batterystats.core;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * See <a href="https://stackoverflow.com/questions/3434719/how-to-get-the-remaining-battery-life-in-a-windows-system">original code</a> and
 * <a href="https://docs.microsoft.com/en-us/windows/desktop/api/winbase/ns-winbase-_system_power_status">Microsoft documentation</a>.
 */
@SuppressWarnings({"WeakerAccess", "UnnecessaryInterfaceModifier"})
public interface Kernel32 extends StdCallLibrary {

    /** ACLineStatus -> battery state: "Online", "Offline". */
    public static final String FIELD_ACLINESTATUS = "AC";

    /** BatteryFlag -> battery state flag, something like "High, more than 66 percent". */
    public static final String FIELD_BATTERYFLAG = "Flag";

    /** BatteryLifePercent -> battery remaining lifetime as percentage from "0%" to "100%". */
    public static final String FIELD_BATTERYLIFEPERCENT = "LifePercent";

    /** BatteryLifeTime -> battery remaining lifetime as time (HH:mm:ss), ex "06:19:23". */
    public static final String FIELD_BATTERYLIFETIME = "LifeTime";

    /** BatteryFullLifeTime -> */
    public static final String FIELD_BATTERYFULLLIFETIME = "FullLifeTime";

    public static final String UNKNOWN = "Unknown";

    public static long MAX_BATT_LIFE = 86399;
    public static LocalTime MAX_BATT_LIFE_DATE = LocalTime.ofSecondOfDay(MAX_BATT_LIFE);

    public static Kernel32 INSTANCE = getKernel32();

    public static Kernel32 getKernel32() {
        try {
            return Native.load("Kernel32", Kernel32.class);
        } catch (UnsatisfiedLinkError ule) {
            return result -> -2;
        } catch (Throwable e) {
            return result -> -1;
        }
    }

    /**
     * http://msdn2.microsoft.com/en-us/library/aa373232.aspx
     */
    @SuppressWarnings("unused")
    public class SYSTEM_POWER_STATUS extends Structure {

        private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        public byte ACLineStatus;
        public byte BatteryFlag;
        public byte BatteryLifePercent;
        public byte Reserved1;
        public int BatteryLifeTime;
        public int BatteryFullLifeTime;

        @Override
        protected List<String> getFieldOrder() {
            ArrayList<String> fields = new ArrayList<>();
            fields.add("ACLineStatus");
            fields.add("BatteryFlag");
            fields.add("BatteryLifePercent");
            fields.add("Reserved1");
            fields.add("BatteryLifeTime");
            fields.add("BatteryFullLifeTime");
            return fields;
        }

        /** The AC power status. */
        public String getACLineStatusString() {
            switch (ACLineStatus) {
                case (0):
                    return "Offline";
                case (1):
                    return "Online";
                default:
                    return UNKNOWN;
            }
        }

        /** The battery charge status. */
        public String getBatteryFlagString() {
            switch (BatteryFlag) {
                case (1):
                    return "High, more than 66 percent";
                case (2):
                    return "Low, less than 33 percent";
                case (4):
                    return "Critical, less than five percent";
                case (8):
                    return "Charging";
                case ((byte) 128):
                    return "No system battery";
                default:
                    return UNKNOWN;
            }
        }

        /** The percentage of full battery charge remaining. */
        public String getBatteryLifePercent() {
            return (BatteryLifePercent == (byte) 255) ? UNKNOWN : BatteryLifePercent + "%";
        }

        /** The number of seconds of battery life remaining. */
        public String getBatteryLifeTime() {
            if (BatteryLifeTime == -1) {
                return UNKNOWN;
            } else {
                if (BatteryLifeTime > MAX_BATT_LIFE) { // fix java.time.DateTimeException: Invalid value for SecondOfDay (valid values 0 - 86399): 106922
                    return "more than " + timeFormat.format(MAX_BATT_LIFE_DATE);
                }
                return timeFormat.format(LocalTime.ofSecondOfDay(BatteryLifeTime));
            }
        }

        /** The number of seconds of battery life when at full charge. */
        public String getBatteryFullLifeTime() {
            return (BatteryFullLifeTime == -1) ? UNKNOWN : BatteryFullLifeTime + " seconds";
        }

        @Override
        public String toString() {
            return ("ACLineStatus: " + getACLineStatusString() + "\n") +
                "Battery Flag: " + getBatteryFlagString() + "\n" +
                "Battery Life: " + getBatteryLifePercent() + "\n" +
                "Battery Left: " + getBatteryLifeTime() + "\n" +
                "Battery Full: " + getBatteryFullLifeTime() + "\n";
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    int GetSystemPowerStatus(SYSTEM_POWER_STATUS result);
}
