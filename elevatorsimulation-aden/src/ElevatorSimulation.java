
import java.util.logging.Level;

import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc
/*
 * Class belongs to: Joseph
 * Reviewed by: Nikash
 * Additional Comments:
 */

/**
 * The Class ElevatorSimulation.
 */
public class ElevatorSimulation extends Application {
	
	/**  Instantiate the GUI fields. */
	private ElevatorSimController controller;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num elevators. */
	private final int NUM_ELEVATORS;
	//private int currFloor;
	//private int passengers;
	/** The pane. */
	//private int time;
	private BorderPane pane;
	
	/** The gp. */
	private GridPane gp;
	
	/** The t. */
	private Timeline t;
	
	/** The building grid height. */
	private int buildingGridHeight;
	
	/** The building grid width. */
	private int buildingGridWidth;
	
	/** The steps label. */
	private Label stepsLabel;
	

	/**  Local copies of the states for tracking purposes. */
	private final int STOP = Elevator.STOP;
	
	/** The mvtoflr. */
	private final int MVTOFLR = Elevator.MVTOFLR;
	
	/** The opendr. */
	private final int OPENDR = Elevator.OPENDR;
	
	/** The offld. */
	private final int OFFLD = Elevator.OFFLD;
	
	/** The board. */
	private final int BOARD = Elevator.BOARD;
	
	/** The closedr. */
	private final int CLOSEDR = Elevator.CLOSEDR;
	
	/** The mv1flr. */
	private final int MV1FLR = Elevator.MV1FLR;
	
	/** The step by. */
	private final int STEP_BY = 10;
	
	/** The step duration. */
	private final int STEP_DURATION = 100;
	
	/** The grid cell height. */
	private final int GRID_CELL_HEIGHT = 70;
	
	/** The grid cell width. */
	private final int GRID_CELL_WIDTH = 70;
	
	/** The grid queue width. */
	private final int GRID_QUEUE_WIDTH = 200;
	
	/** The steps label width. */
	private final double STEPS_LABEL_WIDTH = 100.0;
	
	/** The floor label grid column. */
	private final int FLOOR_LABEL_GRID_COLUMN = 0;
	
	/** The first elevator grid column. */
	private final int FIRST_ELEVATOR_GRID_COLUMN = 2;
	
	/** The building border width. */
	private final int BUILDING_BORDER_WIDTH = 5;
	
	/** The passenger queue grid column. */
	private final int PASSENGER_QUEUE_GRID_COLUMN;
	
	/** The building right edge column. */
	private final int BUILDING_RIGHT_EDGE_COLUMN;
	
	/** The step by button. */
	private Button stepByButton;
	
