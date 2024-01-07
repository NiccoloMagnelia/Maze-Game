package byow.Core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            Pattern pattern = Pattern.compile("N[0-9]++S");
            Matcher matcher = pattern.matcher(args[1]);
            if (matcher.find()) {
                Engine engine = new Engine();
                engine.interactWithInputString(args[1]);
                System.out.println(engine.toString());
            } else {
                System.out.println("Not a valid seed.");
            }
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
