package interFace;

import java.util.Map;

import Bean.DiskBlock;
import Bean.FAT;
import Bean.File;
import Bean.Folder;
import Bean.Path;
import Utiliy.FATUtil;
import handler.DragWindowHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 属性界面
 * @author w
 *
 */

public class PropertyView {

	private DiskBlock block;
	private FAT fat;
	private Label icon;
	private Map<Path, TreeItem<String>> pathMap;
	private String oldName, location;
	private Stage stage;
	private Scene scene;
	private VBox vBox;
	private HBox hBox;
	private GridPane gridPane,linePane;
	private TextField nameField;
	private Label typeField, locField, sizeField,
					spaceField, timeField;
	private Button okButton, cancelButton, applyButton;
	private final ToggleGroup toggleGroup = new ToggleGroup();
	private Image ico;

	private String name;

	public Button minButton,amxButton,closeButton;

	public PropertyView(DiskBlock block, FAT fat, Label icon, Map<Path, TreeItem<String>> pathMap) {
		this.block = block;
		this.fat = fat;
		this.icon = icon;
		this.pathMap = pathMap;
		showView();
	}

	private void showView() {

		RadioButton checkRead = new RadioButton("只读");
		checkRead.setToggleGroup(toggleGroup);
		checkRead.setUserData(FATUtil.FLAGREAD);

		RadioButton checkWrite = new RadioButton("读写");
		checkWrite.setToggleGroup(toggleGroup);
		checkWrite.setUserData(FATUtil.FLAGWRITE);

		HBox checkBoxGroup = new HBox(checkRead, checkWrite);
		checkBoxGroup.setSpacing(10);


		//显示属性
		if (block.getObject() instanceof Folder) {
			Folder folder = (Folder)block.getObject();
			nameField = new TextField(folder.getFolderName());
			typeField = new Label(folder.getType());
			locField = new Label(folder.getLocation());
			sizeField = new Label(folder.getSize() + "KB");
			spaceField = new Label(folder.getSpace());
			timeField = new Label(folder.getCreateTime());
			oldName = folder.getFolderName();
			location = folder.getLocation();
			checkRead.setDisable(true);
			checkWrite.setDisable(true);
			ico = new Image(FATUtil.FOLDER_IMG);
		} else {
			File file = (File)block.getObject();
			nameField = new TextField(file.getFileName());
			typeField = new Label(file.getType());
			locField = new Label(file.getLocation());
			sizeField = new Label(file.getSize() + "KB");
			spaceField = new Label(file.getSpace());
			timeField = new Label(file.getCreateTime());
			oldName = file.getFileName();
			location = file.getLocation();
			toggleGroup.selectToggle(file.getFlag() == FATUtil.FLAGREAD ? checkRead : checkWrite);
			ico = new Image(FATUtil.FILE_IMG);
		}

		name = nameField.getText();

		//确定按钮
		okButton = new Button("确定");
		okButton.setPrefSize(100, 20);
		okButton.setStyle("-fx-background-color:#d3d3d3;");
		okButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #808080;");
			}
		});
		okButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});

		//取消按钮
		cancelButton = new Button("取消");
		cancelButton.setPrefSize(100, 20);
		cancelButton.setStyle("-fx-background-color:#d3d3d3;");
		cancelButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cancelButton.setStyle("-fx-background-color: #ffffff;");
			}
		});
		cancelButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cancelButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});

		//应用按钮
		applyButton = new Button("应用");
		applyButton.setPrefSize(100, 20);
		applyButton.setStyle("-fx-background-color:#d3d3d3;");
		applyButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				applyButton.setStyle("-fx-background-color: #808080;");
			}
		});
		applyButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				applyButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});

		buttonOnAction();


		//文件名文为空或未修改
		nameField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.equals("") || newValue.equals(name)) {
					applyButton.setDisable(true);
					okButton.setDisable(true);
				} else {
					applyButton.setDisable(false);
				}
			}
		});

		//读写or只读
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				applyButton.setDisable(false);
			}
		});

		hBox = new HBox(okButton, cancelButton, applyButton);
		hBox.setPadding(new Insets(15, 12, 5, 12));
		hBox.setSpacing(10);

		gridPane = new GridPane();
		gridPane.add(new Label("名称:"), 0, 0);
		gridPane.add(new Label("文件类型:"), 0, 1);
		gridPane.add(new Label("位置:"), 0, 2);
		gridPane.add(new Label("大小:"), 0, 3);
		gridPane.add(new Label("占用空间:"), 0, 4);
		gridPane.add(new Label("建立时间:"), 0, 5);
		gridPane.add(new Label("属性:"), 0, 6);
		gridPane.add(nameField, 1, 0);
		gridPane.add(typeField, 1, 1);
		gridPane.add(locField, 1, 2);
		gridPane.add(sizeField, 1, 3);
		gridPane.add(spaceField, 1, 4);
		gridPane.add(timeField, 1, 5);
		gridPane.add(checkBoxGroup, 1, 6);
		gridPane.setPadding(new Insets(15, 12, 0, 12));
		gridPane.setVgap(10);
		gridPane.setHgap(10);



		vBox = new VBox();
		stage = new Stage();
		initlineMenu(stage);

		vBox.getChildren().addAll(linePane,gridPane, hBox);
		vBox.setStyle("-fx-background-color: #ffffff;");

		scene = new Scene(vBox);

		stage.initStyle(StageStyle.TRANSPARENT);

		stage.setScene(scene);
		stage.getIcons().add(ico);
		stage.setAlwaysOnTop(true);
		stage.show();

	}


	private void initlineMenu(Stage stage){
		linePane = new GridPane();
		linePane.setStyle("-fx-background-color: rgb(211 211 211);");//日志WARNING
		linePane.setPrefHeight(32);
		linePane.setPrefWidth(300);
		linePane.setAlignment(Pos.CENTER_LEFT);
		DragWindowHandler handler = new DragWindowHandler(stage);
		linePane.setOnMousePressed(handler);
		linePane.setOnMouseDragged(handler);
		Label label = new Label("属性");
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

        /*minButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Stage.setIconified(true);
			}
		});
		amxButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.setMaximized(!primaryStage.isMaximized());
			}
		});*/
		/*closeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				(Stage) (closeButton.getScene().getWindow())).close();
			}
		});*/
        minButton.setOnAction(event ->
        ((Stage) (minButton.getScene().getWindow())).setIconified(true));

        amxButton.setOnAction(event ->
        ((Stage) (amxButton.getScene().getWindow())).setMaximized(!((Stage) amxButton.getScene().getWindow()).isMaximized()));

		closeButton.setOnAction(event ->
        ((Stage) (closeButton.getScene().getWindow())).close());


		linePane.addColumn(0, label);
		GridPane.setHgrow(label, Priority.ALWAYS);
		linePane.addColumn(1, minButton);
		linePane.addColumn(2, amxButton);
		linePane.addColumn(3, closeButton);
	}

	private void buttonOnAction() {
		//应用按钮，判断文件名是否存在
		applyButton.setOnAction(ActionEvent -> {
			String newName = nameField.getText();
			if (!oldName.equals(newName)) {
				if (fat.hasName(location, newName)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("此位置已包含同名文件/文件夹");
					alert.show();
				} else {
					if (block.getObject() instanceof Folder) {
						Folder thisFolder = (Folder)block.getObject();
						thisFolder.setFolderName(newName);
						pathMap.get(thisFolder.getPath()).setValue(newName);
						reLoc(location, location, oldName, newName, thisFolder);
					} else {
						((File)block.getObject()).setFileName(newName);
					}
					oldName = newName;
					icon.setText(newName);
				}
			}
			if (block.getObject() instanceof File) {
				File thisFile = ((File)block.getObject());
				int newFlag = toggleGroup.getSelectedToggle().getUserData().hashCode();
				thisFile.setFlag(newFlag);
			}
			applyButton.setDisable(true);
		});

		//取消按钮
		cancelButton.setOnAction(ActionEvent -> {
			stage.close();
		});

		//确认按钮
		okButton.setOnAction(ActionEvent -> {
			String newName = nameField.getText();
			if (!oldName.equals(newName)) {
				if (fat.hasName(location, newName)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("此位置已包含同名文件/文件夹");
					alert.showAndWait();
				} else {
					if (block.getObject() instanceof Folder) {
						Folder thisFolder = (Folder)block.getObject();
						thisFolder.setFolderName(newName);
						pathMap.get(thisFolder.getPath()).setValue(newName);
						reLoc(location, location, oldName, newName, thisFolder);
					} else {
						((File)block.getObject()).setFileName(newName);
					}
					icon.setText(newName);
				}
			}
			if (block.getObject() instanceof File) {
				File thisFile = ((File)block.getObject());
				int newFlag = toggleGroup.getSelectedToggle().getUserData().hashCode();
				thisFile.setFlag(newFlag);
			}
			stage.close();
		});
	}

	//文件夹改名，为文件更新父文件夹路径
	private void reLoc(String oldP, String newP, String oldN, String newN, Folder folder) {
		String oldLoc = oldP + "\\" + oldN;
		String newLoc = newP + "\\" + newN;
		Path oldPath = fat.getPath(oldLoc);
		fat.replacePath(oldPath, newLoc);
		for (Object child : folder.getChildren()) {
			if (child instanceof File) {
				((File) child).setLocation(newLoc);
			} else {
				Folder nextFolder = (Folder)child;
				nextFolder.setLocation(newLoc);
				if (nextFolder.hasChild()) {
					reLoc(oldLoc, newLoc, nextFolder.getFolderName(),
							nextFolder.getFolderName(), nextFolder);
				}
				else {
					Path nextPath = fat.getPath(oldLoc + "\\" +
							nextFolder.getFolderName());
					String newNext = newLoc + "\\" + nextFolder.getFolderName();
					fat.replacePath(nextPath, newNext);
				}
			}
		}
	}

}
