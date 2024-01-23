package gitlet;
import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
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
            System.err.println("Not in an initialized Gitlet directory.");
            return;
        }
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                gitletadd(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                commit(args);
                break;
            case "rm":
                gitletrm(args[1]);
                break;
            case "log":
                log();
                break;
            case "global-log":
                globallog();
                break;
            case "find":
                find(args);
                break;
            case "branch":
                branch(args);
                break;
            case "rm-branch":
                rmBranch(args);
                break;
            case "status":
                gitletStatus();
                break;
            case "checkout":
                gitletCheckout(args);
                break;
            case "reset":
                gitletreset(args);
                break;
            case "merge":
                gitletmerge(args[1]);
                break;
            default:
                System.err.println("No command with that name exists.");
        }
    }
}
