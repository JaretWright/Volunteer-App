
package views;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Volunteer;

/**
 * FXML Controller class
 *
 * @author jaret_000
 */
public class VolunteerTableViewController implements Initializable {

    @FXML private TableView<Volunteer> volunteerTable;
    @FXML private TableColumn<Volunteer, Integer> volunterIDColumn;
    @FXML private TableColumn<Volunteer, String> firstNameColumn;
    @FXML private TableColumn<Volunteer, String> lastNameColumn;
    @FXML private TableColumn<Volunteer, String> phoneColumn;
    @FXML private TableColumn<Volunteer, LocalDate> birthdayColumn;
    
    
    /**
     * This method will switch to the NewUserView scene when the button is pushed
     */
    public void newVolunteerButtonPushed(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "NewUserView.fxml", "Create New Volunteer");
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // confgure the table columns
        volunterIDColumn.setCellValueFactory(new PropertyValueFactory<Volunteer, Integer>("volunteerID"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Volunteer, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Volunteer, String>("lastName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Volunteer, String>("phoneNumber"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<Volunteer, LocalDate>("birthday"));
        
        try{
            loadVolunteers();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }    
    
    
    /**
     * This method will load the volunteers from the database and load them into 
     * the TableView object
     */
    public void loadVolunteers() throws SQLException
    {
        ObservableList<Volunteer> volunteers = FXCollections.observableArrayList();
        
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/volunteer", "student", "student");
            //2.  create a statement object
            statement = conn.createStatement();
            
            //3.  create the SQL query
            resultSet = statement.executeQuery("SELECT * FROM volunteers");
            
            //4.  create volunteer objects from each record
            while (resultSet.next())
            {
                Volunteer newVolunteer = new Volunteer(resultSet.getString("firstName"),
                                                       resultSet.getString("lastName"),
                                                       resultSet.getString("phoneNumber"),
                                                       resultSet.getDate("birthday").toLocalDate());
                newVolunteer.setVolunteerID(resultSet.getInt("VolunteerID"));
                newVolunteer.setImageFile(new File(resultSet.getString("imageFile")));
                volunteers.add(newVolunteer);
            }
            volunteerTable.getItems().addAll(volunteers);
            
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
    }
    
}
