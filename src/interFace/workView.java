package interFace;

import Bean.FAT;
import Bean.File;
import Utiliy.FATUtil;
import handler.DragWindowHandler;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class workView {
	private FAT fat;
	private VBox vbox;
	private Stage stage;
	private Scene scene;
	private Image ico;
	private TableView<File> openedTable;// 打开文件表
	private ObservableList<File> dataOpened;// 被打开文件数据
	private GridPane gridPane;
	private Button minButton,amxButton,closeButton;
	public workView(FAT fat){
		this.fat = fat;
		initshow();
	}

	public void initshow(){

		initTables();

		initlineMenu(stage);

		vbox = new VBox(gridPane,openedTable);
	    scene = new Scene(vbox);
	    stage = new Stage();
	    stage.initStyle(StageStyle.TRANSPARENT);
	    stage.setScene(scene);
		DragWindowHandler handler = new DragWindowHandler(stage);
		gridPane.setOnMousePressed(handler);
		gridPane.setOnMouseDragged(handler);
		scene.getStylesheets().addAll("tableView.css", "box.css", "backimage.css", "tableviewclo.css");

		stage.show();
	}


	/**
	 * 标签栏
	 * @param stage
	 */
	private void initlineMenu(Stage stage) {
		gridPane = new GridPane();
		gridPane.setStyle("-fx-background-color: rgb(211 211 211);");
		gridPane.setPrefHeight(32);
		gridPane.setPrefWidth(800);
		gridPane.setAlignment(Pos.CENTER_LEFT);
		DragWindowHandler handler = new DragWindowHandler(stage);
		Label label = new Label("任务栏");
		label.setFont(Font.font(14));
		label.setTextFill(Paint.valueOf("black"));
		ImageView imageView = new ImageView(FATUtil.FILE_IMG);
		imageView.setFitHeight(30);
		imageView.setFitWidth(35);
		label.setGraphic(imageView);
		minButton = new Button("");

		amxButton = new Button("");
		closeButton = new Button("");
		minButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0.1;  -fx-background-insets: 0;"
				+ "-fx-max-height: infinity; -fx-border-image-insets: 0;");
		amxButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0.1;  -fx-background-insets: 0;"
				+ "-fx-max-height: infinity; -fx-border-image-insets: 0;");
		closeButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0.1;  -fx-background-insets: 0;"
				+ "-fx-max-height: infinity; -fx-border-image-insets: 0;");

		Image btnImg = new Image(FATUtil.SMALL_IMG);
		ImageView imagev = new ImageView(btnImg);
		imagev.setFitHeight(25);
		imagev.setFitWidth(25);
		minButton.setGraphic(imagev);
		Image btn1Img = new Image(FATUtil.RESET_IMG);
		ImageView imagev1 = new ImageView(btn1Img);
		imagev1.setFitHeight(25);
		imagev1.setFitWidth(25);
		amxButton.setGraphic(imagev1);
		Image btn2Img = new Image(FATUtil.CLOSE_IMG);
		ImageView imagev2 = new ImageView(btn2Img);
		imagev2.setFitHeight(25);
		imagev2.setFitWidth(25);
		closeButton.setGraphic(imagev2);

		/*
		 * minButton.setOnAction(new EventHandler<ActionEvent>() {
		 *
		 * @Override public void handle(ActionEvent event) {
		 * Stage.setIconified(true); } }); amxButton.setOnAction(new
		 * EventHandler<ActionEvent>() {
		 *
		 * @Override public void handle(ActionEvent event) {
		 * primaryStage.setMaximized(!primaryStage.isMaximized()); } });
		 */
		/*
		 * closeButton.setOnAction(new EventHandler<ActionEvent>() {
		 *
		 * @Override public void handle(ActionEvent event) { (Stage)
		 * (closeButton.getScene().getWindow())).close(); } });
		 */
		minButton.setOnAction(event -> ((Stage) (minButton.getScene().getWindow())).setIconified(true));

		amxButton.setOnAction(event -> ((Stage) (amxButton.getScene().getWindow()))
				.setMaximized(!((Stage) amxButton.getScene().getWindow()).isMaximized()));

		closeButton.setOnAction(event -> {
				((Stage) (closeButton.getScene().getWindow())).close();


		});

		gridPane.addColumn(0, label);
		GridPane.setHgrow(label, Priority.ALWAYS);
		gridPane.addColumn(1, minButton);
		gridPane.addColumn(2, amxButton);
		gridPane.addColumn(3, closeButton);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initTables() {
		openedTable = new TableView<File>();

		/*
		 * blockTable .setStyle("-fx-background-color: #ffffff;" +
		 * "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");
		 */
		openedTable
				.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");

		dataOpened = fat.getOpenedFiles();

		TableColumn nameCol = new TableColumn("文件名");
		// nameCol.getStyleClass().add("my-tableColumn");
		nameCol.setCellValueFactory(new PropertyValueFactory<File, String>("fileNameP"));
		nameCol.setSortable(false);
		nameCol.setMinWidth(156);
		nameCol.setResizable(false);

		TableColumn flagCol = new TableColumn("打开方式");
		// flagCol.getStyleClass().add("my-tableColumn");
		flagCol.setCellValueFactory(new PropertyValueFactory<File, String>("flagP"));
		flagCol.setSortable(false);
		flagCol.setResizable(false);

		TableColumn diskCol = new TableColumn("起始盘块号");
		// diskCol.getStyleClass().add("my-tableColumn");
		diskCol.setCellValueFactory(new PropertyValueFactory<File, String>("diskNumP"));
		diskCol.setSortable(false);
		diskCol.setMinWidth(150);
		diskCol.setResizable(false);

		TableColumn pathCol = new TableColumn("路径");
		// pathCol.getStyleClass().add("my-tableColumn");
		pathCol.setCellValueFactory(new PropertyValueFactory<File, String>("locationP"));
		pathCol.setSortable(false);
		pathCol.setMinWidth(400);
		pathCol.setResizable(false);

		TableColumn lengthCol = new TableColumn("文件长度");
		// lengthCol.getStyleClass().add("my-tableColumn");
		lengthCol.setCellValueFactory(new PropertyValueFactory<File, String>("lengthP"));
		lengthCol.setSortable(false);
		lengthCol.setMinWidth(100);
		lengthCol.setResizable(false);
		openedTable.setPlaceholder(new Label("暂无打开的文件"));
		openedTable.setItems(dataOpened);
		openedTable.getColumns().addAll(nameCol, flagCol, lengthCol, diskCol, pathCol);
		openedTable.setPrefHeight(500);
	}
}
