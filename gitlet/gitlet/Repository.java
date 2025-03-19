package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.join;

// : any imports you need here

/**
 * Represents a gitlet repository.
 * Summary: The repository is used in order to track
 * our commits and our "git push origin main" it will hold all
 * of our code AND files AND the history of each blob/ commit
 * - handle data storage and retrie~val
 * does at a high level.
 *
 * @author
 */
public class Repository implements Serializable {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * : add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */


    TreeMap<String, Commit> commitBranches;
    CommitTree tree;
    Commit initialCommit = new Commit("initial commit");
    StagingArea area;
    Commit mainPntr;
    String currBranch;
    TreeMap<String, Blob> allFiles = new TreeMap<String, Blob>();

    public Repository() {
        if (GITLET_DIR.exists()) {
            loadStagingArea();
        } else {
            commitBranches = new TreeMap<>();
        }
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        tree = new CommitTree(initialCommit);
        commitBranches = new TreeMap<>();
        currBranch = "main";
        commitBranches.put("main", initialCommit); // hi I put this in to map the branches to the first commit
        //repository = new Repository(stagingArea, initialCommit, initialCommit, commitBranches);
        area = new StagingArea(commitBranches.get((currBranch)));
        initialCommit.saveCommit();
        //might be an issue to write the pointers for the tree
        saveTree();
    }

    public void saveRepository() { // goal: save the repository
        File repository = new File(Repository.GITLET_DIR, "repository");
        Utils.writeObject(repository, this);
    }

    public void saveCommit() {
        File commitFile = new File(Commit.COMMIT_DIR, "commit");
        Utils.writeObject(commitFile, this);
    }

    public void loadTree() {
        File treeSave = Utils.join(Repository.GITLET_DIR, "tree");
        tree = Utils.readObject(treeSave, CommitTree.class);
    }

    public void saveTree() { // goal: save the commit
        File treeSave = Utils.join(Repository.GITLET_DIR, "tree");
        Utils.writeObject(treeSave, tree);
    }

    public void saveStagingArea() {
        File stagingAreaSave = Utils.join(Repository.GITLET_DIR, "stagingArea");
        Utils.writeObject(stagingAreaSave, area);
    }

    public void loadStagingArea() {
        try {
            File stagingAreaSave = Utils.join(Repository.GITLET_DIR, "stagingArea");
            area = Utils.readObject(stagingAreaSave, StagingArea.class);
        } catch (IllegalArgumentException e) {
            area = null;
        }
    }

    public void saveAllFiles() {
        File allFilesSave = Utils.join(GITLET_DIR, "lastAllFilesRead");
        Utils.writeObject(allFilesSave, allFiles);
    }

    //    public void loadAllFiles() {
    //        File allFilesSave = Utils.join(GITLET_DIR, "lastAllFilesRead");
    //        allFiles = Utils.readObject(allFilesSave, StagingArea.class);
    //    }

    public void add(String[] args) {
        if (area.files.isEmpty()) {
            area = new StagingArea(this.commitBranches.get(currBranch));
        }
        addHelper(args[1]);
        saveStagingArea();

    }

