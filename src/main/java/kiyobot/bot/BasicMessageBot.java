package kiyobot.bot;

import db.mongo.settings.KiyoMongoSettings;
import jql.sql.jdbc.JDBCEnum;
import jql.sql.jdbc.JDBCHandler;
import jql.sql.jdbc.PostgresHandler;
import jql.sql.model.SQLModel;
import jql.sql.util.JsonSqlConfigParser;
import jql.sql.util.SQLModelBuilder;
import kiyobot.message.MessageEvent;
import kiyobot.util.HerokuEnv;
import kiyobot.util.JsonConfigArgParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.sql.SQLException;
import java.util.Map;


/**
 * Packets sent from the client to the Gateway API are encapsulated within a gateway payload
 * object and must have the proper opcode and data object set. The payload object can then be
 * serialized in the format of choice (see ETF/JSON), and sent over the websocket. Payloads to
 * the gateway are limited to a maximum of 4096 bytes sent, going over this will cause a
 * connection termination with error code 4002.
 *
 *  {
 *      "op": 0,
 *      "d": {},
 *      "s": 42,
 *      "t": "GATEWAY_EVENT_NAME"
 *  }
 *
 *  String.format(%[argument_index$][flags][width]conversion);
 *      %s - put string in
 *      %1$s - put the first string arg here
 *
 *
 */
public class BasicMessageBot {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void main(String[] args) {
		// Diskiyord setup
// 		final JsonConfigArgParser parser = new JsonConfigArgParser();
// 		parser.parseConfig();
//
// 		final KiyoMongoSettings settings = new KiyoMongoSettings(parser.getMongoUser(), parser.getMongoPass());
// 		// Used if need to have bot output to this specific channel
// //		String botStuffChannelId = parser.getBotStuff();
// 		final DiscordApi api = new DiscordApiBuilder().setToken(parser.getAuthTok()).login().join();
		HerokuEnv env = new HerokuEnv();
		env.parseEnv();
		final String authTok = env.getAuthTok();
		final String mongoUser = env.getMongoUser();
		final String mongoPass = env.getMongoPass();
		
		final KiyoMongoSettings settings = new KiyoMongoSettings(mongoUser, mongoPass);
		// Used if need to have bot output to this specific channel
//		String botStuffChannelId = parser.getBotStuff();
		final DiscordApi api = new DiscordApiBuilder().setToken(authTok).login().join();
		// Adds a message listener
		MessageEvent messageEvent = MessageEvent.INSTANCE;
		messageEvent.listenOnMessage(api, settings);
	}
}
