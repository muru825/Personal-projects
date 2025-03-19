package gitlet;

import java.io.File;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author
 */
public abstract class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command."); //  : what if args is empty?
            return;
        }
        String firstArg = args[0];
        Repository repository = null;
        File repo = Utils.join(Repository.GITLET_DIR, "repository");
        try {
            if (repo.exists()) {
                repository = loadRepository();
            } else {
                repository = new Repository();
                if (!firstArg.equals("init")) {
                    throw  Utils.error("Not in an initialized Gitlet directory.");
                }
            }
            switch (firstArg) {
                case "init":
                    repository.init();
                    break;
                case "add":
                    validateNumArgs("add", args, 2); //there should be two arguments add(sha ID)
                    repository.add(args);
                    break;
                case "commit": //commit
                    validateNumArgs("commit", args, 2);
                    repository.commit(args);   // this should be a string message
                    break;  //: handle the `add [filename]` command
                case "rm": //remove
                    validateNumArgs("rm", args, 2);
                    repository.rm(args[1]);
                    break;
                case "log": // history of the recent commits
                    repository.log();
                    break;
                case "global-log":
                    repository.globalLog();
                    break;
                case "find":
                    validateNumArgs("find", args, 2);
                    repository.find(args[1]);
                    break;
                case "status":
                    validateNumArgs("status", args, 1);
                    repository.status(args);
                    break;
                case "restore":
                    repository.restore(args);
                    break;
                case "branch":
                    validateNumArgs("branch", args, 2);
                    repository.branch(args);
                    break;
                case "switch":
                    validateNumArgs("switch", args, 2);
                    repository.switchBranch(args[1]);
                    break;
                case "rm-branch":
                    validateNumArgs("rm-branch", args, 2);
                    repository.rmbranch(args[1]);
                    break;
                case "reset":
                    validateNumArgs("reset", args, 2);
                    repository.reset(args[1]);
                    break;
                case "merge":
                    validateNumArgs("merge", args, 2);
                    repository.merge(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    break;
            } repository.saveRepository();
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
    public static Repository loadRepository() {
        File repo = Utils.join(Repository.GITLET_DIR, "repository");
        return Utils.readObject(repo, Repository.class);
    }


}