    public void addHelper(String fileName) {
        List<String> allFilesLst = Utils.plainFilenamesIn(Repository.CWD);
        Commit currCommit = commitBranches.get(currBranch);
        int left = 0;
        int right = allFilesLst.size() - 1;
        boolean isFound = false;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int comparison = allFilesLst.get(mid).compareTo(fileName);
            if (comparison < 0) {
                left = mid + 1;
            } else if (comparison > 0) {
                right = mid - 1;
            } else {
                isFound = true;
                break;
            }

        }
        if (isFound) {
            File f = Utils.join(Repository.CWD, fileName);
            if ((currCommit.files.containsKey(fileName))
                    &&
                    (Arrays.equals((currCommit.files.get(fileName).getContentsAsBytes()), Utils.readContents(f)))) {
                return;
            }
            Blob b = new Blob(f, fileName);
            area.files.put(fileName, b);
            b.isStaged = true;
        } else {
            throw Utils.error("File does not exist.");
        }
    }

    public void commit(String[] args) {
        if (args.length == 0) {
            throw Utils.error("No changes added to the commit.");
        }

        String theMessage = args[1];
        if (theMessage.length() == 0) {
            throw Utils.error("Please enter a commit message.");
        }
        loadStagingArea();
        if (area == null) {
            area = new StagingArea(commitBranches.get(currBranch));
        }
        if (area.files.isEmpty()) {
            throw Utils.error("No changes added to the commit.");
        }
        ArrayList<String> rmLst = new ArrayList<>();
        for (String key : area.files.keySet()) {
            if (area.files.get(key).isDeleted) {
                rmLst.add(key);
            } else if (area.files.get(key).isStaged) {
                area.files.get(key).isStaged = false;
            }
        }
        for (String key : rmLst) {
            area.files.remove(key);
            File f = Utils.join(CWD, key);
            if (f.exists()) {
                f.delete();
            }
        }
        Commit currentCommit = this.commitBranches.get(currBranch); // gets the current branch
        Commit newCommit = new Commit(theMessage); // says hey theres a new commit
        currentCommit.setLeftCommit(newCommit); // the left commit should now be the new commit!!!!
        currentCommit.leftCommit.files = area.files; // sets the files
        newCommit.parent = currentCommit; // links the newcommit back to the previous older main
        commitBranches.put(currBranch, newCommit);
        mainPntr = commitBranches.get(currBranch); // newcommit is the new pointer
        newCommit.commitsSpecificBranch = currBranch;
        newCommit.saveCommit(); // saves the commit (check commit.java class)
        area = new StagingArea(commitBranches.get(currBranch));
        //saveStagingArea();

    }
    //        loadTree();
    //        this.commitBranches.get("main").setLeftCommit(new Commit(theMessage));
    //        this.commitBranches.get("main").leftCommit.files = area.files;
    //        area = null;
    //        this.commitBranches.put("main", this.commitBranches.get("main").leftCommit);
    //        this.mainPntr = this.commitBranches.get("main");
    //        saveStagingArea();
    //        saveTree();
    //        saveRepository();

    public void restoring(Commit theCommit, String filename) {

        if (!theCommit.files.containsKey(filename)) { // if it doesnt have the filename "hello.txt"
            throw Utils.error("File does not exist in that commit. ");
        }
        // TO BE CONTINUED
        Blob b = theCommit.files.get(filename);
        //Utils.writeContents(b.getBlobsContent(), b.getContentsAsBytes());
        Utils.writeContents(new File(CWD, filename), b.getContentsAsBytes());
    }

    public void restore(String[] args) {
        //loadTree();
        if (args.length == 3 && args[1].equals("--")) {
            restoreSimple(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            restoreComplex(args[1], args[3]);
        } else {
            throw Utils.error("Incorrect operands.");
        }
    }

    public void restoreSimple(String mostRecentNeededToRestore) {
        restoring(this.commitBranches.get(currBranch), mostRecentNeededToRestore);
    }

    public void restoreComplex(String id, String file) {
        Commit currCommit = commitBranches.get(currBranch);
        if (currCommit.commitID.equals(id)) {
            restoreSimple(file);
            return;
        }
        String firstSixinMainPntr = currCommit.commitID.substring(0, id.length());
        while (!firstSixinMainPntr.equals(id) && currCommit.parent != null) { // currcommit.commitIDequals(id)
            currCommit = currCommit.parent; // move the pointer back to the parent until it hits
            firstSixinMainPntr = currCommit.commitID.substring(0, id.length());
        }
        if (firstSixinMainPntr.equals(id)) {
            restoring(currCommit, file);
        } else {
            throw Utils.error("No commit with that id exists.");
        }

    }

    public void log() {
        if (currBranch.equals("main")) {
            for (Commit key = mainPntr; key != null; key = key.parent) {
                Utils.message("===");
                Utils.message("commit " + key.commitID);
                Utils.message("Date: " + key.timestamp);
                System.out.println(key.message + "\n");
            }
        } else {
            for (Commit key = mainPntr; key != null; key = key.parent) {
                if (key.parent != null && key.commitID.equals(key.parent.commitID)) {
                    continue;
                }
                Utils.message("===");
                Utils.message("commit " + key.commitID);
                Utils.message("Date: " + key.timestamp);
                System.out.println(key.message + "\n");
            }
        }
    }

    public void globalLog() {
        List<String> filenames = Utils.plainFilenamesIn(Commit.COMMIT_DIR); // grabs commits names from COMMIT_DIR
        if (filenames == null) { // if there is nothing
            return;
        }
        for (String var : filenames) {
            File commitFile = Utils.join(Commit.COMMIT_DIR, var); // creates file that takes us to that commit file
            Commit vvar = Utils.readObject(commitFile, Commit.class); // reads commitFile/ deserializes it
            Utils.message("===");
            Utils.message("commit " + vvar.commitID);
            Utils.message("Date: " + vvar.timestamp);
            Utils.message(vvar.message);
            Utils.message("");
        }
        Utils.message("==="); // this gets the initial commit bc it is left out in for loop
        Utils.message("commit " + initialCommit.commitID);
        Utils.message("Date: " + initialCommit.timestamp);
        System.out.println(initialCommit.message + "\n");
    }


    public void branch(String[] args) {
        if (commitBranches.containsKey(args[1])) {
            throw Utils.error("A branch with that name already exists.");
        }
        String name = args[1];
        Commit c;
        try {
            c = (Commit) commitBranches.get(currBranch).clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        c.files = (TreeMap<String, Blob>) commitBranches.get(currBranch).files.clone();
        c.files.replaceAll((k, v) -> c.files.get(k).clone());
        commitBranches.put(name, c);
        commitBranches.get(currBranch).setRightCommit(c);
        commitBranches.get(name).parent = commitBranches.get(currBranch);
        saveTree();
        // test 31 error where is initial commit/ mainpointer
    }

    public void rmbranch(String branchName) {
        if (!commitBranches.containsKey(branchName)) {
            throw Utils.error("A branch with that name does not exist.");
        } else if (currBranch.equals(branchName)) {
            throw Utils.error("Cannot remove the current branch.");
        }
        updateAreaStatus();
        commitBranches.remove(branchName);

    }

    public void cwdStatus() {
        //loadAllFiles();
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String fileName : cwdFiles) {
            File f = Utils.join(CWD, fileName);
            if (allFiles.containsKey(fileName)) {
                Blob b = allFiles.get(fileName);
                if (Utils.readContents(f) != b.getContentsAsBytes()) {
                    b.isChanged = true;
                }
            }
            Blob b = new Blob(f, fileName);
            allFiles.put(fileName, b);
        } // adding the filenames AS KEY and blob
        List<String> rmLst = new ArrayList<>();
        for (String key : allFiles.keySet()) {
            if (!cwdFiles.contains(key)) {
                rmLst.add(key);
            }
        }
        for (String key : rmLst) {
            allFiles.remove(key);
        }
        //saveAllFiles();
    }

    public void rm(String args) {
        cwdStatus();
        String rmFile = args;
        Commit c = commitBranches.get(currBranch);
        if (!area.files.isEmpty()) {
            for (String key : area.files.keySet()) {
                if (rmFile.compareTo(key) == 0 && !c.files.containsKey(key)) {
                    area.files.get(key).isStaged = false;

                    saveStagingArea();
                    return;
                } else if (rmFile.compareTo(key) == 0) {
                    File f = Utils.join(CWD, rmFile);
                    f.delete();
                    area.files.get(key).isDeleted = true;
                    area.files.get(key).isStaged = false;

                    saveStagingArea();
                    return;
                }
            }
        } else if (!(commitBranches.get(currBranch) == initialCommit) && !allFiles.containsKey(rmFile)) {
            area = new StagingArea(commitBranches.get(currBranch));
            Blob b = new Blob(rmFile);
            area.files.put(rmFile, b);
            for (String key : area.files.keySet()) {
                if (rmFile.compareTo(key) == 0) {
                    area.files.get(key).isDeleted = true;
                    return;
                }

            }

        }
        throw Utils.error("No reason to remove the file.");
    }

    public void switchBranch(String branchName) {
        //updateAreaStatus();
        if (!commitBranches.containsKey(branchName)) {
            throw Utils.error("No such branch exists.");
        }
        // if (branchName.compareTo(currBranch) == 0) {
        if (currBranch == branchName) {
            throw Utils.error("No need to switch to the current branch.");
        }
        updateAreaStatus();
        cwdStatus();
        if (!currBranch.equals(branchName)) {
            for (String key : area.files.keySet()) {
                File cwdFile = Utils.join(CWD, key);
                if (commitBranches.get(currBranch).files.containsValue(cwdFile)) {
                    commitBranches.get(currBranch).files.get(key).isChanged = false;
                }
            }
        }

        int track = 0;
        for (String key : area.files.keySet()) {

            if (allFiles.containsKey(key)) {
                track += 1;
            }
            if (area.files.get(key).isChanged || area.files.get(key).isStaged) {
                throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
            } // throws errors even after it has been committed
        }
        if (track < allFiles.size()) {
            throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        Commit switchedCommit = commitBranches.get(branchName);

        if (switchedCommit.equals(initialCommit)) {
            for (String key : initialCommit.files.keySet()) {
                Blob blob = initialCommit.files.get(key);
                File file = Utils.join(CWD, key);
                Utils.writeContents(file, blob.getContentsAsBytes());
            }
        }
        if (Utils.plainFilenamesIn(CWD).size() != 0) {
            for (String fileName : Utils.plainFilenamesIn(CWD)) {
                File file = Utils.join(CWD, fileName);
                file.delete();
            }
        }
        if (!switchedCommit.equals(initialCommit)) {
            for (String key : switchedCommit.files.keySet()) {
                Blob blob = switchedCommit.files.get(key);
                File file = Utils.join(CWD, key);
                Utils.writeContents(file, blob.getContentsAsBytes());
            }
        }
        currBranch = branchName;
        Commit c = commitBranches.get(currBranch);
        area.files = (TreeMap<String, Blob>) c.files.clone();
        mainPntr = switchedCommit;

    }


    public void status(String[] args) {
        cwdStatus();
        updateAreaStatus();
        System.out.println("=== Branches ===");
        TreeSet<String> commitBranchesKeys = new TreeSet<>(commitBranches.keySet());
        for (String key : commitBranchesKeys) {
            if (key.equals(currBranch)) {
                key = "*" + key;
            }
            System.out.println(key);
        }
        System.out.println("\n=== Staged Files ===");
        TreeSet<String> areaFileKeys = new TreeSet<>(area.files.keySet());
        if (!area.files.isEmpty()) {
            for (String key : areaFileKeys) {
                if (area.files.get(key).isStaged && !area.files.get(key).isDeleted) {
                    Utils.message(key);
                }
            }
        }
        /** rm log here*/
        System.out.println("\n=== Removed Files ===");
        Commit c = commitBranches.get(currBranch);
        TreeSet<String> cFileKeys = new TreeSet<>(c.files.keySet());
        for (String key : areaFileKeys) {
            if (area.files.get(key).isDeleted && c.files.containsKey(key)) { // and not staged????

                Utils.message(key);
            }
        }

        TreeSet<String> allFilesKeys = new TreeSet<>(allFiles.keySet());
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        for (String key : allFilesKeys) {
            if (area.files.containsKey(key) && !area.files.get(key).isStaged && area.files.get(key).isChanged) {
                Utils.message(key + "(modified)");
            } else if (area.files.containsKey(key) && area.files.get(key).isStaged && area.files.get(key).isChanged) {
                Utils.message(key + "(modified)");
            } else if (area.files.containsKey(key) && !area.files.get(key).isStaged && !allFiles.containsKey(key)) {
                Utils.message(key + "(deleted)");
            } else if (area.files.containsKey(key) && area.files.get(key).isStaged
                    && c.files.containsKey(key) && !allFiles.containsKey(key)) {
                Utils.message(key + "(deleted)");
            }
        }
        System.out.println("\n=== Untracked Files ===");

        for (String z : allFilesKeys) {
            if (!area.files.containsKey(z)) {
                Utils.message(z);
            }
        }
    }


    public void reset(String commitID) {
        cwdStatus();
        updateAreaStatus();
        File commitFile = Utils.join(Commit.COMMIT_DIR, commitID);
        Commit dummy = commitBranches.get(currBranch);
        List<String> commits = Utils.plainFilenamesIn(Commit.COMMIT_DIR);
        TreeSet<String> allFilesKeys = new TreeSet<>(allFiles.keySet());

        List<String> allFilesLst = Utils.plainFilenamesIn(CWD);
        if (!commits.contains(commitID)) {
            throw Utils.error("No commit with that id exists.");
        }
        dummy = Utils.readObject(commitFile, Commit.class);
        for (String z : allFilesLst) {
            if (!area.files.containsKey(z)) {
                throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        for (String key : allFilesLst) {
            File f = Utils.join(CWD, key);
            f.delete();
        }

        for (String key : dummy.files.keySet()) {
            File f = Utils.join(CWD, key);
            Utils.writeContents(f, dummy.files.get(key).getContentsAsBytes());
        }
        area = new StagingArea(dummy);
        commitBranches.put(currBranch, dummy);
        mainPntr = commitBranches.get(currBranch);


    }

    /**
     * Prints out the ids of all commits that
     * have the given commit message, one per line. If there are multiple such commits,
     * it prints the ids out on separate lines. The commit message is a single operand; to indicate
     * a multiword message, put the operand in quotation marks, as for the commit command below. Hint:
     * the hint for this command is the same as the one for global-log.
     */
    public void find(String commitMessage) {
        List<String> filenames = Utils.plainFilenamesIn(Commit.COMMIT_DIR);
        if (filenames == null) { // if there is nothing
            return;
        }
        ArrayDeque<String> founded = new ArrayDeque<>();
        for (String var : filenames) {
            File commitFile = Utils.join(Commit.COMMIT_DIR, var);
            Commit vvar = Utils.readObject(commitFile, Commit.class);
            if (vvar.message.equals(commitMessage)) {
                Utils.message(vvar.commitID);
                founded.add(vvar.message);
            }
        }
        if (!founded.contains(commitMessage)) {
            Utils.message("Found no commit with that message.");
        }
    }

    public void updateAreaStatus() {
        cwdStatus();
        Commit currCommit = commitBranches.get(currBranch);
        for (String key : area.files.keySet()) {
            File f = Utils.join(CWD, key);
            area.files.get(key).isDeleted = !f.exists();
            if (!area.files.get(key).isDeleted) {
                byte[] areasfilesbytes = area.files.get(key).getContentsAsBytes();
                byte[] currentCWDbytes = Utils.readContents(f);
                area.files.get(key).isChanged = !Arrays.equals(areasfilesbytes, currentCWDbytes);
            }
        }
    }

    public void merge(String branchName) {
        cwdStatus();
        updateAreaStatus();
        if (currBranch.equals(branchName)) {
            throw Utils.error("Cannot merge a branch with itself.");
        }
        if (!commitBranches.containsKey(branchName)) {
            throw Utils.error("A branch with that name does not exist.");
        }
        for (String key : area.files.keySet()) {
            if (area.files.get(key).isStaged) {
                throw Utils.error("You have uncommitted changes.");
            }
        }


        TreeSet<String> allFilesKeys = new TreeSet<>(allFiles.keySet());
        List<String> allFilesLst = Utils.plainFilenamesIn(CWD);
        for (String z : allFilesLst) {
            if (!area.files.containsKey(z)) {
                throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }


        Commit currCommit = commitBranches.get(currBranch);
        Commit givenCommit = commitBranches.get(branchName); // write if branchname DNE
        Commit lca = splitPoint(currCommit, givenCommit);

        Commit commitPntr = commitBranches.get(branchName);
        /** If there are staged additions or removals present, print the error message You have uncommitted changes.
         *  and exit. If a branch with the given name does not exist, print the error message A branch with that name
         *  does not exist. If attempting to merge a branch with itself, print the error message Cannot merge a branch
         *  with itself. If merge would generate an error because the commit that it does has no changes in it, just let
         *  the normal commit error message for this go through. If an untracked file in the current commit would be
         *  overwritten or deleted by the merge, print There is an untracked file in the way; delete it, or add and
         *  commit it first. and exit; perform this check before doing anything else.*/


        if (givenCommit.commitID.equals(lca.commitID)) { // if the branch is the same
            Utils.message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (currCommit.commitID.equals(lca.commitID)) {
            switchBranch(branchName);
            Utils.message("Current branch fast-forwarded.");
            return;
        }
        try {
            merge1(currCommit, givenCommit, lca);
            merge5(currCommit, givenCommit, lca);
            merge6(currCommit, givenCommit, lca);
            merge8(currCommit, givenCommit, lca);
            commit(new String[]{"commit", "Merged " + branchName + " into " + currBranch + "."});
        } catch (GitletException e) {
            // throw Utils.error("Encountered a merge conflict.");
        }
    }


    private Commit splitPoint(Commit current, Commit other) {
        boolean isFound = false;
        Commit start1 = current;
        Commit compare1 = start1;
        Commit start2 = other;
        Commit compare2 = start2;
        while (!start1.equals(initialCommit)) {
            while (!start2.equals(initialCommit)) {
                if (start1.equals(start2)) {
                    return start1;
                }
                start2 = start2.parent;
            }
            start2 = other;
            start1 = start1.parent;
        }
        return null;
    }

    private void merge1(Commit current, Commit given, Commit leastCommonAncestor) {
        Commit curr = current;
        Commit giv = given;
        Commit lca = leastCommonAncestor;
        for (String key : giv.files.keySet()) {
            boolean modlca = !lca.files.containsKey(key)
                    || !Arrays.equals(lca.files.get(key).getContentsAsBytes(), giv.files.get(key).getContentsAsBytes());
            if (modlca) {
                File f = Utils.join(CWD, key);
                Utils.writeContents(f, giv.files.get(key).getContentsAsBytes());
            }
        }
    }

    private void merge8(Commit current, Commit given, Commit leastCommonAncestor) {
        Commit curr = current;
        Commit giv = given;
        Commit lca = leastCommonAncestor;
        boolean mergeConflict = false;
        for (String key : giv.files.keySet()) {
            if (curr.files.containsKey(key) && lca.files.containsKey(key)
                    && (!Arrays.equals(lca.files.get(key).getContentsAsBytes(), giv.files.get(key).getContentsAsBytes())
                    || lca.files.get(key).isDeleted
                    || (!lca.files.containsKey(key) && !Arrays.equals(curr.files.get(key).getContentsAsBytes(),
                    giv.files.get(key).getContentsAsBytes())))) {
                File f = Utils.join(CWD, key);
                String msg = "<<<<<<< HEAD\n";
                msg += new String(curr.files.get(key).getContentsAsBytes(), StandardCharsets.UTF_8) + "=======\n";
                msg += new String(giv.files.get(key).getContentsAsBytes(), StandardCharsets.UTF_8);
                msg += ">>>>>>>\n";
                Utils.writeContents(f, msg);
                add(new String[]{"add", key});
                mergeConflict = true;
            }
        }
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private void merge5(Commit current, Commit given, Commit leastCommonAncestor) {
        Commit curr = current;
        Commit giv = given;
        Commit lca = leastCommonAncestor;
        for (String key : giv.files.keySet()) {
            if (!lca.files.containsKey(key) && !curr.files.containsKey(key)) {
                File f = Utils.join(CWD, key);
                Utils.writeContents(f, giv.files.get(key).getContentsAsBytes());
                add(new String[]{"add", key});
            }
        }
    }

    private void merge6(Commit current, Commit given, Commit leastCommonAncestor) {
        Commit curr = current;
        Commit giv = given;
        Commit lca = leastCommonAncestor;
        for (String key : lca.files.keySet()) {
            if (!giv.files.containsKey(key) && curr.files.containsKey(key)
                    && Arrays.equals(lca.files.get(key).getContentsAsBytes(),
                    curr.files.get(key).getContentsAsBytes())) {
                rm(key);
            }
        }
    }
}
