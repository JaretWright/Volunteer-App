package volunteerapp;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import models.Volunteer;

/**
 *
 * @author jaret_000
 */
public class VolunteerApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        Volunteer volunteer = new Volunteer("Wilma","Flintstone","651-555-1234", LocalDate.of(2002, Month.MARCH, 12), 
                                    new File("./src/images/Fred_Flintstone.png"));
        System.out.printf("Our volunteer is: %s%n", volunteer);
        
        volunteer.insertIntoDB();
    }
    
}