	/** The configured step by. */
	private int configuredStepBy = STEP_BY;

	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		//currFloor = controller.getCurrentFloor(0); // assumes only one elevator in the building!
		buildingGridHeight = NUM_FLOORS;
		buildingGridWidth = NUM_ELEVATORS + 4; // add a column for the floor number, passenger queue, each elevator, and 2 extra columns on the left and right
		PASSENGER_QUEUE_GRID_COLUMN = buildingGridWidth - 1;
		BUILDING_RIGHT_EDGE_COLUMN = buildingGridWidth - 2;
	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		pane = new BorderPane();
		gp = new GridPane();
		setGridPaneConstraints();
		updateBuildingView();
		pane.setCenter(gp);
		gp.setAlignment(Pos.CENTER);
        pane.setBottom(addButtons()); 
        initTimeline();
 		Scene scene = new Scene(pane,800,150 + (buildingGridHeight * GRID_CELL_HEIGHT));
		primaryStage.setScene(scene);
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();		
	}
		
	/**
	 * Adds the buttons.
	 *
	 * @return the h box
	 */
	private HBox addButtons() {
		HBox btnBox = new HBox(15);
		Label stepByLabel = new Label("Configure Step Count:");
		TextField stepByField = new TextField("" + configuredStepBy);
		stepByField.setPrefWidth(40);
		Button step = new Button("Step by 1");
		stepByButton = new Button("Step by " + configuredStepBy);
		Button run = new Button("Run");
		Button logButton = new Button("Enable Logging");
		stepsLabel = new Label("Step Count: 0");
		stepsLabel.setMinWidth(STEPS_LABEL_WIDTH);
		stepByField.setOnKeyReleased(e -> setStepBy(stepByField.getText()));
		step.setOnAction(e -> controller.stepSim());
		stepByButton.setOnAction(e -> {t.setCycleCount(configuredStepBy); t.play();});
		run.setOnAction(e -> {t.setCycleCount(Animation.INDEFINITE); t.play();});
		logButton.setOnAction(e -> controller.enableLogging());
        btnBox.getChildren().addAll(stepByLabel, stepByField, stepByButton, step, run, logButton, stepsLabel);
        btnBox.setAlignment(Pos.CENTER);
        return btnBox;
	}
	
	/**
	 * Sets the number of steps to step by from an inputted text string.
	 *
	 * @param text the new step by
	 */
	private void setStepBy(String text) {
		try {
			configuredStepBy = Integer.parseInt(text);
			stepByButton.setText("Step by " + configuredStepBy);
		}
		catch (NumberFormatException ex) {
			// ignore
		}
	}
	
	/**
	 * Sets the grid pane constraints.
	 */
	private void setGridPaneConstraints() {
		for (int i = 0; i < buildingGridWidth-1; i++) 
			gp.getColumnConstraints().add(new ColumnConstraints(GRID_CELL_WIDTH));
		gp.getColumnConstraints().add(new ColumnConstraints(GRID_QUEUE_WIDTH));

		for (int i = 0; i < buildingGridHeight; i++) 
			gp.getRowConstraints().add(new RowConstraints(GRID_CELL_HEIGHT));
	}
	
	/**
	 * Starting point for all label-based cells in the elevator.
	 *
	 * @return the label
	 */
	private Label makeStarterCell() {
		Label cell = new Label("");
		cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		cell.setAlignment(Pos.CENTER);
		return cell;
	}
	
	/**
	 * Helper to build up a style.
	 *
	 * @param backgroundColor the background color
	 * @param textFillColor the text fill color
	 * @param topBorder the top border
	 * @param rightBorder the right border
	 * @param bottomBorder the bottom border
	 * @param leftBorder the left border
	 * @return the string
	 */
	private String makeCellStyle(String backgroundColor, String textFillColor, int topBorder, int rightBorder, int bottomBorder, int leftBorder) {
		String style = "-fx-border-color: black; -fx-border-style: solid; -fx-border-width: " + topBorder + " " + rightBorder + " " + bottomBorder + " " + leftBorder + ";";
		if (!backgroundColor.isEmpty())
			style += "-fx-background-color:" + backgroundColor + ";";
		if (!textFillColor.isEmpty())
			style += "-fx-text-fill:" + textFillColor + ";";
		return style;
	}
	
	/**
	 * Make a polygon pointing in the given direction.
	 *
	 * @param direction the direction
	 * @return the polygon
	 */
	private Polygon makeArrow(int direction) {
		Polygon arrow = new Polygon();
		arrow.getPoints().addAll(5.0,20.0,25.0,20.0,15.0,20-10*Math.pow(3,0.5));
		arrow.setStrokeWidth(2);
		if (direction == Elevator.UP) { // elevator.getDirection(); calling on object not constant intrinsic to class, needs to be implemented
			arrow.setStroke(Color.GREEN);
			arrow.setFill(Color.GREEN);				
		} else {
			arrow.setStroke(Color.RED);
			arrow.setFill(Color.RED);
			arrow.setRotate(180);
		}
		return arrow;
	}
	
	/**
	 * Set up an elevator cell in the building grid.
	 *
	 * @param floor the floor
	 * @param elevatorNumber the elevator number
	 * @return the node
	 */
	private Node initElevatorCell(int floor, int elevatorNumber) {
		Label cell = makeStarterCell();
		Node elevatorNode = cell;
		String backgroundColor = "darkGray";
		String textFillColor = "";
		int topBorder = (floor == this.NUM_FLOORS-1) ? BUILDING_BORDER_WIDTH : 0;
		int elevatorFloor = controller.getCurrentFloor(elevatorNumber);
		if (elevatorFloor == floor) {
			backgroundColor = "blue";
			textFillColor = "white";
			cell.setText("" + controller.getCurrentPassengers(elevatorNumber) + "\n" + controller.getStateString(elevatorNumber));
		} else {
			int direction = controller.getCurrentDirection(elevatorNumber);
			Polygon arrow = makeArrow(direction);
			StackPane sp = new StackPane();
			sp.getChildren().add(arrow);
			elevatorNode = sp;
		}
		elevatorNode.setStyle(makeCellStyle(backgroundColor, textFillColor, topBorder, 0, 0, 0));
		return elevatorNode;
	}
	
	/**
	 * Set up a floor label cell in the building grid.
	 *
	 * @param floor the floor
	 * @return the node
	 */
	private Node initFloorLabelCell(int floor) {
		Label cell = makeStarterCell();
		cell.setText("" + (floor + 1));
		cell.setStyle(makeCellStyle("", "", 0, BUILDING_BORDER_WIDTH, 0, 0));
		return cell;
	}
	
	/**
	 * Set up a passenger queue cell in the building grid.
	 *
	 * @param floor the floor
	 * @return the node
	 */
	private Node initPassengerQueueCell(int floor) {
		Label cell = makeStarterCell();
		cell.setStyle(makeCellStyle("", "", 0, 0, 0, 0) + "-fx-padding:0 0 0 10;");
		cell.setText(controller.getQueueString(floor, Elevator.UP) + "\n" + controller.getQueueString(floor, Elevator.DOWN));
		cell.setAlignment(Pos.CENTER_LEFT);
		return cell;
	}
	
	/**
	 * Set up an empty building cell.
	 *
	 * @param firstFloor the first floor
	 * @return the node
	 */
	private Node initEmptyBuildingCell(boolean firstFloor) {
		Label cell = makeStarterCell();
		cell.setStyle(makeCellStyle("white", "", BUILDING_BORDER_WIDTH, 0, firstFloor ? BUILDING_BORDER_WIDTH : 0, 0));
		return cell;		
	}
	
	/**
	 * Set up an empty building cell on the right edge of the building.
	 *
	 * @param firstFloor the first floor
	 * @return the node
	 */
	private Node initRightSideBuildingCell(boolean firstFloor) {
		Label cell = makeStarterCell();
		cell.setStyle(makeCellStyle("white", "", BUILDING_BORDER_WIDTH, BUILDING_BORDER_WIDTH, firstFloor ? BUILDING_BORDER_WIDTH : 0, 0));
		return cell;
	}
	
	/**
	 * Set up one cell in the building grid (all types).
	 *
	 * @param floor the floor
	 * @param column the column
	 * @return the node
	 */
	private Node initBuildingCell(int floor, int column) {
		Node cell = null;
		boolean firstFloor = (floor == 0);		
		if (column == FLOOR_LABEL_GRID_COLUMN) {
			cell = initFloorLabelCell(floor);
		} else if (column >= FIRST_ELEVATOR_GRID_COLUMN && column < (FIRST_ELEVATOR_GRID_COLUMN + NUM_ELEVATORS)) {
			cell = initElevatorCell(floor, column - FIRST_ELEVATOR_GRID_COLUMN);
		} else if (column == BUILDING_RIGHT_EDGE_COLUMN) {
			cell = initRightSideBuildingCell(firstFloor);
		} else if (column == PASSENGER_QUEUE_GRID_COLUMN) {
			cell = initPassengerQueueCell(floor);
		} else {
			cell = initEmptyBuildingCell(firstFloor);
		}
		return cell;
	}
	
	/**
	 * Set up the grid to represent the building with its floors, elevators, etc.
	 * Used for both initial setup and to update the UI after time passes.
	 */
	private void updateBuildingView() {
		gp.getChildren().clear();
		for (int floor = 0; floor < this.NUM_FLOORS; floor++) {
			for (int column = 0; column < this.buildingGridWidth; column++) {
				Node cell = initBuildingCell(floor, column);
				
				gp.add(cell, column, this.buildingGridHeight - floor - 1);
			}
		}
		gp.setStyle("-fx-padding:50;");
	}
	
	/**
	 * Creates a new timeline. Configure the timeline as follows:
	 *      create a new KeyFrame with Duration = duration and
	 *      	                       stepSim() as the event handler
	 *      set the timeline cycle count to Animation.INDEFINITE
	 */
	private void initTimeline() {
		t = new Timeline(new KeyFrame(Duration.millis(STEP_DURATION), ae -> controller.stepSim()));
		t.setCycleCount(Animation.INDEFINITE);
	}
		
	
	/**
	 * Called by the controller to update the UI.
	 */
	public void updateGUI() {
		if (controller.checkIfSimulationHasEnded())
			stepsLabel.setText("Simulation Complete");
		else
			stepsLabel.setText("Step Count: " + controller.getStepCount());
			
		updateBuildingView();
	}
	
	
	/**
	 * Called by the controller to end the simulation.
	 */
	public void endSimulation() {
		t.stop();
	}

	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		Application.launch(args);
	}


}
