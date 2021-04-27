package interFace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Bean.DiskBlock;
import Bean.FAT;
import Bean.File;
import Bean.Folder;
import Bean.Path;
import Utiliy.FATUtil;
import autoField.AutoCompleteTextField;
import autoField.AutoCompleteTextFieldBuilder;
import handler.DragWindowHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 主界面
 *
 * @author w
 *
 */
public class MainView {

	private static final String DISK_PATH = "c";

	private FAT fat;// 文件分配表
	private int index;// 临时，获取该文件、文件夹在磁盘的哪个块
	private List<DiskBlock> blockList;// 磁盘
	private String recentPath;// 当前路径
	private Map<Path, TreeItem<String>> pathMap;// 路径

	private Scene scene;
	private HBox workBox, mainBox, lineBox;
	private VBox rightBox, fullBox;

	private FlowPane flowPane;// 文件区
	private Label[] icons;// 文件和文件夹标签

	/*
	 * 导航栏
	 */
	private HBox locBox, menuBox;
	private Label locLabel, locspace, locspace1, locspace2;
	private TextField locField, locserarchField;
	private Button gotoButton, backButton, searchButton, proButton, workButton;
	//////////////////////////////////////////////////////////

	private TreeView<String> treeView;
	private TreeItem<String> rootNode, recentNode;// 根节点、当前节点

	private TableView<DiskBlock> blockTable;// 磁盘使用情况表
	private TableView<File> openedTable;// 打开文件表

	private ObservableList<File> dataOpened;// 被打开文件数据

	private ContextMenu contextMenu, contextMenu2;
	private MenuItem createFileItem, createFolderItem, openItem, renameItem, delItem, propItem;//

	private GridPane gridPane;// 自定义标签行

	private Button minButton, amxButton, closeButton;// 最小化、最大化、关闭

	List<String> finderList = new ArrayList<String>();// 下拉框中数据

	Map<String, Integer> mapfind = new HashMap<String, Integer>();// 当前目录下的文件夹和文件的名字

	private static final String STANDARD_BUTTON_STYLE = "-fx-background-color: rgb(211,211,211);";// 灰色
	private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: white;";// 白色

	private StackPane stackpane;
	private VBox top;

	public PieChart pieChart;

	private MenuBar menuBar;

	private Menu fileMenu;
	private MenuItem newMenuItem, saveMenuItem, exitMenuItem;

