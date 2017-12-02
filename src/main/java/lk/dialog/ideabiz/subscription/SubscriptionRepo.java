package lk.dialog.ideabiz.subscription;


import com.google.gson.Gson;
import lk.ideabiz.api.model.common.PIN.SubscriptionResponse;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionRepo {

    private static DataSource authDataSource;
    private Logger logger;
    private Gson gson;

    public SubscriptionRepo() {
        logger = Logger.getLogger(SubscriptionRepo.class);
        gson = new Gson();
    }

    private static DataSource getAuthDataSource() {
        return authDataSource;
    }

    public void setAuthDataSource(DataSource authDataSource) {
        SubscriptionRepo.authDataSource = authDataSource;
    }

    public String getSubscriber(String msisdn) {
        logger.info("Retrieving auth token for id " + msisdn);
        String sql = "SELECT * FROM `subscribers` WHERE `msisdn`  = ?";
        Connection dbConnection = null;

        try {
            dbConnection = getAuthDataSource().getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, msisdn);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                System.out.println("Resuslts...." + result.getString("ref"));
            }
        } catch (SQLException e) {
            logger.error("DB get Oauth : " + e.getMessage());
        } finally {
            try {
                if (dbConnection != null && !dbConnection.isClosed())
                    dbConnection.close();
            } catch (SQLException e) {
            }
        }

        return "ref";
    }

    public void insertSubscriber(SubscriptionResponse response) {
        String sql = "INSERT INTO subscribers(msisdn, ref, status) VALUES(?, ?, ?)";
        Connection dbConnection = null;
        try {
            dbConnection = getAuthDataSource().getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, response.getMsisdn());
            preparedStatement.setString(2, response.getServerRef());
            preparedStatement.setString(3, response.getStatus());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB Update Oauth : " + e.getMessage());
            if (e.getMessage().contains("Duplicate")) {
                logger.info("Updating...");
            }
        } finally {
            try {
                if (dbConnection != null && !dbConnection.isClosed())
                    dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void deleteSubscriber(String msisdn) {
        logger.info("Deleting from table. for " + msisdn);
        String sql = "DELETE FROM subscribers WHERE msisdn = ?";
        Connection dbConnection = null;
        try {
            dbConnection = getAuthDataSource().getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, msisdn);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB Update Oauth : " + e.getMessage());
        } finally {
            try {
                if (dbConnection != null && !dbConnection.isClosed())
                    dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public List<SubscriptionResponse> getAll() {
        String sql = "SELECT * FROM subscribers";
        Connection dbConnection = null;
        ArrayList<SubscriptionResponse> list = new ArrayList<>();

        try {
            dbConnection = getAuthDataSource().getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
                subscriptionResponse.setServerRef(result.getString("ref"));
                subscriptionResponse.setMsisdn(result.getString("msisdn"));
                subscriptionResponse.setStatus(result.getString("status"));
                list.add(subscriptionResponse);
            }
        } catch (SQLException e) {
            logger.error("DB get Oauth : " + e.getMessage());
        } finally {
            try {
                if (dbConnection != null && !dbConnection.isClosed())
                    dbConnection.close();
            } catch (SQLException e) {
                logger.info("Error" + e);
            }
        }
        return list;
    }
}
