import javafx.application.Application;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {

    public WeatherAppGUI() {
        super();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        //Loads the gui at the center of the screen
        setLocationRelativeTo(null);
        //Sets layout manager to null to manually control gui components
        setLayout(null);
        setResizable(false);
        addGuiCompentents();
    }

    private void addGuiCompentents() {
        JTextField searchTextfield = new JTextField();

        //Sets location and size of the searchfield
        searchTextfield.setBounds(15, 15, 350, 45);

        //Changes font and size within the searchfield
        searchTextfield.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextfield);


        //Adds image and of weather condition and sets bounds for the image
        JLabel weatherConditionImage = new JLabel(loadImage("assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //Adds text to show temperature. Sets font and bounds
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //Centers the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);


        //Adds a centered description of the weather
        JLabel weatherConditionDescription = new JLabel("Cloudy");
        weatherConditionDescription.setBounds(0, 405, 450, 36);
        weatherConditionDescription.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDescription);

        //Adds humidity image to the gui
        JLabel humidityImage = new JLabel(loadImage("assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //Creates a textlabel for the current humidity
        //Uses html syntax to only make some of the label bold
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //Add windspeed image to the gui
        JLabel windSpeedImage = new JLabel(loadImage("assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);


        JLabel windSpeedText = new JLabel("<html><b>Windspeed </b> 15km/h </html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        //Search button
        JButton searchButton = new JButton(loadImage("assets/search.png"));

        //Change cursor visuals to hand cursor when hovering over searchbutton
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //Sets the bounds of when the cursor should change
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextfield.getText();

                // Validates input from user and removes whitespace
                if (userInput.replaceAll("\\s", "").length() == 0) {
                    return;
                }

                //Retrieving weatherdata
                JSONObject weatherData = WeatherApp.getWeatherData(userInput);

                //Updating gui based on weather conditions

                //Updates the image
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("assets/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                weatherConditionDescription.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html> <b>Humidity:</b> " + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed: </b>" + windspeed + "km/h </html>");

            }
        });
        add(searchButton);

    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
}



