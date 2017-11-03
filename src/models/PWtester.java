package models;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *
 * @author jaret_000
 */
public class PWtester {
    public static void main(String[] args) throws NoSuchAlgorithmException
    {
        String password = "simple";
        byte[] salt = PasswordGenerator.getSalt();
        
        System.out.printf("password: %s%n", PasswordGenerator.getSHA512Password(password, salt));
        System.out.printf("password: %s%n", PasswordGenerator.getSHA512Password(password, salt));
    }
}
