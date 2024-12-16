package BusinessLayer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Payment {
    private int paymentId;
    private String username;
    private String encryptedCreditCardNumber;
    private String encryptedExpirationDate;
    private String encryptedCvv;
    private String fullName;

    public Payment() {}

    public Payment(int paymentId, String username, String encryptedCreditCardNumber, String encryptedExpirationDate,
            String encryptedCvv, String fullName) {
        this.paymentId = paymentId;
        this.username = username;
        this.encryptedCreditCardNumber = encryptedCreditCardNumber;
        this.encryptedExpirationDate = encryptedExpirationDate;
        this.encryptedCvv = encryptedCvv;
        this.fullName = fullName;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedCreditCardNumber() {
        return encryptedCreditCardNumber;
    }

    public void setEncryptedCreditCardNumber(String encryptedCreditCardNumber) {
        this.encryptedCreditCardNumber = encryptedCreditCardNumber;
    }

    public String getEncryptedExpirationDate() {
        return encryptedExpirationDate;
    }

    public void setEncryptedExpirationDate(String encryptedExpirationDate) {
        this.encryptedExpirationDate = encryptedExpirationDate;
    }

    public String getEncryptedCvv() {
        return encryptedCvv;
    }

    public void setEncryptedCvv(String encryptedCvv) {
        this.encryptedCvv = encryptedCvv;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean insertEncryptedPayment(Connection connection, String username, String creditCardNumber, LocalDate expirationDate, Integer cvv, String fullName) {
        boolean success = false;
        try {
            String encryptedCreditCardNumber = encrypt(creditCardNumber);
            String encryptedExpirationDate = encrypt(expirationDate.toString());
            String encryptedCvv = encrypt(String.valueOf(cvv));

            String sql = "INSERT INTO Payment (username, creditCardNumber, expirationDate, cvv, fullName) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, encryptedCreditCardNumber);
                preparedStatement.setString(3, encryptedExpirationDate);
                preparedStatement.setString(4, encryptedCvv);
                preparedStatement.setString(5, fullName);
                int rowsChanged = preparedStatement.executeUpdate();
                if (rowsChanged > 0) {
                    success = true;
                } else {
                    success = false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while inserting encrypted payment data: " + e.getMessage());
        }
        return success;
    }

    private static String encrypt(String input) {
        try {
            String key = "0123456789abcdef0123456789abcdef";
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Error during encryption: " + e.getMessage());
            return null;
        }
    }

    private static String decrypt(String encryptedData) {
        try {
            String key = "0123456789abcdef0123456789abcdef"; 
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        } catch (Exception e) {
            System.err.println("Error during decryption: " + e.getMessage());
            return null;
        }
    }
}

