package com.github.alxwhtmr.herbzdbdatamanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created on 22.01.2015.
 */
public class DataManager {
    private String user;
    private String password;
    private String host;
    private String hostPrefix;
    private String database;
    private String dbDriver;
    private String table;
    private String substTitle;
    private String substDescription;
    private Connection connection;
    private boolean isConnectionEstablished;

    public DataManager() {
        try {
            initFields();
            initConnection();
        } catch (IOException e) {
            Utils.abort(e);
        }
    }

    @Override
    public String toString() {
        String stringed = String.format("%s -- %s -- %s -- %s%s%s -- %s\n\t%s: [%s -- %s -- %s]",
                user, password, hostPrefix, host, database, dbDriver, database, table, substTitle, substDescription);
        return stringed;
    }

    public boolean isConnectionEstablished() {
        return isConnectionEstablished;
    }

    public String getSubstDescription(String substance) throws SQLException {
        String description = null;

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT " +
                substDescription +
                " FROM " +
                table +
                " WHERE " +
                substTitle + "=\'" + substance + "\'");

        if (resultSet.next()) {
            StringBuffer buf = new StringBuffer();
            String unedited = resultSet.getString(1);
            int counter = 0;
            for (int i = 0 ; i < unedited.length(); i++) {
                if (counter != Constants.LETTERS_IN_TEXT_ROW) {
                    buf.append(unedited.charAt(i));
                } else {
                    counter = 0;
                    while (unedited.charAt(i) != ' ') {
                        buf.append(unedited.charAt(i++));
                    }
                    buf.append("\n");
                    continue;
                }
                counter++;
            }
            description = buf.toString();
        }
        return description;
    }

    private final void initFields() throws IOException {
        isConnectionEstablished = false;
        ArrayList<String> data = new ArrayList<String>();
        File file = new File(Constants.CONFIG);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            scanner.close();
            Utils.abort(e);
        }
        for (String line : data) {
            String lineSplit[] = line.split(Constants.FIELDS_SEPARATOR);
            String field = lineSplit[0];
            String value = lineSplit[1];

            switch (field) {
                case (Constants.USER_FIELD): {
                    user = value;
                    break;
                }
                case (Constants.PASSWORD_FIELD): {
                    password = value;
                    break;
                }
                case (Constants.HOST_FIELD): {
                    host = value;
                    break;
                }
                case (Constants.HOST_PREFIX_FIELD): {
                    hostPrefix = value;
                    break;
                }
                case (Constants.DATABASE_FIELD): {
                    database = value;
                    break;
                }
                case (Constants.DBDRIVER_FIELD): {
                    dbDriver = value;
                    break;
                }
                case (Constants.TABLE_NAME_FIELD): {
                    table = value;
                    break;
                }
                case (Constants.SUBST_TITLE_FIELD): {
                    substTitle = value;
                    break;
                }
                case (Constants.SUBST_DESCRIPTION_FIELD): {
                    substDescription = value;
                    break;
                }
                default:
                    break;
            }
        }
        if (user == null || password == null || host == null || hostPrefix == null ||
                database == null || dbDriver == null || table == null || substTitle == null || substDescription == null) {
            throw new IOException("Error reading fields in " + Constants.CONFIG);
        }
    }

    private final void initConnection() {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            Utils.abort(e);
        }

        try {
            connection = DriverManager.getConnection(hostPrefix+host+database, user, password);
            isConnectionEstablished = true;
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException e1) {
                Utils.logErr(e1);
            }
            Utils.abort(e);
        }
    }


    public static void main(String args[]) {
        DataManager dm = new DataManager();
        Utils.log(dm);
        try {
            System.out.println(dm.getSubstDescription("substance1"));
        } catch (SQLException e) {
            Utils.abort(e);
        }
    }
}
