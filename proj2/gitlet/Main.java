package gitlet;
import gitlet.Repository.*;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Vivek Singh
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs("init", args, 1);

                if (gitlet.Repository.checkGitDirExists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }

                gitlet.Repository.setupGitlet();
                break;
            case "add":
                validateNumArgs("add", args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                break;
            // TODO: FILL THE REST IN
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     *
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
