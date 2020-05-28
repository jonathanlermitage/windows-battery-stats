package biz.lermitage.batterystats.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

public class HideToSystemTray extends JFrame {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String APP_NAME = "Battery Stats Collector for Windows";
    private static final Image logo = Toolkit.getDefaultToolkit().getImage("icon.png");

    private TrayIcon trayIcon;
    private SystemTray tray;

    public HideToSystemTray() {
        super(APP_NAME);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Unable to set LookAndFeel", e);
        }
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();

            ActionListener exitListener = e -> System.exit(0);
            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Exit program");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            /*defaultItem = new MenuItem("Show");
            defaultItem.addActionListener(e -> {
                setVisible(true);
                setExtendedState(JFrame.NORMAL);
            });*/
            popup.add(defaultItem);
            trayIcon = new TrayIcon(logo, APP_NAME, popup);
            trayIcon.setImageAutoSize(true);
        } else {
            logger.warn("system tray not supported");
        }
        addWindowStateListener(e -> {
            if (e.getNewState() == ICONIFIED) {
                try {
                    tray.add(trayIcon);
                    setVisible(false);
                } catch (AWTException ex) {
                    logger.warn("unable to add to tray", ex);
                }
            }
            if (e.getNewState() == 7) {
                try {
                    tray.add(trayIcon);
                    setVisible(false);
                } catch (AWTException ex) {
                    logger.warn("unable to add to system tray", ex);
                }
            }
            if (e.getNewState() == MAXIMIZED_BOTH) {
                tray.remove(trayIcon);
                setVisible(true);
            }
            if (e.getNewState() == NORMAL) {
                tray.remove(trayIcon);
                setVisible(true);
            }
        });
        setIconImage(logo);

        setVisible(true);
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setState(ICONIFIED);
    }
}
