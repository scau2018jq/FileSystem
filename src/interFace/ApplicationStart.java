package interFace;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application Start
 * @author w
 *
 */
public class ApplicationStart extends Application{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		new MainView(primaryStage);

	}

}
