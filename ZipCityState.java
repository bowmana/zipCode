/*Alex Bowman
 * HW#11
 * Professor Silvestri
 * 5/7/20
 */
package zipToCity;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.sql.*;

public class ZipCityState extends Application {

	private final TextField cityText1 = new TextField();
	private final TextField stateText1 = new TextField();
	private final TextField zipText2 = new TextField();
	private final TextField cityText2 = new TextField();
	private final TextField stateText2 = new TextField();
	private final Button zToCbtn = new Button("Zip To City");
	private final Button cToZbtn = new Button("City to Zip");
	private final TextField zipf = new TextField("Zip Code(s)");
	private final TextField statustf = new TextField();
	private final TopRow tr = new TopRow();
	private final CenterRow cr = new CenterRow();
	private final BottomRow br = new BottomRow();

	private PreparedStatement preparedStatement;
	Connection conn;

	@Override
	public void start(Stage primaryStage) {
		DataBase();
		Scene scene = new Scene(new AppGUI(), 800, 350);
		scene.getStylesheets().add("zipStyler.css");
		primaryStage.setTitle("ZipCode Translation System");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(true);

	}

	private class AppGUI extends BorderPane {
		private AppGUI() {
			this.setTop(tr);
			this.setCenter(cr);
			this.setBottom(br);
			zToCbtn.setOnAction(e -> CheckZip());
			cToZbtn.setOnAction(e -> CheckCityState());

		}
	}

	private class TopRow extends VBox {
		public TopRow() {
			HBox hbox = new HBox();
			hbox.setPadding(new Insets(15, 10, 15, 10));
			hbox.setSpacing(15);
			hbox.setAlignment(Pos.CENTER);

			this.setAlignment(Pos.CENTER);

			Line line = new Line();
			line.setStrokeWidth(3);
			line.setEndX(800.0);

			zipText2.setPrefWidth(80);

			Label ZipCode = new Label("Zip Code:");
			Label City = new Label("City:");
			Label State = new Label("State:");

			stateText1.setEditable(false);
			stateText1.setMouseTransparent(true);
			stateText1.setPrefWidth(50);

			cityText1.setPrefWidth(200);
			cityText1.setEditable(false);
			cityText1.setMouseTransparent(true);

			hbox.getChildren().addAll(ZipCode, zipText2, zToCbtn, City, cityText1, State, stateText1);
			this.getChildren().addAll(hbox, line);

		}
	}

	private class CenterRow extends VBox {
		public CenterRow() {
			HBox hbox = new HBox();
			hbox.setPadding(new Insets(15, 10, 15, 10));
			hbox.setSpacing(15);
			hbox.setAlignment(Pos.CENTER);

			this.setAlignment(Pos.CENTER);

			stateText2.setPrefWidth(50);

			Line line = new Line();
			line.setStrokeWidth(3);
			line.setEndX(800.0);

			Label City = new Label("City:");
			Label State = new Label("State:");

			zipf.setMouseTransparent(true);
			zipf.setEditable(false);
			zipf.setPrefHeight(100);
			zipf.setPrefWidth(140);

			hbox.getChildren().addAll(City, cityText2, State, stateText2, cToZbtn, zipf);
			this.getChildren().addAll(hbox, line);

		}
	}

	private class BottomRow extends VBox {
		public BottomRow() {
			HBox hbox = new HBox();
			hbox.setPadding(new Insets(15, 10, 15, 10));
			hbox.setAlignment(Pos.CENTER);

			statustf.setEditable(false);
			statustf.setMouseTransparent(true);
			statustf.setPrefWidth(500);

			Label Status = new Label("Status: ");

			hbox.getChildren().addAll(Status, statustf);
			this.getChildren().add(hbox);

		}
	}

	private void CheckZip() {
		if (zipText2.getText().matches("(\\d{5})")) {
			zipDatabase();
			statustf.setText("Valid Zip Code");
		} else {
			statustf.setText("Invalid Zip Code");

		}
	}

	private void CheckCityState() {
		if (cityText2.getText().matches("^[a-zA-Z\\s-]+$")) {
			if (stateText2.getText().matches("^[A-Za-z]{2}"))
				cityDatabase();
			statustf.setText("Valid City/State");
		} else {
			statustf.setText("Invalid City/State");

		}
	}

	private void DataBase() {
			String DATABASE = "silvestri";
			String USERNAME = "readonly";
			String PASSWORD = "readonly";
			//String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://cs.stcc.edu/" + DATABASE + "?user=" + USERNAME + "&password=" + PASSWORD;
			String zip = zipText2.getText();
			String state = stateText2.getText();
			String city = cityText2.getText();

			try {
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("Driver loaded successfully");

				Connection connection = DriverManager.getConnection(url);
				System.out.println("Database Connected");
				String zipCode = ("Select city, state FROM Zipcodes WHERE zipcode= " + zip);
				String City = ("Select zipcode from Zipcodes where state ='" + state + "' && city ='" + city + "';");

				preparedStatement = connection.prepareStatement(zipCode);
				preparedStatement = connection.prepareStatement(City);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void zipDatabase() {
			try {
				String zip = zipText2.getText();
				String zipCode = ("Select city, state FROM Zipcodes WHERE zipcode= " + zip);
				ResultSet rset = preparedStatement.executeQuery(zipCode);
				if (rset.next()) {
					cityText1.setText(rset.getString(1));
					stateText1.setText(rset.getString(2));
					statustf.setText("City and State were retrieved from Database");
				} else {
					statustf.setText("Error Database Connection Fail");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		// City & State to Database
		private void cityDatabase() {
			try {
				String state = stateText2.getText();
				String city = cityText2.getText();
				String City = ("Select zipcode from Zipcodes where state ='" + state + "' && city ='" + city + "';");
				ResultSet rset = preparedStatement.executeQuery(City);
				if (rset.next()) {
					String zipCode = rset.getString("zipcode");
					zipf.setText(zipCode + ", " + System.lineSeparator());
					while (rset.next()) {
						zipCode = rset.getString("zipcode");
						zipf.setText(zipf.getText() + zipCode + ", " + System.lineSeparator());
					}
				} else {
					statustf.setText("Error Invalid City or State");
					zipf.setText("No Zip Codes Found");
				}
			} catch (Exception ex) {
				ex.getMessage();
			}
		}
	}
