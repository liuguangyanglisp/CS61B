package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;
import java.text.SimpleDateFormat;


/**Represents a gitlet commit object.
 * construct commit and save it into file system.
 * To make commit immutable, all instance variable are private
 * commit information can get by method, like: parents/files/messages/commitID/splitPoint etc.
 *
 * @author liuguangyang
 */
public class Commit implements Serializable {
    /**List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /**Folder to store commit file.*/
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");

    /**Folder to store bolob file. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /**numbers of commit has been made. */
    private static int number;
    /**The first parent commit of this Commit. */
    private String parent;
    /**second parent of a merged commit. */
    private String secondParent;
    /**Map of all the files the commit is tracking.
     * key: fileName, value: blobID. */
    private FileMap fileMap;
    /**FileMap class and constructor. FileMap is a TreeMap. */
    private class FileMap extends TreeMap<String, String> {
        private FileMap(String filename, String blobID) {
            put(filename, blobID);
        }
    }

    /**Commit time. */
    private Date time;
    /**The message of this Commit. */
    private String message;

    /**Construct an init commit and save it in file when gitlet init.
     * only use once in a gitlet repo. */
    public Commit() {
        message = "initial commit";
        time = new Date(0);
        if (number == 0) {
            saveCommit();
        }
    }

    /**Create a new commit,save a snapshot of tracked files in the current commit and staging area
     * so they can be restored at a later time.The commit is said to be tracking the saved files.*/
    public Commit(String amessage, String secondprent) {
        if (amessage.isBlank()) {
            System.err.println("Please enter a commit message.");
            return;
        }
        message = amessage;
        time = new Date();
        parent = headCommitID();
        secondParent = secondprent;
        fileMap = getCommit(parent).fileMap;
        if (trackStage()) {
            saveCommit();
            number++;
        }
    }

    /**track or remove files from this commit according to stageArea.
     * empty add-StageArea and remove-StageArea
     */
    private boolean trackStage() {
        List<String> addStageFileNames = plainFilenamesIn(ADDSTAGEAREA_DIR);
        List<String> removeStageFileNames = plainFilenamesIn(REMOVESTAGEAREA_DIR);
        if (addStageFileNames.isEmpty() && removeStageFileNames.isEmpty()) {
            System.err.println("No changes added to the commit.");
            return false;
        }

        for (String fileName : addStageFileNames) {
            File stageFile = join(ADDSTAGEAREA_DIR, fileName);
            String blobId = readContentsAsString(stageFile);
            if (fileMap == null) {
                fileMap = new FileMap(fileName, blobId);
            }
            fileMap.put(fileName, blobId);
            stageFile.delete();
        }


        for (String fileName : removeStageFileNames) {
            File stageFile = join(REMOVESTAGEAREA_DIR, fileName);
            //String blobId = readContentsAsString(stageFile);
            fileMap.remove(fileName);
            stageFile.delete();
        }
        return true;
    }

    /**Save this commit into file, and let it be HEAD.
     */
    private void saveCommit() {
        //serialize commit and generate SHA1
        byte[] serializeCommit = serialize(this);
        String commitID = sha1(serializeCommit);
        String shortCommitID = commitID.substring(0, 6);

        //use SHA1 to create a file in Commit_Dir,and write the commit into the file.
        File storeDir = join(COMMIT_DIR, shortCommitID);
        storeDir.mkdir();
        File storFile = join(storeDir, commitID);
        writeObject(storFile, this);

        //change HEAD(activeBranch) to the newest commit.
        writeContents(activeBranch(), commitID);
    }

    /**Return the HEAD commit.*/
    public static Commit head() {
        Commit head = Commit.getCommit(headCommitID());
        return head;
    }
    /**Return the commtID of the HEAD commit. */
    public static String headCommitID() {
        return readContentsAsString(activeBranch());
    }

    /**Return the File path of activeBranch. */
    public static File activeBranch() {
        File activeBranch = new File(readContentsAsString(HEAD));
        return activeBranch;
    }

    /*Return a Commit object,take an commitID as argument. */
    public static Commit getCommit(String commitID) {
        if (commitID == null) {
            return null;
        }
        String shortID = commitID.substring(0, 6);
        File commitFile = join(COMMIT_DIR, shortID, commitID);
        if (commitFile.exists()) {
            Commit commit = readObject(commitFile, Commit.class);
            return commit;
        }
        return null;
    }

    /**Given a abbreviate commitID , return a long commitID (sha1)
     */
    public static String getlongCommitID(String commitID) {
        if (commitID == null) {
            return null;
        }
        if (commitID.length() == 40) {
            return commitID;
        }
        if (commitID.length() >= 6 && commitID.length() < 40) {
            String sixDigitsID = commitID.substring(0, 6);
            File shortCommitIDfile = join(COMMIT_DIR, sixDigitsID);
            if (shortCommitIDfile.exists()) {
                List<String> longCommitID = plainFilenamesIn(shortCommitIDfile);
                return longCommitID.get(0);
            }
        }
        return null;
    }

    /**Return this commit's message.*/
    public String getMessage() {
        return this.message;
    }

    /**Return this commit's time in String format*/
    public String getTime() {
        Formatter formatter = new Formatter(Locale.US);
        String date = formatter.format("%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", time).toString();
        return date;
    }

    /**Return this commit's blobID*/
    public String getBlob(String filename) {
        if (this.fileMap == null) {
            return null;
        }
        return this.fileMap.get(filename);
    }

    /**Returns this commit's parent commit. */
    public String getParent() {
        return this.parent;
    }
    /**Returns this commit's second parent. */
    public String getSecondParent() {
        return this.secondParent;
    }

    /**Return fileNames of the commit in lexicographic order set. */
    public Set<String> fileNameSet() {
        if (fileMap == null) {
            return null;
        } else {
            return fileMap.keySet();
        }
    }

    /**Return a commit ID which is the splitPoint of Head and another branch when merging. */
    public static String splitPoint(String headID, String branchID) {
        Collection<String> headParent = new TreeSet<>();
        Queue<String> branchParent = new ArrayDeque<>();
        headParent = getParents(headID, headParent);
        branchParent = (Queue<String>) getParents(branchID, branchParent);

        while (!branchParent.isEmpty()) {
            String currentID = branchParent.poll();
            if (headParent.contains(currentID)) {
                return currentID;
            }
        }
        return null;
    }
    /**Return a collection of all parents of a commit.
    * Return type can be Queue/TreeSet etc. */
    private static Collection<String> getParents(String commitID, Collection<String> col) {
        Queue<String> queue = new ArrayDeque<>();
        queue.add(commitID);
        Collection<String> parents = col;
        parents.add(commitID);

        while (!queue.isEmpty()) {
            String currentID = queue.poll();
            Commit commit = getCommit(currentID);
            if (commit.parent != null && !parents.contains(commit.parent)) {
                queue.add(commit.parent);
                parents.add(commit.parent);
            }
            if (commit.secondParent != null && !parents.contains(commit.secondParent)) {
                queue.add(commit.secondParent);
                parents.add(commit.secondParent);
            }
        }
        return parents;
    }
}