	/**
	 * 构造器，输入数据，如果为空，就创建一个FAT 调用初始化界面
	 *
	 * @param stage
	 */
	public MainView(Stage stage) {
		System.out.println("start...");
		pathMap = new HashMap<Path, TreeItem<String>>();
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DISK_PATH))) {
			fat = (FAT) inputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fat == null) {
			fat = new FAT();
		}
		recentPath = "C:";

		initFrame(stage);

	}

	/**
	 * 初始化界面 工作区 自定义标签栏 工作栏 表格
	 *
	 * @param stage
	 */
	private void initFrame(Stage stage) {
		stage.initStyle(StageStyle.TRANSPARENT);
		flowPane = new FlowPane();
		flowPane.setPrefSize(920, 800);
		flowPane.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #ffffff;" + "-fx-border-width:0.5px;");
		flowPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && !contextMenu2.isShowing()) {
				contextMenu.show(flowPane, me.getScreenX(), me.getScreenY());
			} else {
				contextMenu.hide();
			}
		});

		/*
		 * flowPane.
		 * setStyle("-fx-background-image: url(/com/sun/javafx/scene/control/skin/modena/HTMLEditor-Paste.png);"
		 * );
		 */

		initmenubox(stage);
		initContextMenu();
		menuItemSetOnAction();

		initlineMenu(stage);

		initTopBox();

		initTreeView();
		workBox = new HBox(flowPane/* , blockTable */);
		rightBox = new VBox(workBox/* , openedTable */);
		mainBox = new HBox(treeView, rightBox);
		fullBox = new VBox(gridPane, menuBox, locBox, mainBox);

		fullBox.setPrefSize(1200, 800);
		top = new VBox(fullBox);


		scene = new Scene(top);

		stage.setScene(scene);
		stage.setResizable(false);
		Image imageicon = new Image(FATUtil.ICO);
		stage.getIcons().add(imageicon);
		stage.setTitle("模拟磁盘文件系统");

		scene.getStylesheets().addAll("tableView.css", "box.css", "backimage.css", "tableviewclo.css");
		// top.getStyleClass().add("my-pane");背景图片还未实现
		top.setId("pane");

		stage.show();

		/*
		 * finderList.clear(); finderList =
		 * fat.getFilesAndFoldersName(recentPath); initSearchField();
		 */

	}

	/**
	 * 右键菜单栏
	 */
	private void initContextMenu() {
		createFileItem = new MenuItem("新建文件");
		createFolderItem = new MenuItem("新建文件夹");

		openItem = new MenuItem("打开");
		delItem = new MenuItem("删除");
		renameItem = new MenuItem("重命名");
		propItem = new MenuItem("属性");

		contextMenu = new ContextMenu(createFileItem, createFolderItem);
		contextMenu2 = new ContextMenu(openItem, delItem, renameItem, propItem);
	}

	/**
	 * 右键菜单栏选项事件
	 */
	private void menuItemSetOnAction() {
		createFileItem.setOnAction(ActionEvent -> {
			int no = fat.createFile(recentPath);
			if (no == FATUtil.ERROR) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("磁盘容量已满，无法创建");
				alert.showAndWait();
			} else {
				flowPane.getChildren().removeAll(flowPane.getChildren());
				addIcon(fat.getBlockList(recentPath), recentPath);
			}
		});

		createFolderItem.setOnAction(ActionEvent -> {
			int no = fat.createFolder(recentPath);
			if (no == FATUtil.ERROR) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("磁盘容量已满，无法创建");
				alert.showAndWait();
			} else {
				Folder newFolder = (Folder) fat.getBlock(no).getObject();
				Path newPath = newFolder.getPath();
				flowPane.getChildren().removeAll(flowPane.getChildren());
				addIcon(fat.getBlockList(recentPath), recentPath);
				addNode(recentNode, newPath);
			}
		});

		openItem.setOnAction(ActionEvent -> onOpen());

		delItem.setOnAction(ActionEvent -> {
			DiskBlock thisBlock = blockList.get(index);
			if (fat.isOpenedFile(thisBlock)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("文件未关闭");
				alert.showAndWait();
			} else {
				new delView(thisBlock, fat, MainView.this);
				flowPane.getChildren().removeAll(flowPane.getChildren());
				addIcon(fat.getBlockList(recentPath), recentPath);
			}
		});

		renameItem.setOnAction(ActionEvent -> {
			DiskBlock thisBlock = blockList.get(index);
			new RenameView(thisBlock, fat, icons[index], pathMap);
		});

		propItem.setOnAction(ActionEvent -> {
			DiskBlock thisBlock = blockList.get(index);
			new PropertyView(thisBlock, fat, icons[index], pathMap);
		});

	}

	/**
	 * css样式，选中和未选中 两种方法
	 *
	 * @param node
	 */
	private void changeBackgroundOnHoverUsingBinding(Node node) {
		node.styleProperty()
				.bind(Bindings.when(node.hoverProperty()).then(new SimpleStringProperty(HOVERED_BUTTON_STYLE))
						.otherwise(new SimpleStringProperty(STANDARD_BUTTON_STYLE)));
	}

	public void changeBackgroundOnHoverUsingEvents(final Node node) {
		node.setStyle(STANDARD_BUTTON_STYLE);
		node.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				node.setStyle(HOVERED_BUTTON_STYLE);
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				node.setStyle(STANDARD_BUTTON_STYLE);
			}
		});
	}

	/**
	 * 标签栏 报WARNING
	 *
	 * @param stage
	 */
	private void initlineMenu(Stage stage) {
		gridPane = new GridPane();
		gridPane.setStyle("-fx-background-color: rgb(211,211,211);");// WARNING,未改，报211,211,211
		gridPane.setPrefHeight(32);
		gridPane.setPrefWidth(1000);
		gridPane.setAlignment(Pos.CENTER_LEFT);
		DragWindowHandler handler = new DragWindowHandler(stage);
		gridPane.setOnMousePressed(handler);
		gridPane.setOnMouseDragged(handler);
		Label label = new Label("磁盘文件管理系统");
		label.setFont(Font.font(14));
		label.setTextFill(Paint.valueOf("black"));
		ImageView imageView = new ImageView(FATUtil.ICO);
		imageView.setFitHeight(30);
		imageView.setFitWidth(35);
		label.setGraphic(imageView);
		minButton = new Button("");

		amxButton = new Button("");
		closeButton = new Button("");
		minButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0.1;  -fx-background-insets: 0;"
				+ "-fx-max-height: infinity; -fx-border-image-insets: 0; ");

		changeBackgroundOnHoverUsingBinding(minButton);

		amxButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0.1;  -fx-background-insets: 0;"
				+ "-fx-max-height: infinity; -fx-border-image-insets: 0;");

		changeBackgroundOnHoverUsingEvents(amxButton);

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

		/*
		 * closeButton.setOnAction(event -> ((Stage)
		 * (closeButton.getScene().getWindow())).close());
		 */

		/*
		 * 关闭按钮 结束时写入
		 */
		closeButton.setOnAction(e -> {
			try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DISK_PATH))) {
				System.out.println("writing...exit");
				closeAllFile();
				outputStream.writeObject(fat);

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} finally {
				((Stage) closeButton.getScene().getWindow()).close();
			}
		});

		gridPane.addColumn(0, label);
		GridPane.setHgrow(label, Priority.ALWAYS);
		gridPane.addColumn(1, minButton);
		gridPane.addColumn(2, amxButton);
		gridPane.addColumn(3, closeButton);
	}

	/**
	 * 工具栏
	 */
	private void initTopBox() {
		locLabel = new Label("当前目录：");
		locLabel.setStyle("-fx-font-size: 20px");

		locField = new TextField("C:");
		locField.setPrefWidth(400);

		locspace = new Label("       ");
		locspace1 = new Label("    ");
		locspace2 = new Label("");

		locserarchField = new TextField(null);
		locserarchField.setPrefWidth(200);

		locserarchField.setOnMouseClicked(ActionEvent -> {
			finderList.clear();
			finderList = fat.getFilesAndFoldersName(recentPath);
			initSearchField();
		});

		backButton = new Button();
		backButton.setOnAction(ActionEvent -> {
			//System.out.println(recentPath);
			Path backPath = fat.getPath(recentPath).getParent();
			if (backPath != null) {
				List<DiskBlock> blocks = fat.getBlockList(backPath.getPathName());
				flowPane.getChildren().removeAll(flowPane.getChildren());
				addIcon(blocks, backPath.getPathName());
				recentPath = backPath.getPathName();
				/*
				 * finderList.clear(); finderList =
				 * fat.getFilesAndFoldersName(recentPath); initSearchField();
				 */
				recentNode = pathMap.get(backPath);
				locField.setText(recentPath);
			}
		});
		backButton.setGraphic(new ImageView(FATUtil.BACK_IMG));
		backButton.setStyle("-fx-background-color: #ffffff;");
		backButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				backButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		backButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				backButton.setStyle("-fx-background-color: #ffffff;");
			}
		});

		gotoButton = new Button();
		gotoButton.setOnAction(ActionEvent -> {
			String textPath = locField.getText();
			Path gotoPath = fat.getPath(textPath);
			if (gotoPath != null) {
				List<DiskBlock> blocks = fat.getBlockList(textPath);
				flowPane.getChildren().removeAll(flowPane.getChildren());
				addIcon(blocks, textPath);
				recentPath = textPath;
				/*
				 * finderList.clear(); finderList =
				 * fat.getFilesAndFoldersName(recentPath); initSearchField();
				 */
				recentNode = pathMap.get(gotoPath);
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("目录不存在");
				alert.setHeaderText(null);
				alert.show();
				locField.setText(recentPath);
			}
		});
		gotoButton.setGraphic(new ImageView(FATUtil.FORWARD_IMG));
		gotoButton.setStyle("-fx-background-color: #ffffff;");
		gotoButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				gotoButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		gotoButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				gotoButton.setStyle("-fx-background-color: #ffffff;");
			}
		});

		searchButton = new Button("打开");

		searchButton.setOnAction(ActionEvent -> {
			String name = locserarchField.getText();
			//System.out.print(name);
			if (name == null) {
				Alert duplicate = new Alert(AlertType.ERROR, "输入框不能为空!!!");
				duplicate.showAndWait();
			} else {
				try {
					index = mapfind.get(name);
				} catch (NullPointerException e) {
					Alert duplicate = new Alert(AlertType.ERROR, "找不到该文件夹或文件!");
					duplicate.showAndWait();
				} finally {
					onOpen();
				}

			}
		});

		searchButton.setStyle("-fx-background-color: lightgrey;");

		searchButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				searchButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		searchButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				searchButton.setStyle("-fx-background-color: lightgrey;");
			}
		});

		proButton = new Button("属性");

		proButton.setOnAction(ActionEvent -> {
			new pieChartView(fat);

		});

		proButton.setStyle("-fx-background-color: lightgrey;");

		proButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				proButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		proButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				proButton.setStyle("-fx-background-color: lightgrey;");
			}
		});

		workButton = new Button("任务栏");

		workButton.setOnAction(ActionEvent -> {
			new workView(fat);

		});

		workButton.setStyle("-fx-background-color: lightgrey;");

		workButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				workButton.setStyle("-fx-background-color: #1e90ff;");
			}
		});
		workButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				workButton.setStyle("-fx-background-color: lightgrey;");
			}
		});

		locBox = new HBox(backButton, locLabel, locField, gotoButton, locspace, locserarchField, searchButton,
				locspace1, proButton, locspace2, workButton);
		locBox.getStyleClass().add("my-locbox");
		// locBox.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color:
		// #d3d3d3;" + "-fx-border-width:0.5px;");
		locBox.setSpacing(10);
		locBox.setPadding(new Insets(5, 5, 5, 5));

	}

	private void initmenubox(Stage stage) {

		menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(stage.widthProperty());

		fileMenu = new Menu("开始");
		newMenuItem = new MenuItem("属性");
		saveMenuItem = new MenuItem("任务栏");
		exitMenuItem = new MenuItem("关闭");

		newMenuItem.setOnAction(actionEvent -> {
			new pieChartView(fat);
		});

		saveMenuItem.setOnAction(actionEvent -> {
			new workView(fat);
		});

		exitMenuItem.setOnAction(actionEvent -> {
			try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DISK_PATH))) {
				System.out.println("writing...exit");
				closeAllFile();
				outputStream.writeObject(fat);

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} finally {
				((Stage) closeButton.getScene().getWindow()).close();
			}
		});

		fileMenu.getItems().addAll(newMenuItem, saveMenuItem, new SeparatorMenuItem(), exitMenuItem);

		/*
		 * Menu webMenu = new Menu(""); CheckMenuItem htmlMenuItem = new
		 * CheckMenuItem(""); htmlMenuItem.setSelected(true);
		 * webMenu.getItems().add(htmlMenuItem);
		 *
		 * CheckMenuItem cssMenuItem = new CheckMenuItem("");
		 * cssMenuItem.setSelected(true); webMenu.getItems().add(cssMenuItem);
		 *
		 * Menu sqlMenu = new Menu(""); ToggleGroup tGroup = new ToggleGroup();
		 * RadioMenuItem mysqlItem = new RadioMenuItem("");
		 * mysqlItem.setToggleGroup(tGroup);
		 *
		 * RadioMenuItem oracleItem = new RadioMenuItem("");
		 * oracleItem.setToggleGroup(tGroup); oracleItem.setSelected(true);
		 *
		 * sqlMenu.getItems().addAll(mysqlItem, oracleItem, new
		 * SeparatorMenuItem());
		 *
		 * Menu tutorialManeu = new Menu("");
		 * tutorialManeu.getItems().addAll(new CheckMenuItem(""), new
		 * CheckMenuItem(""), new CheckMenuItem(""));
		 */

		/* sqlMenu.getItems().add(tutorialManeu); */

		menuBar.getMenus().addAll(fileMenu/* , webMenu, sqlMenu */);

		menuBox = new HBox(menuBar);
		menuBox.getStyleClass().add("my-locbox");
		menuBox.setSpacing(10);
		menuBox.setPadding(new Insets(5, 5, 5, 5));
	}

	/**
	 * 目录树
	 */
	private void initTreeView() {
		rootNode = new TreeItem<>("C:", new ImageView(FATUtil.DISK_IMG));
		rootNode.setExpanded(true);

		recentNode = rootNode;
		pathMap.put(fat.getPath("C:"), rootNode);

		treeView = new TreeView<String>(rootNode);
		treeView.setPrefWidth(280);
		treeView.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());
		/*
		 * treeView.setStyle("-fx-background-color: #ffffff;" +
		 * "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");
		 */
		treeView.getStyleClass().add("my-tableColumn");
		for (Path path : fat.getPaths()) {
			//System.out.println(path);
			if (path.hasParent() && path.getParent().getPathName().equals(rootNode.getValue())) {
				initTreeNode(path, rootNode);
			}
		}
		addIcon(fat.getBlockList(recentPath), recentPath);
	}

	private void initTreeNode(Path newPath, TreeItem<String> parentNode) {
		TreeItem<String> newNode = addNode(parentNode, newPath);
		if (newPath.hasChild()) {
			for (Path child : newPath.getChildren()) {
				initTreeNode(child, newNode);
			}
		}
	}

	private void addIcon(List<DiskBlock> bList, String path) {
		blockList = bList;
		int n = bList.size();
		icons = new Label[n];
		for (int i = 0; i < n; i++) {
			if (bList.get(i).getObject() instanceof Folder) {
				icons[i] = new Label(((Folder) bList.get(i).getObject()).getFolderName(),
						new ImageView(FATUtil.FOLDER_IMG));
				mapfind.put(((Folder) bList.get(i).getObject()).getFolderName(), i);

			} else {
				icons[i] = new Label(((File) bList.get(i).getObject()).getFileName(), new ImageView(FATUtil.FILE_IMG));
				mapfind.put(((File) bList.get(i).getObject()).getFileName(), i);
			}
			icons[i].setContentDisplay(ContentDisplay.TOP);
			icons[i].setWrapText(true);
			flowPane.getChildren().add(icons[i]);
			icons[i].setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					((Label) event.getSource()).setStyle("-fx-background-color: #f0f8ff;");
				}
			});
			icons[i].setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					((Label) event.getSource()).setStyle("-fx-background-color: #ffffff;");
				}
			});
			icons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					Label src = (Label) event.getSource();
					for (int j = 0; j < n; j++) {
						if (src == icons[j]) {
							index = j;
						}
					}
					if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
						contextMenu2.show(src, event.getScreenX(), event.getScreenY());
					} else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
						onOpen();
					} else {
						contextMenu2.hide();
					}
				}
			});
		}
	}

	private TreeItem<String> addNode(TreeItem<String> parentNode, Path newPath) {

		String pathName = newPath.getPathName();
		String value = pathName.substring(pathName.lastIndexOf('\\') + 1);
		TreeItem<String> newNode = new TreeItem<String>(value, new ImageView(FATUtil.TREE_NODE_IMG));
		newNode.setExpanded(true);
		pathMap.put(newPath, newNode);
		parentNode.getChildren().add(newNode);
		return newNode;
	}

	public void removeNode(TreeItem<String> recentNode, Path remPath) {
		recentNode.getChildren().remove(pathMap.get(remPath));
		pathMap.remove(remPath);
	}

	public TreeItem<String> getRecentNode() {
		return recentNode;
	}

	public void setRecentNode(TreeItem<String> recentNode) {
		this.recentNode = recentNode;
	}

	// public void refreshBlockTable() {
	// dataBlock = FXCollections.observableArrayList(fat.getDiskBlocks());
	// blockTable.setItems(dataBlock);
	// blockTable.refresh();
	// }

	// public void refreshOpenedTable() {
	// dataOpened = FXCollections.observableArrayList(fat.getOpenedFiles());
	// openedTable.setItems(dataOpened);
	// openedTable.refresh();
	// }

	/**
	 * 打开文件 判断文件是否已被打开 或者打开文件数量上限
	 */
	private void onOpen() {
		DiskBlock thisBlock = blockList.get(index);
		for (DiskBlock block : blockList) {
			System.out.println(block);
		}
		if (thisBlock.getObject() instanceof File) {
			if (fat.getOpenedFiles().size() < 5) {
				if (fat.isOpenedFile(thisBlock)) {
					Alert duplicate = new Alert(AlertType.ERROR, "文件已打开");
					duplicate.showAndWait();
				} else {
					fat.addOpenedFile(thisBlock);
					new FileView((File) thisBlock.getObject(), fat, thisBlock);
				}
			} else {
				Alert exceed = new Alert(AlertType.ERROR, "文件打开已到上限");
				exceed.showAndWait();
			}
		} else {
			Folder thisFolder = (Folder) thisBlock.getObject();
			String newPath = thisFolder.getLocation() + "\\" + thisFolder.getFolderName();
			flowPane.getChildren().removeAll(flowPane.getChildren());
			addIcon(fat.getBlockList(newPath), newPath);
			locField.setText(newPath);
			recentPath = newPath;
			/*
			 * finderList.clear(); finderList =
			 * fat.getFilesAndFoldersName(recentPath); initSearchField();
			 */
			recentNode = pathMap.get(thisFolder.getPath());
		}
	}

	/**
	 * 初始化下拉搜索框
	 */
	private void initSearchField() {
		AutoCompleteTextField auto = AutoCompleteTextFieldBuilder.build(locserarchField);
		auto.setCacheDataList(finderList);
		locserarchField.setOnAction(new EventHandler<ActionEvent>() // 设置setOnAction的作用：将当点击enter键时，若文本框中的内容是匹配集合中不存在的，就会将其加入到匹配集合中
		{

			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("");
			}
		});
	}


	/**
	 * 对于TreeView事件的重写
	 * @author Administrator
	 *
	 */
	public final class TextFieldTreeCellImpl extends TreeCell<String> {

		private TextField textField;

		public TextFieldTreeCellImpl() {

			this.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
						if (getTreeItem() != null) {
							String pathName = null;
							for (Map.Entry<Path, TreeItem<String>> entry : pathMap.entrySet()) {
								if (getTreeItem() == entry.getValue()) {
									pathName = entry.getKey().getPathName();
									break;
								}
							}
							List<DiskBlock> fats = fat.getBlockList(pathName);
							flowPane.getChildren().removeAll(flowPane.getChildren());
							addIcon(fats, pathName);
							recentPath = pathName;
							/*
							 * finderList.clear(); finderList =
							 * fat.getFilesAndFoldersName(recentPath);
							 */
							recentNode = getTreeItem();
							locField.setText(recentPath);
							/* initSearchField(); */
						}
					}
				}
			});
		}

		@Override
		public void startEdit() {
			super.startEdit();

			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText((String) getItem());
			setGraphic(getTreeItem().getGraphic());
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setOnKeyReleased((KeyEvent t) -> {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});

		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}

	}

	public void closeAllFile() {
		DiskBlock[] closeblock = fat.getDiskBlocks();
		for (DiskBlock diskBlock : closeblock) {
			if(diskBlock.getObject() instanceof File){

				fat.closeOpenedFile(diskBlock);
			}
		}
	}



}
