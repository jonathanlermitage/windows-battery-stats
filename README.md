<h1 align="center">
    Battery Stats
</h1>

A MS Windows program that logs battery statistics.

## Build and Run

Install a JDK8+, Gradle, and add java to system PATH.  
Build with `gradlew bootJar`, then run application by going to `build/libs/` and run `run-noconsole.cmd`. Program has no UI, adds an icon in System Tray (you can right-clic on icon and close application), and regularly reports battery state (Online or Offline), charge level percentage and remaining lifetime to a CSV report file in `report/` folder (one file per day).

## License

MIT License. In other words, you can do what you want: this project is entirely OpenSource, Free and Gratis.
