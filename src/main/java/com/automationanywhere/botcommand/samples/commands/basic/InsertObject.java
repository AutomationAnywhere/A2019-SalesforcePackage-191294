package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.DICTIONARY;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * <pre>
 * insertObject allows for inserting salesforce objects. ObjectID is returned on successful response as is a success boolean value
 *
 * </pre>
 *
 * @author Micah Smith
 */

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "insertobject", label = "[[InsertObject.label]]",
        node_label = "[[InsertObject.node_label]]", description = "[[InsertObject.description]]", icon = "salesforce.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[InsertObject.return_label]]", return_description = "[[InsertObject.return_description]]", return_type = STRING, return_required = true)

public class InsertObject {
    //Messages read from full qualified property file name and provide i18n capability.
    private static final Messages MESSAGES = MessagesFactory
            .getMessages("com.automationanywhere.botcommand.samples.messages");

    @Sessions
    private Map<String, Object> sessionMap;

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public StringValue action(
            @Idx(index = "1", type = TEXT)
            //UI labels.
            @Pkg(label = "[[Authenticate.session.label]]", default_value_type = STRING, default_value = "Default")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String sessionName,


            @Idx(index = "2", type = TEXT)
            //UI labels.
            @Pkg(label = "[[UpdateObject.objectType.label]]", description = "[[UpdateObject.objectType.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String objectType,


            @Idx(index = "3", type = DICTIONARY)
            //UI labels.
            @Pkg(label = "[[InsertObject.insertJSON.label]]", description = "[[InsertObject.insertJSON.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    Map<String, Value>  insertDictionary) {
        String line;
        //sobjects endpoint
        String grantService = "/services/data/v49.0/sobjects/";
        String result = "";
        StringBuffer responseContent = new StringBuffer();
        String message = "";
        String errorCode = "";

        //Create HashMap from session Map Object which was stored
        Map<String, String> sessionValues = (Map<String, String>) sessionMap.get(sessionName);
        if(sessionValues.get("sessionName") != sessionName)
            throw new BotCommandException(MESSAGES.getString("Session " + sessionName + " does not exist."));
        //Retrieve values from session Hashmap
        String loginURL = sessionValues.get("baseLoginURL");
        String access_token =sessionValues.get("access_token");

        String requestBodyString = "";
        try {
            String urlWithParams = loginURL +
                    grantService + objectType;

            if(insertDictionary.size() > 0){
                //Building JSON with input values to later be converted to StringEntity for URL Encoding
                JSONObject insertJSON = new JSONObject();
                for (Map.Entry<String,Value> entry: insertDictionary.entrySet())
                    insertJSON.put(entry.getKey(),entry.getValue());
                // go from JSON to String to Encode
                requestBodyString = insertJSON.toString();
                StringEntity entity = new StringEntity(requestBodyString, ContentType.APPLICATION_FORM_URLENCODED);

                //Note: Using apache HTTP client this time
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(urlWithParams);
                request.setHeader("Content-type", "application/json");
                request.setHeader("Authorization", "Bearer " + access_token);
                request.setEntity(entity);
                //Execute HTTP Request
                HttpResponse response = client.execute(request);
                int actualResponseCode = response.getStatusLine().getStatusCode();

                //Response should be JSON, read and parse
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }

                //Assuming JSON response
                JSONObject jsonResponse = new JSONObject(responseContent.toString());

                if (jsonResponse.getBoolean("success") == true) {
                    //Returning Object ID for success
                    result = jsonResponse.getString("id");
                } else {
                    //success wasnt returned, sending error message formatted cleanly for user
                    result = "ResponseCode: "+ Integer.toString(actualResponseCode)  +" Error Occured: "+jsonResponse.getString("errorCode")+", Error Message: "+ jsonResponse.getString(message);
                }

            } else {
                //Set error message for empty dictionary
                result = "Dictionary is empty. Add at least one object property to perform an insert.";
            }
        } catch (Exception e) {
            //including full payload of error so user has full understanding of response from the API
            result = result + " Exception Occured: " + e.getMessage() + "|| Payload Sent:" + requestBodyString + " || Response Content: " + responseContent.toString() ;
            e.printStackTrace();
        } finally {
            //Return StringValue.
            return new StringValue(result);
        }
    }

    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
