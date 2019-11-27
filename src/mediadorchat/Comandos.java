package mediadorchat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class Comandos {

    public Options options;

    public Comandos() {

        Option option_m = Option.builder("m")
                .desc("The command sends a text message to a topic")
                .argName("mBody")
                //.required(true)
                .hasArg()
                .build();
        Option option_t = Option.builder("t")
                .desc("The t refers to the topic name")
                .argName("tName")
                //.required(true)
                .hasArg()
                .build();
        Option option_w = Option.builder("")
                .desc("Regresa lista de topics")
                .hasArg(false)
                .build();

        options = new Options();
        options.addOption(option_m);
        options.addOption(option_w);
        options.addOption(option_t);

    }

    public CommandLine parse(String args[]) throws ParseException {

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {

            commandLine = parser.parse(options, args);
        } catch (ParseException exception) {
            System.out.print("Parse error: ");
            System.out.println(exception);
        }
        return commandLine;
        
    }
}
