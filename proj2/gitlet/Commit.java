package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author LiuGuangYang
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    public static final File Commit_Dir = join(GITLET_DIR,"commits");

    //TODO: where to put blob?should blob be short?
    public static final File Blobs_Dir = join(GITLET_DIR,"blobs");

    /*The parent commit of this Commit. */
    private String parent;
    /*If this is a merged Commit, it has a second parent. */
    private FileMap fileMap;
    private class FileMap extends TreeMap<String, String> {
        private FileMap (String filename, String blobID) {
            put(filename,blobID);
        }
    }
    /*Commit time. */
    private Date time;
    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    public Commit() {
        message = "initial commit";
        time = new Date(0);
    }

    /*Any changes made to files after staging for addition or removal are ignored by the commit command,
    which only modifies the contents of the .gitlet directory.
    For example, if you remove a tracked file using the Unix rm command (rather than Gitlet’s command of the same name),
    it has no effect on the next commit, which will still contain the (now deleted) version of the file.*/
    /*TODO:Each commit has a log message associated with it that describes the changes to the files in the commit.
    This is specified by the user. The entire message should take up only one entry in the array args that is passed to main.
    To include multiword messages, you’ll have to surround them in quotes.*/
    //TODO: branch?
    //TODO: runtime

    public Commit(String amessage) {
        if (amessage.isBlank()) {
            throw new GitletException("Please enter a commit message.");
        }
        message = amessage;
        time = new Date();
        parent = headCommitID();
        fileMap = getCommit(parent).fileMap;
        trackStage();
    }

    /**track or remove files from this commit according to stageArea.
     * empty add-StageArea and remove-StageArea*/
    private  void trackStage() {
        List<String> addStageFileNames = plainFilenamesIn(AddStageArea);
        List<String> removeStageFileNames = plainFilenamesIn(RemoveStageArea);
        if (addStageFileNames.isEmpty() && removeStageFileNames.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

            for (String fileName : addStageFileNames) {
                File stageFile = join(AddStageArea, fileName);
                String blobId = readContentsAsString(stageFile);
                if (fileMap == null) {
                    fileMap = new FileMap(fileName,blobId);
                }
                fileMap.put(fileName, blobId);
                stageFile.delete();
            }


            for (String fileName : removeStageFileNames) {
                File stageFile = join(RemoveStageArea,fileName);
                String blobId = readContentsAsString(stageFile);
                fileMap.remove(fileName);
                stageFile.delete();
            }
        }

    /**Save this commit into file, and let it be HEAD.*/
    public void saveCommit() {
        //serialize commit and generate SHA1
        byte[] serializeCommit = serialize(this);
        String commitID = sha1(serializeCommit);
        String shortCommitID = commitID.substring(0,6);

        //use SHA1 to create a file in Commit_Dir,and write the commit into the file.
        File storeDir = join(Commit_Dir,shortCommitID);
        storeDir.mkdir();
        File storFile = join(storeDir,commitID);
        writeObject(storFile,this);

        //change HEAD(activeBranch) to the newest commit.
        writeContents(activeBranch(),commitID);
    }

    /*Return a Commit object,take an commitID as argument. */
    public static Commit getCommit (String commitID) {
        String shortID = commitID.substring(0,6);
        File commit = join(Commit_Dir,shortID,commitID);
        return readObject(commit,Commit.class);
    }
    /*Return this commit's message.*/
    public String getMessage() {
        return this.message;
    }
    /*Return this commit's time in String format*/
    public String getTime() {
        Formatter formatter = new Formatter(Locale.US);
        String dateString = formatter.format("%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", time).toString();
        return dateString;
    }
    /*Return this commit's blobID*/
    public String getBlob(String filename) {
        if (this.fileMap == null) {
            return null;
        }
        return this.fileMap.get(filename);
    }
    /**Returns this commit's parent commit.*/
    public String getParent( ) {
        return this.parent;
    }
    /**Return fileNames of the commit in lexicographic order set. */
    public Set<String> fileNameSet() {
        if (fileMap == null) {
            return null;
        } else {
            return fileMap.keySet();
        }
    }

    /**Return Files(Map) of this commit*/
    public FileMap getFileMap () {
        return fileMap;
    }

    /**Given a directory whose file named after short SHA1 , return long SHA1*/
    public static String getlongSHA1 (File directory,String shortSHA1) {
        File shortSHA1file = join(directory,shortSHA1);
        List<String> longSHA1 = plainFilenamesIn(shortSHA1file);
        return longSHA1.getFirst();
    }
}
