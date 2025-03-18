import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Creates a window of the weather app Gui
                new WeatherAppGUI().setVisible(true);

            }
        });
    }
}
