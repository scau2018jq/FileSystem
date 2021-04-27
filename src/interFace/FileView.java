package interFace;

import java.util.List;
import java.util.Optional;

import Bean.DiskBlock;
import Bean.FAT;
import Bean.File;
import Bean.Folder;
import Bean.Path;
import Utiliy.FATUtil;
import handler.DragWindowHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * 文件界面
 *
 * @author w
 *
 */

public class FileView {

	private File file;//文件
	private FAT fat;//文件分配表
	private DiskBlock block;//磁盘
	private String newContent, oldContent;//内容
	private Stage stage;
	private Scene scene;
	private BorderPane borderPane;
	private TextArea contentField;//文本框
	//菜单栏
	private MenuBar menuBar;
	private Menu fileMenu;
	private MenuItem saveItem, closeItem;
	//////////////
	//标签栏
	private Button minButton, amxButton, closeButton;
	private GridPane gridPane;
	//////////////
	private VBox fullbox;

	public FileView(File file, FAT fat, DiskBlock block) {
		this.file = file;
		this.fat = fat;
		this.block = block;
		showView();
	}

	/**
	 * 初始化
	 */
	private void showView() {

		contentField = new TextArea();
		contentField.setPrefRowCount(25);
		contentField.setWrapText(true);
		contentField.setText(file.getContent());
		if (file.getFlag() == FATUtil.FLAGREAD) {
			contentField.setDisable(true);
		}

		initlineMenu(stage);

		saveItem = new MenuItem("保存");
		saveItem.setGraphic(new ImageView(FATUtil.FILEX_IMG));
		saveItem.setOnAction(ActionEvent -> {
			newContent = contentField.getText();
			oldContent = file.getContent();
			if (newContent == null) {
				newContent = "";
			}
			if (!newContent.equals(oldContent)) {
				saveContent(newContent);
			}
		});

		closeItem = new MenuItem("关闭");
		closeItem.setGraphic(new ImageView(FATUtil.CLOSEX_IMG));
		closeItem.setOnAction(ActionEvent -> onClose(ActionEvent));

		fileMenu = new Menu("文件", null, saveItem, closeItem);
		menuBar = new MenuBar(fileMenu);
		menuBar.setPadding(new Insets(0));

		borderPane = new BorderPane(contentField, menuBar, null, null, null);
		fullbox = new VBox(gridPane, borderPane);
		fullbox.setPrefSize(800, 500);

		scene = new Scene(fullbox);
		stage = new Stage();
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(scene);
		stage.setTitle(file.getFileName());
		stage.titleProperty().bind(file.fileNamePProperty());
		stage.getIcons().add(new Image(FATUtil.FILE_IMG));
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				onClose(event);
			}
		});

		DragWindowHandler handler = new DragWindowHandler(stage);
		gridPane.setOnMousePressed(handler);
		gridPane.setOnMouseDragged(handler);
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
		gridPane.setPrefWidth(1000);
		gridPane.setAlignment(Pos.CENTER_LEFT);
		Label label = new Label(file.getFileName());
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
			newContent = contentField.getText();
			oldContent = file.getContent();
			boolean isCancel = false;
			if (newContent == null) {
				newContent = "";
			}
			System.out.println(newContent + " newContent");
			if (!newContent.equals(oldContent)) {
				event.consume();
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("保存更改");
				alert.setHeaderText(null);
				alert.setContentText("文件内容已更改，是否保存?");
				ButtonType saveType = new ButtonType("保存");
				ButtonType noType = new ButtonType("不保存");
				ButtonType cancelType = new ButtonType("取消", ButtonData.CANCEL_CLOSE);
				alert.getButtonTypes().setAll(saveType, noType, cancelType);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == saveType) {
					saveContent(newContent);
				} else if (result.get() == cancelType) {
					isCancel = true;
				}
			}
			if (!isCancel) {
				fat.removeOpenedFile(block);
				((Stage) (closeButton.getScene().getWindow())).close();
			}

		});

		gridPane.addColumn(0, label);
		GridPane.setHgrow(label, Priority.ALWAYS);
		gridPane.addColumn(1, minButton);
		gridPane.addColumn(2, amxButton);
		gridPane.addColumn(3, closeButton);

	}

	/**
	 * 关闭文件，并判断是否更改，是否要保存
	 *
	 * @param event
	 */
	private void onClose(Event event) {

	}

	/**
	 * 保存文件内容
	 *
	 * @param newContent
	 */
	private void saveContent(String newContent) {
		int newLength = newContent.length();
		int blockCount = FATUtil.blocksCount(newLength);
		file.setLength(blockCount);
		file.setContent(newContent);
		file.setSize(FATUtil.getSize(newLength));
		if (file.hasParent()) {
			Folder parent = (Folder) file.getParent();
			parent.setSize(FATUtil.getFolderSize(parent));
			while (parent.hasParent()) {
				parent = (Folder) parent.getParent();
				parent.setSize(FATUtil.getFolderSize(parent));
			}
		}
		fat.reallocBlocks(blockCount, block);
	}

}
