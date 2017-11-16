package views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import models.Volunteer;

/**
 * FXML Controller class
 *
 * @author jaret_000
 */
public class NewUserViewController implements Initializable, ControllerClass {

    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField phoneTextField;
    @FXML private DatePicker birthday;
    @FXML private Label errMsgLabel;
    @FXML private Label headerLabel;
    @FXML private ImageView imageView;
    @FXML private CheckBox adminCheckBox;
    
    
    private File imageFile;
    private boolean imageFileChanged;
    private Volunteer volunteer;
    
    //used for the passwords
    @FXML private PasswordField pwField;
    @FXML private PasswordField confirmPwField;
    
    
    
    /**
     * This method will change back to the TableView of volunteers without adding
     * a user.  All data in the form will be lost
     */
    public void cancelButtonPushed(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        
        //check if it is an admin user and go to the table view
        if (SceneChanger.getLoggedInUser().isAdmin())
            sc.changeScenes(event, "VolunteerTableView.fxml", "All Volunteers");
        else
        {
            LogHoursViewController controller = new LogHoursViewController();
            sc.changeScenes(event, "LogHoursView.fxml", "Log Hours", volunteer, controller);
        }
    }
    
    /**
     * When this button is pushed, a FileChooser object is launched to allow the user
     * to browse for a new image file.  When that is complete, it will update the 
     * view with a new image
     */
    public void chooseImageButtonPushed(ActionEvent event)
    {
        //get the Stage to open a new window (or Stage in JavaFX)
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        //Instantiate a FileChooser object
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        
        //filter for .jpg and .png
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);
        
        //Set to the user's picture directory or user directory if not available
        String userDirectoryString = System.getProperty("user.home")+"\\Pictures";
        File userDirectory = new File(userDirectoryString);
        
        //if you cannot navigate to the pictures directory, go to the user home
        if (!userDirectory.canRead())
            userDirectory = new File(System.getProperty("user.home"));
        
        fileChooser.setInitialDirectory(userDirectory);
        
        //open the file dialog window
        File tmpImageFile = fileChooser.showOpenDialog(stage);
        
        if (tmpImageFile != null)
        {
            imageFile = tmpImageFile;
        
            //update the ImageView with the new image
            if (imageFile.isFile())
            {
                try
                {
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(img);
                    imageFileChanged = true;
                }
                catch (IOException e)
                {
                    System.err.println(e.getMessage());
                }
            }
        }
        
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        birthday.setValue(LocalDate.now().minusYears(18));
        
        imageFileChanged = false; //initially the image has not changed, use the default
        
        errMsgLabel.setText("");    //set the error message to be empty to start
        
        //load the defautl image for the avatar
        try{
            imageFile = new File("./src/images/defaultPerson.png");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);
            
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }    
    
    
    /**
     * This method will read from the scene and try to create a new instance of a Volunteer.
     * If a volunteer was successfully created, it is updated in the database.
     */
    public void saveVolunteerButtonPushed(ActionEvent event)
    {
        if (validPassword())
        {
            try
            {
                if (volunteer != null) //we need to edit/update an existing volunteer
                {
                    updateVolunteer();
                    volunteer.updateVolunteerInDB();
                }
                else    //we need to create a new volunteer
                {
                    if (imageFileChanged) //create a Volunteer with a custom image
                    {
                        volunteer = new Volunteer(firstNameTextField.getText(),lastNameTextField.getText(),
                                                        phoneTextField.getText(), birthday.getValue(), imageFile, 
                                                        adminCheckBox.isSelected(),pwField.getText());
                    }
                    else  //create a Volunteer with a default image
                    {
                        volunteer = new Volunteer(firstNameTextField.getText(),lastNameTextField.getText(),
                                                        phoneTextField.getText(), birthday.getValue(), 
                                                        pwField.getText(),
                                                        adminCheckBox.isSelected());
                    }
                    errMsgLabel.setText("");    //do not show errors if creating Volunteer was successful
                    volunteer.insertIntoDB();    
                }

                SceneChanger sc = new SceneChanger();
                sc.changeScenes(event, "VolunteerTableView.fxml", "All Volunteers");
            }
            catch (Exception e)
            {
                errMsgLabel.setText(e.getMessage());
            }
        }
    }
    
    /**
     * This method will validate that the passwords match
     * 
     */
    public boolean validPassword()
    {
        if (pwField.getText().length() < 5)
        {
            errMsgLabel.setText("Passwords must be greater than 5 characters in length");
            return false;
        }
        
        if (pwField.getText().equals(confirmPwField.getText()))
            return true;
        else
            return false;
    }
    
    /**
     * This method will update the view with a Volunteer object preloaded for an edit
     * @param volunteer 
     */
    @Override
    public void preloadData(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.firstNameTextField.setText(volunteer.getFirstName());
        this.lastNameTextField.setText(volunteer.getLastName());
        this.birthday.setValue(volunteer.getBirthday());
        this.phoneTextField.setText(volunteer.getPhoneNumber());
        this.headerLabel.setText("Edit Volunteer");
        
        //load the image 
        try{
            String imgLocation = ".\\src\\images\\" + volunteer.getImageFile().getName();
            imageFile = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(img);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * This method will read from the GUI fields and update the volunteer object
     */
    public void updateVolunteer() throws IOException
    {
        volunteer.setFirstName(firstNameTextField.getText());
        volunteer.setLastName(lastNameTextField.getText());
        volunteer.setPhoneNumber(phoneTextField.getText());
        volunteer.setBirthday(birthday.getValue());
        volunteer.setImageFile(imageFile);
        volunteer.copyImageFile();
    }
   }
