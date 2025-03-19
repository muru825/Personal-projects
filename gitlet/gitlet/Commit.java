package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class Commit implements Serializable, Cloneable {
    public static final File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commitshistory");
    String message;
    String timestamp;
    TreeMap<String, Blob> files; //make private
    String commitID;
    Commit parent = null;
    Commit leftCommit = null; //make private
    Commit rightCommit = null; //make private
    String commitsSpecificBranch;

    //Commit object constructor
    public Commit(String message) {
        this.message = message;
        Date now = new Date();
        String pattern = "EEE MMM d HH:mm:ss yyyy Z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        this.timestamp = dateFormat.format(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = sdf.format(new Date());
        commitID = Utils.sha1(formattedTimestamp); // commitID is based on the timestamp
        files = new TreeMap<String, Blob>();
    }

    public void saveCommit() {
        if (!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdirs();
        }
        File commitFile = Utils.join(COMMIT_DIR, commitID);
        Utils.writeObject(commitFile, this);
    }

    public void toConsole() {
        System.out.println("===\n");
        System.out.println("commit " + this.commitID + "\n");
        System.out.println("Date: " + timestamp + "\n");
        System.out.println(message + "\n");
    }

    private void setParent(Commit parent) {
        this.parent = parent;
    }

    public void setLeftCommit(Commit leftCommit) {
        leftCommit.setParent(this);
        this.leftCommit = leftCommit;
    }

    public void setRightCommit(Commit rightCommit) {
        rightCommit.setParent(this);
        this.rightCommit = rightCommit;
    }

    public TreeMap<String, Blob> getFiles() {
        return files;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Commit c) {
        return this.commitID.equals(c.commitID) && this.message.equals(c.message) && this.files.equals(c.files);
    }
}
