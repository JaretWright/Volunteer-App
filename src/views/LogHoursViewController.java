
package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import models.Volunteer;

/**
 * FXML Controller class
 *
 * @author jaret_000
 */
public class LogHoursViewController implements Initializable, ControllerClass {
    @FXML    private DatePicker datePicker;
    @FXML    private Spinner hoursWorkedSpinner;
    @FXML    private Label volunteerIDLabel;
    @FXML    private Label firstNameLabel;
    @FXML    private Label lastNameLabel;
    @FXML    private Label errMsgLabel;
    @FXML    private Button backButton;

    private Volunteer volunteer;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0,18,8);
        hoursWorkedSpinner.setValueFactory(valueFactory);
        
        //change the text on the button if it is not an administrative user
        if (!SceneChanger.getLoggedInUser().isAdmin())
            backButton.setText("Edit");
    }    

    @Override
    public void preloadData(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteerIDLabel.setText(Integer.toString(volunteer.getVolunteerID()));
        firstNameLabel.setText(volunteer.getFirstName());
        lastNameLabel.setText(volunteer.getLastName());
        datePicker.setValue(LocalDate.now());
        errMsgLabel.setText("");
    }
    
    /**
     * This method will read/validate the inputs and store the information
     * in the hoursWorked table
     */
    public void saveButtonPushed(ActionEvent event)
    {
        try{
            volunteer.logHours(datePicker.getValue(), (int) hoursWorkedSpinner.getValue());           
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            errMsgLabel.setText(e.getMessage());
        }
    }
    
    
     /**
     * This will return the user to the table of all volunteers
     */
    public void cancelButttonPushed(ActionEvent event) throws IOException
    {
        //if this is an admin user, go back to the table of volunteers
        SceneChanger sc = new SceneChanger();
        
        if (SceneChanger.getLoggedInUser().isAdmin())
            sc.changeScenes(event, "VolunteerTableView.fxml", "All Volunteers");
        else
        {
            NewUserViewController controller = new NewUserViewController();
            sc.changeScenes(event, "NewUserView.fxml", "Edit", volunteer, controller);
        }
            
    }
}
