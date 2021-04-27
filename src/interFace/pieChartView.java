package interFace;

import java.util.List;

import Bean.DiskBlock;
import Bean.FAT;
import Bean.File;
import Utiliy.FATUtil;
import handler.DragWindowHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 饼状图
 * @author Administrator
 *
 */

public class pieChartView {

	private MainView mainView;//主界面
	private TableView<DiskBlock> blockTable;// 磁盘使用情况表
	private List<DiskBlock> blockList;
	private FAT fat;
	private HBox hbox;
	private Stage stage;
	private Scene scene;
	private Image ico;
	private VBox vbox;
	private ObservableList<DiskBlock> dataBlock;// 磁盘块数据
	private GridPane gridPane;
	private Button minButton,amxButton,closeButton;
	public pieChartView(FAT fat){
		this.fat = fat;
		showView();
	}

	private void showView() {
		initlineMenu(stage);
		dataBlock = FXCollections.observableArrayList(fat.getDiskBlocks());
	    stage = new Stage();
		PieChart pieChart = new PieChart();
	    pieChart.setData(getChartData());
	    pieChart.setTitle("磁盘空间");
	    pieChart.setLegendSide(Side.LEFT);
	    pieChart.setClockwise(false);
	    pieChart.setLabelsVisible(false);
	    initTables();

		final Label caption = new Label("");
	    caption.setTextFill(Color.WHITE);
	    caption.setStyle("-fx-font: 24 arial;");
	    for (final PieChart.Data data : pieChart.getData()) {
	        data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
	            new EventHandler<MouseEvent>() {
	                @Override public void handle(MouseEvent e) {
	                    caption.setTranslateX(e.getSceneX());
	                    caption.setTranslateY(e.getSceneY());
	                    caption.setText(String.valueOf(data.getPieValue()) + "%");
	                    caption.setVisible(true);
	                 }
	            });
	    }
	    hbox = new HBox(blockTable,pieChart);
	    vbox = new VBox(gridPane,hbox);
	    scene = new Scene(vbox);

	    stage.initStyle(StageStyle.TRANSPARENT);
	    stage.setScene(scene);
	    DragWindowHandler handler = new DragWindowHandler(stage);
		gridPane.setOnMousePressed(handler);
		gridPane.setOnMouseDragged(handler);
	    ico = new Image(FATUtil.FOLDER_IMG);
		stage.getIcons().add(ico);
		stage.setAlwaysOnTop(true);
		scene.getStylesheets().addAll("tableView.css", "box.css", "backimage.css", "tableviewclo.css");


		stage.show();
	}

	public ObservableList<Data> getChartData() {
		ObservableList<Data> answer = FXCollections.observableArrayList();
	    answer.addAll(new PieChart.Data("已使用磁盘", getblock()),
	            new PieChart.Data("空闲磁盘",(128-getblock()))
	            );
	    return answer;
	}

	public int getblock(){
		int j = 0;
		for(int i = 0 ; i < 128 ; i++){
			if(dataBlock.get(i).getIndex() == 255){
				j++;
			}else if(dataBlock.get(i).getIndex() == 0){
				break;
			}
		}
		return j++;
	}

	/**
	 * 表格
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initTables() {
		blockTable = new TableView<DiskBlock>();

		blockTable.getStyleClass().add("my-viewtable");

		dataBlock = FXCollections.observableArrayList(fat.getDiskBlocks());

		TableColumn noCol = new TableColumn("磁盘块");

		noCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("noP"));
		noCol.setSortable(false);
		noCol.setMaxWidth(80);
		noCol.setResizable(false);

		TableColumn indexCol = new TableColumn("值");
		// indexCol.getStyleClass().add("my-tableColumn");
		indexCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("indexP"));
		indexCol.setSortable(false);
		indexCol.setMaxWidth(50);
		indexCol.setResizable(false);

		TableColumn typeCol = new TableColumn("类型");
		// typeCol.getStyleClass().add("my-tableColumn");
		typeCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("typeP"));
		typeCol.setSortable(false);
		typeCol.setMaxWidth(100);
		typeCol.setResizable(false);

		TableColumn objCol = new TableColumn("名称");
		// objCol.getStyleClass().add("my-tableColumn");
		objCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("objectP"));
		objCol.setSortable(false);
		objCol.setMinWidth(133);
		objCol.setResizable(false);

		blockTable.setItems(dataBlock);
		blockTable.getColumns().addAll(noCol, indexCol, typeCol, objCol);
		blockTable.setEditable(false);
		blockTable.setPrefWidth(345);
	}


	/**
	 * 标签栏
	 * @param stage
	 */
	private void initlineMenu(Stage stage) {
		gridPane = new GridPane();
		gridPane.setStyle("-fx-background-color: rgb(211 211 211);");
		gridPane.setPrefHeight(32);
		gridPane.setPrefWidth(400);
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


}
