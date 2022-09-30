package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Vivek Singh
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
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

                if (!gitlet.Repository.checkFileExists(args[1])) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }

                if (!gitlet.Repository.checkIndexExists()) {
                    gitlet.Repository.setupStagingArea(args[1]);
                } else {
                    gitlet.Repository.addToIndex(args[1]);
                }
                break;

            case "commit":
                validateNumArgs("commit", args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (gitlet.Repository.isIndexEmpty()) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                }

                if (args[1].isBlank()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }

                gitlet.Repository.createANewCommit(args[1]);
                break;

            case "rm" :
                validateNumArgs("rm", args, 2);

                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (!gitlet.Repository.checkFileExists(args[1])) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }
                if (!gitlet.Repository.fileExistsInIndex(args[1])
                        && !gitlet.Repository.fileInHEADCommit(args[1])) {
                    System.out.println("No reason to remove the file.");
                    System.exit(0);
                }

                gitlet.Repository.removeFile(args[1]);
                break;

            case "log" :
                validateNumArgs("log", args, 1);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                gitlet.Repository.printLog();
                break;

            case "global-log" :
                validateNumArgs("global-log", args, 1);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                gitlet.Repository.printAllCommits();
                break;

            case "find" :
                validateNumArgs("find", args, 2);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                gitlet.Repository.findMsgInCommits(args[1]);
                break;

            case "status" :
                validateNumArgs("status", args, 1);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (!gitlet.Repository.checkIndexExists()) {
                    System.out.println("No files tracked yet.");
                    System.exit(0);
                }
                gitlet.Repository.printStatus();
                break;
            case "branch" :
                validateNumArgs("branch", args, 2);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (gitlet.Repository.branchExists(args[1])) {
                    System.out.println("A branch with that name already exists.");
                }

                gitlet.Repository.createBranch(args[1]);
                break;
            case "checkout" :
                //validateNumArgs("checkout", args, 2);
                if (!gitlet.Repository.checkGitDirExists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }

                if (args.length == 2) {
                    if (!gitlet.Repository.branchExists(args[1])) {
                        System.out.println("No such branch exists.");
                        System.exit(0);
                    }

                    if (!gitlet.Repository.isIndexEmpty()) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }

                    if (gitlet.Repository.isCurrentBranch(args[1])) {
                        System.out.println("No need to checkout the current branch.");
                        System.exit(0);
                    }
                    gitlet.Repository.checkoutBranch(args[1]);
                } else if (args.length == 3 && args[1].equals("--")) {
                    gitlet.Repository.checkoutFile(gitlet.Repository.returnHEADPointer(), args[2]);
                } else if (args[2].equals("--") && args.length == 4) {
                    gitlet.Repository.checkoutFile(args[1], args[3]);
                }

                break;
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

    /** Checks if the predicate is equal to the expected value and if not then print the error
     * msg and exit.
     * @param pred The result of a function call
     * @param expected Expected return val of the predicate function
     * @param msg Error msg that will be displayed before exiting
     */
    public static void checkFor(boolean pred, boolean expected, String msg) {
        if (pred != expected) {
            System.out.println(msg);
            System.exit(0);
        }
    }
}
