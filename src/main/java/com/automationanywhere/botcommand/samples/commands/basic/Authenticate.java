package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.core.security.SecureString;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 *<pre>
Authenticates via the Salesforce API to generate access_token...which is used in subsequent calls

 * </pre>
 *
 * @author Micah Smith
 */

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "authenticate", label = "[[Authenticate.label]]",
        node_label = "[[Authenticate.node_label]]", description = "[[Authenticate.description]]", icon = "salesforce.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[Authenticate.return_label]]", return_type = STRING, return_required = true)

public class Authenticate {
    private static HttpURLConnection connection;
    //Messages read from full qualified property file name and provide i18n capability.
    private static final Messages MESSAGES = MessagesFactory
            .getMessages("com.automationanywhere.botcommand.samples.messages");

    @Sessions
    private Map<String, Object> sessionMap;

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public Value<String> action(
            @Idx(index = "1", type = TEXT)
            //UI labels.
            @Pkg(label = "[[Authenticate.session.label]]", default_value_type = STRING, default_value="Default")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String sessionName,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "2", type = CREDENTIAL)
            //UI labels.
            @Pkg(label = "[[Authenticate.loginURL.label]]", description = "[[Authenticate.loginURL.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    SecureString loginURL,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "3", type = CREDENTIAL)
            //UI labels.
            @Pkg(label = "[[Authenticate.clientID.label]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    SecureString clientID,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "4", type = CREDENTIAL)
            //UI labels.
            @Pkg(label = "[[Authenticate.clientSecret.label]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    SecureString clientSecret,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "5", type = CREDENTIAL)
            //UI labels.
            @Pkg(label = "[[Authenticate.username.label]]", description = "[[Authenticate.username.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    SecureString username,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "6", type = CREDENTIAL)
            //UI labels.
            @Pkg(label = "[[Authenticate.password.label]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    SecureString password) {

        //Setting values for REST call
        String grant_type = "password";
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String access_token = "";
        String grantService = "/services/oauth2/token?grant_type=password";
        //Create Map to store values for session as session requires String/Object Map (see line 170)
        Map<String, String> sessionValues = new HashMap<String, String>();
        sessionValues.put("sessionName", sessionName);
        sessionValues.put("baseLoginURL", loginURL.getInsecureString());


        try {
            String urlWithParams = loginURL.getInsecureString() +
                    grantService +
                    "&client_id=" + clientID.getInsecureString() +
                    "&client_secret=" + clientSecret.getInsecureString() +
                    "&username="+username.getInsecureString() +
                    "&password="+password.getInsecureString();

            //url should now have all the proper values from the credential vault or insecure strings
            URL url = new URL(urlWithParams);
            connection = (HttpURLConnection) url.openConnection();
            //Request Setup
            connection.setRequestMethod("POST");
            //set 5 second timeout for read and connect
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            //while loop going through each line of the failed response to build the response message
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }

            //reading response is complete, closer the reader
            reader.close();

            System.out.println(responseContent.toString());
            //parse(responseContent.toString());
            JSONObject response = new JSONObject(responseContent.toString());

            if (response.has("access_token")) {
                access_token = response.getString("access_token");
                sessionValues.put("access_token", access_token);
                //sessionMap.put("access_token", access_token);
            } else {
                //if error occured - indicate error to user and send full responseContent (message from salesforce) so they can see what might be wrong
                access_token = "Error retrieving access token : " + responseContent.toString();
            }

        } catch (Exception e) {
            //should any error occur in try block, appending Exception occured + message so user can fully understand what may have happened.
            access_token = access_token + " Exception Occured: " + e.getMessage();
            e.printStackTrace();
        } finally {
            //disconnect connection
            connection.disconnect();
        }

        //Create session - must be String/Object Map
        //SessionName is a string, sessionValues is a map (object) which has session name, base url, and access token
        sessionMap.put(sessionName, sessionValues);
        //Return StringValue of access token
        //Note: This COULD be an error message if the response didnt have access_token in it, so returning the response
        //either way so user can check for error message.
        return new StringValue(access_token);
    }

    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
