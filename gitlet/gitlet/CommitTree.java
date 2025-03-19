package gitlet;

// : any imports you need here;

import java.io.File;
import java.io.Serializable;


/**
 * Represents a gitlet commit object.
 *  : It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Murari Ganesan
 */
public class CommitTree implements Serializable {
    // Instance variables of CommitTree class
    Commit parent;
    //File COMMIT_DIR;
    public static final File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commits");
    public CommitTree(Commit parent) {
        this.parent = parent;
    }

    public CommitTree(Commit parent, Commit left, Commit right) {
        this.parent = parent;
        this.parent.leftCommit = left;
        this.parent.rightCommit = right;
        //COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commits");
        if (!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
    }

    /**
     * Represents a single commit in the gitlet commit tree.
     */


}
