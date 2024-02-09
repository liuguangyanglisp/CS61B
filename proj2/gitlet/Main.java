package gitlet;
import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author liuguangyang
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.err.println("Please enter a command");
            return;
        }
        String firstArg = args[0];
        if (!GITLET_DIR.exists() && !firstArg.equals("init")) {
            System.err.println("Not in a1tn initialized Gitlet directory.");
            return;
        }
        switch(firstArg) {
            case "init":
                init();
                break;
            case "add":
                add(args[1]);
                break;
            case "commit":
                commit(args[1]);
                break;
            case "rm":
                rm(args[1]);
                break;
            case "log":
                log();
                break;
            case "global-log":
                globallog();
                break;
            case "find":
                find(args[1]);
                break;
            case "branch":
                branch(args[1]);
                break;
            case "rm-branch":
                rmBranch(args[1]);
                break;
            case "status":
                status();
                break;
            case "checkout":
                checkout(args);
                break;
            case "reset":
                reset(args[1]);
                break;
            case "merge":
                merge(args[1]);
                break;
            default:
                System.err.println("No command with that name exists.");
        }
    }
}
