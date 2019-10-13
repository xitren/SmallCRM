/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smallcrm;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author xitre
 */
public class SmallCRM extends Application {
    
    CalendarViewCRM calendarView;
    Timer timer = new Timer();
    
    @Override
    public void start(Stage primaryStage) {
        calendarView = new CalendarViewCRM(
		"Arsenty P. Gusev", "db.json"
	);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(calendarView); // introPane);

        Scene scene = new Scene(stackPane);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                calendarView.saveToFile("db.json");
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                calendarView.saveToFile("db.json");
            }
        }, 0, 10000);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
    
}



