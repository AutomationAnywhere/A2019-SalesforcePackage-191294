package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.BotCommand;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.core.security.SecureString;
import java.lang.ClassCastException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AuthenticateCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(AuthenticateCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    Authenticate command = new Authenticate();
    HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
    if(parameters.containsKey("sessionName") && parameters.get("sessionName") != null && parameters.get("sessionName").get() != null) {
      convertedParameters.put("sessionName", parameters.get("sessionName").get());
      if(!(convertedParameters.get("sessionName") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","sessionName", "String", parameters.get("sessionName").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("sessionName") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","sessionName"));
    }

    if(parameters.containsKey("loginURL") && parameters.get("loginURL") != null && parameters.get("loginURL").get() != null) {
      convertedParameters.put("loginURL", parameters.get("loginURL").get());
      if(!(convertedParameters.get("loginURL") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","loginURL", "SecureString", parameters.get("loginURL").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("loginURL") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","loginURL"));
    }

    if(parameters.containsKey("clientID") && parameters.get("clientID") != null && parameters.get("clientID").get() != null) {
      convertedParameters.put("clientID", parameters.get("clientID").get());
      if(!(convertedParameters.get("clientID") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","clientID", "SecureString", parameters.get("clientID").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("clientID") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","clientID"));
    }

    if(parameters.containsKey("clientSecret") && parameters.get("clientSecret") != null && parameters.get("clientSecret").get() != null) {
      convertedParameters.put("clientSecret", parameters.get("clientSecret").get());
      if(!(convertedParameters.get("clientSecret") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","clientSecret", "SecureString", parameters.get("clientSecret").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("clientSecret") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","clientSecret"));
    }

    if(parameters.containsKey("username") && parameters.get("username") != null && parameters.get("username").get() != null) {
      convertedParameters.put("username", parameters.get("username").get());
      if(!(convertedParameters.get("username") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","username", "SecureString", parameters.get("username").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("username") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","username"));
    }

    if(parameters.containsKey("password") && parameters.get("password") != null && parameters.get("password").get() != null) {
      convertedParameters.put("password", parameters.get("password").get());
      if(!(convertedParameters.get("password") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","password", "SecureString", parameters.get("password").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("password") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","password"));
    }

    command.setSessionMap(sessionMap);
    try {
      Optional<Value> result =  Optional.ofNullable(command.action((String)convertedParameters.get("sessionName"),(SecureString)convertedParameters.get("loginURL"),(SecureString)convertedParameters.get("clientID"),(SecureString)convertedParameters.get("clientSecret"),(SecureString)convertedParameters.get("username"),(SecureString)convertedParameters.get("password")));
      return logger.traceExit(result);
    }
    catch (ClassCastException e) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.IllegalParameters","action"));
    }
    catch (BotCommandException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }
}
