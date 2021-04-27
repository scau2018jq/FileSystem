package interFace;

import java.util.Map;

import Bean.DiskBlock;
import Bean.FAT;
import Bean.File;
import Bean.Folder;
import Bean.Path;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 重命名界面
 * @author w
 *
 */
public class RenameView {

	private DiskBlock block;//磁盘
	private FAT fat;//文件分配表
	private Label icon;
	private Map<Path, TreeItem<String>> pathMap;
	private Stage stage;
	private Scene scene;
	private HBox hBox;
	private TextField nameField;
	private Button okButton, cancelButton;
	private String oldName, location;

	public RenameView(DiskBlock block, FAT fat, Label icon, Map<Path, TreeItem<String>> pathMap) {
		this.block = block;
		this.fat = fat;
		this.icon = icon;
		this.pathMap = pathMap;
		showView();
	}


	private void showView() {
		if (block.getObject() instanceof Folder) {
			oldName = ((Folder) block.getObject()).getFolderName();
			location = ((Folder) block.getObject()).getLocation();
		} else {
			oldName = ((File) block.getObject()).getFileName();
			location = ((File) block.getObject()).getLocation();
		}

		nameField = new TextField(oldName);

		//确认按钮
		okButton = new Button("确认");
		okButton.setStyle("-fx-background-color:#d3d3d3;");
		okButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		okButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});
		okButton.setOnAction(ActionEvent -> {
			String newName = nameField.getText();
			if (!newName.equals(oldName)) {
				if (fat.hasName(location, newName)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("此位置已包含同名文件/文件夹");
					alert.showAndWait();
				} else {
					if (block.getObject() instanceof Folder) {
						Folder thisFolder = (Folder) block.getObject();
						thisFolder.setFolderName(newName);
						pathMap.get(thisFolder.getPath()).setValue(newName);
						reLoc(location, location, oldName, newName, thisFolder);
					} else {
						((File) block.getObject()).setFileName(newName);
					}
					icon.setText(newName);
				}
			}
			stage.close();
		});

		//取消按钮
		cancelButton = new Button("取消");
		cancelButton.setStyle("-fx-background-color:#d3d3d3;");
		cancelButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cancelButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		cancelButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cancelButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});
		cancelButton.setOnAction(ActionEvent -> stage.close());

		hBox = new HBox(nameField, okButton, cancelButton);
		hBox.setSpacing(5);
		hBox.setPadding(new Insets(5));
		hBox.setStyle("-fx-background-color:#d3d3d3");

		scene = new Scene(hBox);
		stage = new Stage();
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	//文件夹改名，为文件夹和子节点更新路径
	private void reLoc(String oldP, String newP, String oldN,
			String newN, Folder folder) {
		String oldLoc = oldP + "\\" + oldN;
		String newLoc = newP + "\\" + newN;
		Path oldPath = fat.getPath(oldLoc);
		fat.replacePath(oldPath, newLoc);
		for (Object child : folder.getChildren()) {
			if (child instanceof File) {
				((File) child).setLocation(newLoc);
			} else {
				Folder nextFolder = (Folder) child;
				nextFolder.setLocation(newLoc);
				if (nextFolder.hasChild()) {
					reLoc(oldLoc, newLoc, nextFolder.getFolderName(),
							nextFolder.getFolderName(), nextFolder);
				} else {
					Path nextPath = fat.getPath(oldLoc + "\\" +
							nextFolder.getFolderName());
					fat.replacePath(nextPath, newLoc + "\\" +
							nextFolder.getFolderName());
				}
			}
		}
	}

}
