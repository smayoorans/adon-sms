package lk.dialog.ideabiz.library.APICall.DataProvider;


import com.google.gson.Gson;
import lk.dialog.ideabiz.library.model.APICall.OAuth2Model;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLCredentialProviderImpl implements IdeabizOAuthDataProviderInterface {

    private Logger logger;
    private Gson gson;

    private static DataSource authDataSource;

    private static DataSource getAuthDataSource() {
        return authDataSource;
    }

    public  void setAuthDataSource(DataSource authDataSource) {
        MySQLCredentialProviderImpl.authDataSource = authDataSource;
    }

    /***
     * Read Token form DB
     * @param id id of the app in DB
     * @return
     */
    public OAuth2Model getToken(int id) {
        logger.info("Retrieving auth token for id " + id);
        String sql = "SELECT * FROM `oauth2` WHERE `id`  = ?";
        OAuth2Model oAuth2Model = null;
        Connection dbConnection = null;

        try {

            dbConnection = getAuthDataSource().getConnection();

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {

                oAuth2Model = new OAuth2Model();
                oAuth2Model.setId(result.getInt("id"));
                oAuth2Model.setUsername("smayoorans");
                oAuth2Model.setPassword("*********");
                oAuth2Model.setAccessToken(result.getString("accessToken"));
                oAuth2Model.setRefreshToken(result.getString("refreshToken"));
                oAuth2Model.setConsumerKey(result.getString("consumerKey"));
                oAuth2Model.setConsumerSecret(result.getString("consumerSecret"));
                oAuth2Model.setScope(result.getString("scope"));
                oAuth2Model.setExpire(result.getLong("expire"));
                oAuth2Model.setTokenURL(result.getString("tokenURL"));
                oAuth2Model.setLastUpdated(result.getString("lastUpdated"));
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

        return oAuth2Model;
    }

    /***
     * Update token once it refreshed
     * @param id app DB ID
     * @param accessToken
     * @param refreshToken
     * @param expire
     */
    public void updateToken(int id, String accessToken, String refreshToken, String expire) {
        logger.info("Updating auth token..");
        String sql = "UPDATE `oauth2` SET `accessToken` = ?, `refreshToken` = ?, `expire` = ?,lastUpdated=CURRENT_TIMESTAMP() " +
                "WHERE `oauth2`.`id` = ?;";

        OAuth2Model oAuth2Model = null;
        Connection dbConnection = null;

        try {

            dbConnection = getAuthDataSource().getConnection();

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, accessToken);
            preparedStatement.setString(2, refreshToken);
            preparedStatement.setString(3, expire);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            logger.error("DB Update Oauth : " + e.getMessage());

        } finally {
            try {
                if (dbConnection != null && !dbConnection.isClosed())
                    dbConnection.close();
            } catch (SQLException e) {
            }
        }

    }

    public MySQLCredentialProviderImpl() {
        logger = Logger.getLogger(MySQLCredentialProviderImpl.class);
        gson = new Gson();
    }
}
