package gitlet;

import java.io.IOException;
import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            throw new GitletException("Must have at least one argument");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                gitletadd(args);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                commit(args);
                break;
            case "rm":
                gitletrm(args);
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
        }
    }
}
