import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import weather.Period;
import weather.WeatherAPI;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JavaFX extends Application{
	//Main Scene FX Components
	Button threeDayForecastButton;
	Button changeLocationButton;
	BorderPane buttonPane;

	Text temperature;
	Text windSpeedDirection;
	VBox tempWindBox;

	Image weatherImage;
	ImageView imageView;
	HBox tempWindGraphicBox;

	Text shortDescription;
	Text longDescription;
	TextFlow descriptionFlow;

	VBox mainSceneBox;
	Scene mainScene;

	//Three Day Forecast Scene FX Components
	Button backButton;
	BorderPane threeDayButtonPane;

	Image raindropImage;

	ForecastBox dayOne;
	ForecastBox nightOne;
	VBox dayNightOne;

	ForecastBox dayTwo;
	ForecastBox nightTwo;
	VBox dayNightTwo;

	ForecastBox dayThree;
	ForecastBox nightThree;
	VBox dayNightThree;

	BorderPane threeDayForecastPane;
	VBox threeDayForecastSceneBox;
	Scene threeDayForecastScene;

	//Change Locations Scene FX Components
	Text instructions;

	Text latInstructions;
	TextField latInput;
	HBox latBox;

	Text lonInstructions;
	TextField lonInput;
	HBox lonBox;

	HBox latLonBox;

	Button enterButton;

	VBox changeLocationSceneBox;
	Scene changeLocationScene;

	public static void main(String[] args){launch(args);}

	@Override
	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("Weather App");

		ArrayList<Period> forecast = WeatherAPI.getForecast("LOT",77,70);
		if(forecast == null){throw new RuntimeException("Forecast did not load");}

		mainSceneSetup(forecast);
		threeDaySceneSetup(forecast);
		changeLocationSceneSetup(forecast);

		threeDayForecastButton.setOnAction(e -> {	//Change scene from main scene to three day forecast scene
			primaryStage.setScene(threeDayForecastScene);
		});

		changeLocationButton.setOnAction(e -> {		//Change scene from main scene to change locations scene
			enterButton.setText("Enter");
			primaryStage.setScene(changeLocationScene);
		});

		backButton.setOnAction(e -> {				//Change scene from three day forecast scene to main scene
			primaryStage.setScene(mainScene);
		});

		enterButton.setOnAction(e -> {
			//Check if input is valid double
			double tempLat = 0.0;
			double tempLon = 0.0;
			try{
				tempLat = Double.parseDouble(latInput.getText());
				tempLon = Double.parseDouble(lonInput.getText());
			}catch(NumberFormatException nfe){}

			Timer timer = new Timer();		//Used for user feedback
			TimerTask task = new TimerTask(){
				public void run(){
					//For some reason JavaFX doesn't like it if you change stuff while in the timer class, this is a work around
					Platform.runLater(() -> {
						primaryStage.setScene(mainScene);
					});
					timer.cancel();			//Close timer after used
				}
			};

			MyWeatherAPI.Properties properties = MyWeatherAPI.getRegion(tempLat, tempLon);
			timer.schedule(task, 3000L);		//Program will show user if change location was successful or not then return to the main scene after 3 seconds
			if(properties == null){
				enterButton.setText("Failed");
			}else{
				updateForecast(forecast, properties.gridId, properties.gridX, properties.gridY);
				enterButton.setText("Success");
			}
		});

		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	private void mainSceneSetup(ArrayList<Period> forecast){
		//Buttons on opposite sides of the top
		threeDayForecastButton = new Button("3 Day Forecast");
		changeLocationButton = new Button("Change Location");
		buttonPane = new BorderPane();
		buttonPane.setMinHeight(40);
		buttonPane.setLeft(threeDayForecastButton);
		buttonPane.setRight(changeLocationButton);
		buttonPane.setPadding(new Insets(5, 5, 5, 5));

		//Temperature in large font and windspeed/direction in slightly smaller font below it; weather image to the right of both
		temperature = new Text(forecast.get(0).temperature + "°" + forecast.get(0).temperatureUnit);
		temperature.setFont(Font.font(50));
		temperature.setFill(Color.WHITE);
		windSpeedDirection = new Text(forecast.get(0).windSpeed + " " + forecast.get(0).windDirection);
		windSpeedDirection.setFont(Font.font(15));
		windSpeedDirection.setFill(Color.WHITE);

		tempWindBox = new VBox(temperature, windSpeedDirection);
		tempWindBox.setAlignment(Pos.CENTER);
		tempWindBox.setMinWidth(150);

		//The national weather api has images for each weather condition; api automatically gets "medium" size, "large" is more helpful in this case
		weatherImage = new Image(forecast.get(0).icon.substring(0, forecast.get(0).icon.length() - 6) + "large");
		imageView = new ImageView(weatherImage);
		imageView.setFitWidth(200);
		imageView.setFitHeight(200);
		tempWindGraphicBox = new HBox(tempWindBox, imageView);

		//Short description in large font and long description in smaller font below it
		shortDescription = new Text(forecast.get(0).shortForecast + "\n");
		shortDescription.setFont(Font.font(20));
		shortDescription.setFill(Color.WHITE);
		longDescription = new Text(forecast.get(0).detailedForecast);
		longDescription.setFont(Font.font(12));
		longDescription.setFill(Color.WHITE);

		descriptionFlow = new TextFlow(shortDescription, longDescription);	//Used to get long description text to wrap around window
		descriptionFlow.setPadding(new Insets(5, 5, 5, 5));

		mainSceneBox = new VBox(buttonPane, tempWindGraphicBox, descriptionFlow);
		if(forecast.get(0).isDaytime)										//Set window color based on time of day
			mainSceneBox.setStyle("-fx-background-color: lightsteelblue;");
		else
			mainSceneBox.setStyle("-fx-background-color: lightslategray;");
		mainScene = new Scene(mainSceneBox, 600,325);
	}

	//Used to get multiple instances of ImageViewer for the raindrop image
	private ImageView getRainDropImageView(){return new ImageView(raindropImage);}

	//Used to setup and update each forecast box
	private class ForecastBox{
		Text time;
		Text temperature;
		Text precipitation;
		HBox precip;
		VBox day;

		private ForecastBox(Period period){
			//Time of forecast in small font above temperature in large font above precipitation change in medium font
			time = new Text(period.name);
			time.setFont(Font.font(15));
			time.setFill(Color.WHITE);
			temperature = new Text(period.temperature + "°" + period.temperatureUnit);
			temperature.setFont(Font.font(50));
			temperature.setFill(Color.WHITE);
			precipitation = new Text(period.probabilityOfPrecipitation.value + "%");
			precipitation.setFont(Font.font(20));
			precipitation.setFill(Color.WHITE);

			//Put raindrop image to the left of precipitation chance
			precip = new HBox(getRainDropImageView(), precipitation);
			precip.setAlignment(Pos.CENTER);

			day = new VBox(time, temperature, precip);
			day.setAlignment(Pos.CENTER);
			day.setPadding(new Insets(5, 5, 5, 5));
			if(period.isDaytime)								//Set forecast box color based on time of day of that forecast
				day.setStyle("-fx-background-color: skyblue;");
			else
				day.setStyle("-fx-background-color: midnightblue;");
		}

		private void updateForecast(Period period){
			time.setText(period.name);
			temperature.setText(period.temperature + "°" + period.temperatureUnit);
			precipitation.setText(period.probabilityOfPrecipitation.value + "%");
			if(period.isDaytime)
				day.setStyle("-fx-background-color: skyblue;");
			else
				day.setStyle("-fx-background-color: midnightblue;");
		}
	}

	private void threeDaySceneSetup(ArrayList<Period> forecast){
		//Back button in same location as three day forecast button on main scene; Borderpane used to keep consistency
		backButton = new Button("Back");
		threeDayButtonPane = new BorderPane();
		threeDayButtonPane.setMinHeight(40);
		threeDayButtonPane.setLeft(backButton);
		threeDayButtonPane.setPadding(new Insets(5, 5, 5, 5));

		//Import raindrop image from resource folder
		raindropImage = new Image(getClass().getResource("/raindrop.png").toExternalForm(), 25, 25, false, false);

		//Stack daytime and nighttime forecast boxes for each day on top of each other
		dayOne = new ForecastBox(forecast.get(0));
		nightOne = new ForecastBox(forecast.get(1));
		dayNightOne = new VBox(dayOne.day, nightOne.day);
		dayNightOne.setSpacing(10);
		dayNightOne.setPrefWidth(150);

		dayTwo = new ForecastBox(forecast.get(2));
		nightTwo = new ForecastBox(forecast.get(3));
		dayNightTwo = new VBox(dayTwo.day, nightTwo.day);
		dayNightTwo.setSpacing(10);
		dayNightTwo.setMaxWidth(150);

		dayThree = new ForecastBox(forecast.get(4));
		nightThree = new ForecastBox(forecast.get(5));
		dayNightThree = new VBox(dayThree.day, nightThree.day);
		dayNightThree.setSpacing(10);
		dayNightThree.setPrefWidth(150);

		//Set day 1 on the left, day 2 in the middle, and day 3 to the right
		threeDayForecastPane = new BorderPane();
		threeDayForecastPane.setLeft(dayNightOne);
		threeDayForecastPane.setCenter(dayNightTwo);
		threeDayForecastPane.setRight(dayNightThree);
		threeDayForecastPane.setPadding(new Insets(10, 10, 10, 10));

		threeDayForecastSceneBox = new VBox(threeDayButtonPane, threeDayForecastPane);
		if(forecast.get(0).isDaytime)													//Set window color based on time of day
			threeDayForecastSceneBox.setStyle("-fx-background-color: lightsteelblue;");
		else
			threeDayForecastSceneBox.setStyle("-fx-background-color: lightslategray;");
		threeDayForecastScene = new Scene(threeDayForecastSceneBox, 600,325);
	}

	private void changeLocationSceneSetup(ArrayList<Period> forecast){
		//Instructions on the top, latitude input on the left, longitude input on the right, enter button on the bottom; text fields start with starting location values
		instructions = new Text("Enter latitude and longitude coordinates");
		instructions.setFont(Font.font(25));
		instructions.setFill(Color.WHITE);

		latInstructions = new Text("Latitude:");
		latInstructions.setFont(Font.font(15));
		latInstructions.setFill(Color.WHITE);
		latInput = new TextField(String.valueOf(41.8078));	//Starting location latitude
		latBox = new HBox(latInstructions, latInput);
		latBox.setAlignment(Pos.CENTER);

		lonInstructions = new Text("Longitude");
		lonInstructions.setFont(Font.font(15));
		lonInstructions.setFill(Color.WHITE);
		lonInput = new TextField(String.valueOf(-87.5897));	//Starting location longitude
		lonBox = new HBox(lonInstructions, lonInput);
		lonBox.setAlignment(Pos.CENTER);

		latLonBox = new HBox(latBox, lonBox);
		latLonBox.setSpacing(30);
		latLonBox.setAlignment(Pos.CENTER);

		enterButton = new Button("Enter");

		changeLocationSceneBox = new VBox(instructions, latLonBox, enterButton);
		changeLocationSceneBox.setSpacing(20);
		changeLocationSceneBox.setAlignment(Pos.CENTER);
		if(forecast.get(0).isDaytime)												//Change color of window based on time of day
			changeLocationSceneBox.setStyle("-fx-background-color: lightsteelblue;");
		else
			changeLocationSceneBox.setStyle("-fx-background-color: lightslategray;");
		changeLocationScene = new Scene(changeLocationSceneBox, 600,325);
	}

	//Updates all forecast values of the program for a new location
	private void updateForecast(ArrayList<Period> forecast, String ID, int x, int y){
		forecast = WeatherAPI.getForecast(ID, x, y);	//Values are from national weather api

		//Update main scene values
		temperature.setText(forecast.get(0).temperature + "°" + forecast.get(0).temperatureUnit);
		windSpeedDirection.setText(forecast.get(0).windSpeed + " " + forecast.get(0).windDirection);
		Image newImage = new Image(forecast.get(0).icon.substring(0, forecast.get(0).icon.length() - 6) + "large");
		imageView.setImage(newImage);
		shortDescription.setText(forecast.get(0).shortForecast + "\n");
		longDescription.setText(forecast.get(0).detailedForecast);
		if(forecast.get(0).isDaytime)
			mainSceneBox.setStyle("-fx-background-color: lightsteelblue;");
		else
			mainSceneBox.setStyle("-fx-background-color: lightslategray;");

		//Update three day forecast values
		dayOne.updateForecast(forecast.get(0));
		nightOne.updateForecast(forecast.get(1));
		dayTwo.updateForecast(forecast.get(2));
		nightTwo.updateForecast(forecast.get(3));
		dayThree.updateForecast(forecast.get(4));
		nightThree.updateForecast(forecast.get(5));
		if(forecast.get(0).isDaytime)
			threeDayForecastSceneBox.setStyle("-fx-background-color: lightsteelblue;");
		else
			threeDayForecastSceneBox.setStyle("-fx-background-color: lightslategray;");
	}
}