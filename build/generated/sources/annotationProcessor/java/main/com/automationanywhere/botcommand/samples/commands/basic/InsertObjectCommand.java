package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.BotCommand;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
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

public final class InsertObjectCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(InsertObjectCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    InsertObject command = new InsertObject();
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

    if(parameters.containsKey("objectType") && parameters.get("objectType") != null && parameters.get("objectType").get() != null) {
      convertedParameters.put("objectType", parameters.get("objectType").get());
      if(!(convertedParameters.get("objectType") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","objectType", "String", parameters.get("objectType").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("objectType") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","objectType"));
    }

    if(parameters.containsKey("insertDictionary") && parameters.get("insertDictionary") != null && parameters.get("insertDictionary").get() != null) {
      convertedParameters.put("insertDictionary", parameters.get("insertDictionary").get());
      if(!(convertedParameters.get("insertDictionary") instanceof Map)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","insertDictionary", "Map", parameters.get("insertDictionary").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("insertDictionary") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","insertDictionary"));
    }

    command.setSessionMap(sessionMap);
    try {
      Optional<Value> result =  Optional.ofNullable(command.action((String)convertedParameters.get("sessionName"),(String)convertedParameters.get("objectType"),(Map<String, Value>)convertedParameters.get("insertDictionary")));
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
