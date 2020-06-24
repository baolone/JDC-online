package com.jdc.app;

import java.io.FileInputStream;

import com.jdc.app.view.MainFrame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(MainFrame.class.getResource("MainFrame.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Book Store");
		primaryStage.getIcons().add(new Image(new FileInputStream("search.png")));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
