
package views;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author jaret_000
 */
public class MonthlyHoursViewController implements Initializable {

    @FXML    private BarChart<?, ?> barChart;
    @FXML    private CategoryAxis months;
    @FXML    private NumberAxis hoursWorked;
    
    private XYChart.Series currentYearSeries, previousYearSeries;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentYearSeries = new XYChart.Series<>();
        previousYearSeries = new XYChart.Series<>();
        
        //barChart.setTitle("Hours Worked");
        months.setLabel("Months");
        hoursWorked.setLabel("Hours worked");
        
        currentYearSeries.setName(Integer.toString(LocalDate.now().getYear()));
        previousYearSeries.setName(Integer.toString(LocalDate.now().getYear()-1));
        
        try{
            populateSeriesFromDB();
        }
        catch (SQLException e)
        {
            System.err.println(e);
        }
        
        barChart.getData().addAll(previousYearSeries);
        barChart.getData().addAll(currentYearSeries);
    }    
    
    /**
     * This will read the user data from the database and populate the series
     */
    public void populateSeriesFromDB() throws SQLException
    {
        //get the results from the database
        Connection conn=null;
        Statement statement=null;
        ResultSet resultSet=null;
        
        try
        {
            //1.  connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/volunteer", "student", "student");
            
            //2.  create the statement
            statement = conn.createStatement();
            
            //3.  create a string with the sql statement
            String sql = "SELECT YEAR(dateWorked), MONTHNAME(dateWorked), SUM(hoursworked) " +
                         "FROM hoursworked " +
                         "GROUP BY YEAR(dateWorked), MONTH(dateWorked)" +
                         "ORDER BY YEAR(dateWorked), MONTH(dateWorked);";
            
            //4. execute the query
            resultSet = statement.executeQuery(sql);
            
            //5.  loop over the result set and add to our series
            while (resultSet.next())
            {
                if (resultSet.getInt(1) == LocalDate.now().getYear())
                    currentYearSeries.getData().add(new XYChart.Data(resultSet.getString(2), resultSet.getInt(3)));
                else if (resultSet.getInt(1) == LocalDate.now().getYear()-1)
                    previousYearSeries.getData().add(new XYChart.Data(resultSet.getString(2), resultSet.getInt(3)));    
            }       
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }
    
    
    /**
     * This method will return the scene to the VolunteerTableView
     */
    public void backButtonPushed(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "VolunteerTableView.fxml", "All Volunteers");
    }
    
}
