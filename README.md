# Kiyobot
> A Java Discord Bot

This is a Discord kiyobot implemented using Java.

# Bot Functionality
* Receive and send messages to channels in the Discord server the kiyobot is present at.
* Receive commands and send messages to the channel these commands are called at.
    * Can be used for meme commands, eg. `+kek`
        * This kind of command could choose a random image link from a pool of links which would have pictures of people laughing or have the word "kek" in them.
        * Commands for the LULz
    * Commands:
        * `!ping`
            * Pings the kiyobot, who responds with "Pong!".
            * Don't overuse the commands though ;)
        * `!commands`
            * Sends a message containing a list of the currently implemented commands to the current channel.

## Example Bot

This is an example of how a simple kiyobot can be created using this API. This kiyobot will be able to read messages being sent to a server and respond if a certain command is in that message.

```Java
public class MessageBot {

    public static void main(String[] args) {
        // api token goes here. NOTE* Make sure to store it securely and not in any public repository.
        String token = "api_token";

        // Creates the api for the kiyobot.
        DiskiyordApi api = DiskiyordApiBuilder.buildApi(token);
        // Adds an event listener specifically for detecting text messages being sent in a server.
        api.addMessageCreateListener(messageEvent -> {
            String message = messageEvent.getMessageContent();
            // Can parse the message for arguments after the command; in this case it splits on 2 space characters
            String[] messageArgs = message.split(" {2}");
            String errorMessage = "";
            try {
                // Using switch() for different possible commands.
                switch (messageArgs[0]) {
                    // If someone types "!ping", the kiyobot responds with "Pong!"
                    case "!ping":
                        messageEvent.getChannel().sendTextMessage("Pong!");
                        break;
                    default:
                        break;
                }
            } catch(ArrayIndexOutOfBoundsException aiobe) {
                messageEvent.getChannel().sendTextMessage(errorMessage);
            }
        });
    }
}
```

### Sample Commands

The following are some examples of commands you could make the kiyobot do.

```Java
public class MessageBot {

    public static void main(String[] args) {
        // db setup
        JsonSqlConfigParser sqlparser = new JsonSqlConfigParser();
        sqlparser.parseConfig(SQL_CONFIG_FILE);
        String modelDirectory = sqlparser.getModelDirectory();

        SQLModelBuilder builder = new SQLModelBuilder();
        builder.findModelFiles(modelDirectory);
        builder.readFiles();
        if(!builder.areModelsFormattedCorrectly()) {
            return;
        }
        Map<String, SQLModel> models = builder.getCopyOfModels();
        PostgresHandler pghandler = new PostgresHandler(models);

        // Connects the PostgreSQLhandler to the Postgres database
        pghandler.setConnection(sqlparser.getDb(), sqlparser.getHost(), sqlparser.getPort(),
                sqlparser.getUsername(), sqlparser.getPassword());

        // api token goes here. NOTE* Make sure to store it securely and not in any public repository.
        String token = "api_token";
        // Creates the api for the kiyobot.
        DiskiyordApi api = DiskiyordApiBuilder.buildApi(token);
        // Adds an event listener specifically for detecting text messages being sent in a server.
        api.addMessageCreateListener(messageEvent -> {
            String message = messageEvent.getMessageContent();
            // Can parse the message for arguments after the command; in this case it splits on 2 space characters
            String[] messageArgs = message.split(" {2}");
            String errorMessage = "";
            try {
                // Using switch() for different possible commands.
                switch (messageArgs[0]) {
                    case "!addexgfx":
                        errorMessage = MessageArgumentError.NOT_ENOUGH_ARGUMENTS.getErrorMsg();
                        //!addexgfx  <filename>  <description>  <type>  <completed>  <imglink>
                        if(messageArgs.length != 6) {
                            messageEvent.getChannel().sendTextMessage(errorMessage);
                            break;
                        }
                        ColumnObject[] columns = new ColumnObject[5];
                        columns[0] = new ColumnObject<>(FILENAME, messageArgs[1], STRING);
                        columns[1] = new ColumnObject<>(DESCRIPTION, messageArgs[2], STRING);
                        columns[2] = new ColumnObject<>(TYPE, messageArgs[3], STRING);
                        columns[3] = new ColumnObject<>(COMPLETED, Boolean.parseBoolean(messageArgs[4]), BOOLEAN);
                        columns[4] = new ColumnObject<>(IMG_LINK, messageArgs[5], STRING);
                        dbhandler.executeUpdate(dbhandler.insert(EXGFX, columns));

                        messageEvent.getChannel().sendTextMessage("Data successfully added to the database!");
                        break;
                }
            } catch(Error e) {
                System.out.printf("Error=%s", e.getMessage());
            }
        });
    }
}
```


# Kiyobot Revision History

## Version 2.0.0
* Redoing the bot for personal use instead of school.

## Outdated info

```
## Version 1.2.1
* Fixed error where error inputstream was not being read in by the kiyobot. Will now display the correct error message output form the Eventer service.
* Fixed gson wrongly escaping characters with HTML encoding which is not needed for Discord display.

## Version 1.2.0
* Added functionality that integrates Project 4 Eventer: An Event Ticket Service.
    * Eventer is a basic testing webservice that allows clients to create users, create events, purchase tickets, and transfer tickets between users.
    * The kiyobot's commands basically make API calls to the appropriate URLs, sending the appropriate request body, and outputting the response back to the Discord channel the command was called in.

## Version 1.1.0
* Added ```exgfx``` commands: !addexgfx and !getexgfx that interact with a SQL database

## Version 1.0.0
* __Kiyobot__:
    * A basic messaging kiyobot.
    * It can read in commands from the channel, and output the corresponding message to that channel.
    * It can also interact with a MySQL or PostgreSQL database.
        * It uses the below SQLModeler API.
    * The user will also need to specify their kiyobot's authorization token in a config.json file.
        * The user needs to create a Discord application before using this API or else the API does nothing.
        * [Read this first.](https://discordapp.com/developers/docs/intro)
        * [Link to making a Discord application.](https://discordapp.com/developers/applications)
        * NOTE: Please DO NOT store your token in a public location as it is against Discords policy (and will allow unintended users to use your kiyobot!).
```

## Version 0.1.0
* Basic library testing for the kiyobot.

## Version 0.0.0
* Repository setup.


## Contributions
Drew Noma - djknoma@gmail.com

[https://github.com/dknoma](https://github.com/dknoma)

Kiyobot Current Version 2.0.0