package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import static gitlet.Commit.*;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  * The structure of a Repository is as follows:
 *  *
 *  * .gitlet/ -- top level folder for all persistent data in my Proj2 folder.
 *  *    - Commit_Dir/ -- folder containing all of the persistent data for Commits.
 *  *    - story -- file containing the current story
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File StageArea = join(GITLET_DIR,"stageArea");
    public static final File AddStageArea = join(StageArea,"addStageArea");
    public static final File RemoveStageArea = join(StageArea,"removeStageArea");
    public static final File HEAD = join(GITLET_DIR,"HEAD");
    public static final File BRANCHS = join(GITLET_DIR,"branchHeads");

    /* TODO: fill in the rest of this class. */

    /*Creates a new Gitlet version-control system in the current directory.
    TODO:This system will automatically start with one commit:
    a commit that contains no files and has the commit message initial commit (just like that, with no punctuation).
    TODO:It will have a single branch: master, which initially points to this initial commit, and master will be the current branch.
    The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates
    (this is called “The (Unix) Epoch”, represented internally by the time 0.)
    Since the initial commit in all repositories created by Gitlet will have exactly the same content,
    it follows that all repositories will automatically share this commit (they will all have the same UID) and all commits in all repositories will trace back to it.*/
    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();

            BRANCHS.mkdir();
            moveHeadto("master");

            Commit_Dir.mkdir();
            //Create a commit, and save into Commit_Dir;
            Commit initCommit = new Commit();
            initCommit.saveCommit();
            Blobs_Dir.mkdir();
            StageArea.mkdir();
            AddStageArea.mkdir();
            RemoveStageArea.mkdir();
        }

    }

    /*Adds a copy of the file as it currently exists to the staging area (see the description of the commit command).
    For this reason, adding a file is also called staging the file for addition.
    TODO:(should i do something? seems not)Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
    The staging area should be somewhere in .gitlet.

    TODO:The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.*/
    /*If the current working version of the file is identical to the version in the current commit, do not stage it to be added,
        and remove it from the staging area if it is already there (as can happen when a file is changed, added, and then changed back to it’s original version).*/
    public static void gitletadd(String[] args) {
        if (args.length != 2) {
            System.err.println("Error: please provide a fileName.");
        }
        String fileName = args[1];

        File fileInCWD = join(CWD,fileName);
        if (!fileInCWD.exists()) {
            throw new GitletException("File does not exist.");
        }

        if (identical(CWD,fileName)) {
            join(AddStageArea,fileName).delete();
        } else {
            stageFile(fileName,AddStageArea);
            join(RemoveStageArea,fileName).delete();
        }
    }

    /**Stage a file from CWD to stageArea(addStage or removeStage)
     *stage format: origin fileName and write SHA1 in it */
    private static void stageFile (String fileName,File stageArea) {
        File CWDfile = join(CWD,fileName);
        byte[] fileContent = readContents(CWDfile);
        String blobID = sha1(fileContent);
        String shortBlobID = blobID.substring(0,6);

        File stageFile = join(stageArea,fileName);
        writeContents(stageFile,blobID);

        File singleBlobDir = join(Blobs_Dir,shortBlobID);
        singleBlobDir.mkdir();
        File blobfile = join(singleBlobDir,blobID);
        writeContents(blobfile,fileContent);
    }

    public static Commit Head() {
        Commit HEAD = Commit.getCommit(headCommitID());
        return HEAD;
    }

    public static String headCommitID() {
        return readContentsAsString(activeBranch());
    }

    public static File activeBranch() {
        File activeBranch = new File(readContentsAsString(HEAD));
        return activeBranch;
    }

    /*Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit.
    The commit is said to be tracking the saved files.
    By default, each commit’s snapshot of files will be exactly the same as its parent commit’s snapshot of files;
    it will keep versions of files exactly as they are, and not update them.
    A commit will only update the contents of files it is tracking that have been staged for addition at the time of commit,
    in which case the commit will now include the version of the file that was staged instead of the version it got from its parent.
    A commit will save and start tracking any files that were staged for addition but weren’t tracked by its parent.
    Finally, files tracked in the current commit may be untracked in the new commit as a result being staged for removal by the rm command (below).
    The bottom line: By default a commit has the same file contents as its parent.
    Files staged for addition and removal are the updates to the commit.
    Of course, the date (and likely the mesage) will also different from the parent.*/
    /*TODO: how to deal with quotation word*/
    public static void commit(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Error: please provide a message. multiwords should be surrounded by quotation. ");
        }
        String message = args[1];
        Commit newcommit = new Commit(message);
        newcommit.saveCommit();
    }

    /**Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for removal and remove the file from the working directory if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).*/
    public static void gitletrm(String[] args) {
        if (args.length != 2) {
            throw new GitletException("Error: please provide a fileName");
        }
        String fileName = args[1];
        File sameFileInAddStage = join(AddStageArea,fileName);

        if (!sameFileInAddStage.exists() && !isTracked(fileName)) {
            throw new GitletException("No reason to remove the file.");
        }

        if (sameFileInAddStage.exists()) {
            sameFileInAddStage.delete();
        }

        if (isTracked(fileName)) {
            stageFile(fileName,RemoveStageArea);
            restrictedDelete(join(CWD,fileName));
        }
    }


    /*Print log form HEAD to initial commit.
    *log information: commit, time, message */
    public static void log() {
        String commitID = headCommitID();
        while (commitID != null) {
            printlog(commitID);
            commitID = Commit.getCommit(commitID).getParent();
        }
    }

    /**Print log information: commit, time, message
     * take a commitID as argment. */
    private static void printlog (String commitID) {
        Commit commit = getCommit(commitID);
        System.out.println("===");
        System.out.println("commit " + commitID);
        System.out.println("Date: " + commit.getTime());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /**Print all the commits ever made.*/
    public static void globallog () {
        List<String> commitFileNames = FilenamesIn(Commit_Dir);
        for (String commitFileName : commitFileNames) {
            String longCommitID = getlongSHA1(Commit_Dir,commitFileName);
            printlog(longCommitID);
        }
    }
    /**Prints out the ids of all commits that have the given commit message, one per line. */
    public static void find(String[] args) {
        if (args.length != 2) {
            System.err.println("Error: please provide a message.");
            return;
        }
        String message = args[1];
        List<String> commitFileNames = FilenamesIn(Commit_Dir);
        int findTimes = 0;
        for (String commitFileName : commitFileNames) {
            String longCommitID = getlongSHA1(Commit_Dir,commitFileName);
            String commitMessage = Commit.getCommit(longCommitID).getMessage();
            if (commitMessage.equals(message)) {
                System.out.println(longCommitID);
                findTimes ++;
            }
        }
        if (findTimes == 0) {
            System.err.println("Error: Found no commit with that message.");
        }
    }

    /**Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch (just as in real Git).
     * Before you ever call branch, your code should be running with a default branch called “master”.*/
    public static void branch(String[] args) {
        if (args.length != 2) {
            throw new GitletException("Error: please provide a branchName");
        }
        String branchName = args[1];
        File branchHeads = join(BRANCHS,branchName);
        if (branchHeads.exists()){
            throw new GitletException("Error: A branch with that name already exists.");
        } else {
            writeContents(branchHeads,headCommitID());
        }
    }
    /**Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     * it does not mean to delete all commits that were created under the branch, or anything like that. */
    public static void rmBranch(String[] args) throws IOException {
        if (args.length != 2) {
            throw new GitletException("Error: please provide a branchName");
        }
        String branchName = args[1];
        File branchHeadFile = join(BRANCHS,branchName);
        if (branchHeadFile.exists()){
            if (activeBranch().getName().equals(branchName)) {
                throw new GitletException("Error: Cannot remove the current branch.");
            } else {
                branchHeadFile.delete();
            }
        } else {
            throw new GitletException("Error: A branch with that name does not exist.");
        }
    }

    /**Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     * An example of the exact format it should follow is as follows.*/
    public static void gitletStatus() {
        System.out.println("=== Branches ===");
        List<String> branchFileNames = plainFilenamesIn(BRANCHS);
            for (String fileName : branchFileNames) {
                if (fileName.equals(activeBranch().getName())) {
                    System.out.println("*" + activeBranch().getName());
                } else {
                    System.out.println(fileName);
                }
            }

        System.out.println("\n" + "=== Staged Files ===");
        List<String> addStageFileNames = plainFilenamesIn(AddStageArea);
        for (String fileName : addStageFileNames) {
            System.out.println(fileName);
        }

        System.out.println("\n" + "=== Removed Files ===");
        List<String> rmStageFileNames = plainFilenamesIn(RemoveStageArea);
        for (String fileName : rmStageFileNames) {
            System.out.println(fileName);
        }

        System.out.println("\n" + "=== Modifications Not Staged For Commit ===");
        //Tracked in the current commit, changed in the working directory, but not staged; or

        List<String> CWDfileNames = plainFilenamesIn(CWD);
        for (String fileName : CWDfileNames) {
            if (isTracked(fileName) && !identical(CWD,fileName) && !addStageFileNames.contains(fileName)) {
                System.out.println(fileName + "(modified)");
            }
        }

        //Staged for addition, but with different contents than in the working directory; or
        //Staged for addition, but deleted in the working directory; or
        for (String fileName : addStageFileNames) {
            if (!join(CWD,fileName).exists()) {
                System.out.println(fileName + "(deleted)");
            } else if (!(getBlob(CWD,fileName).equals(readContentsAsString(join(AddStageArea,fileName))))) {
                System.out.println(fileName + "(modified)");
            }
        }

        //Not staged for removal, but tracked in the current commit and deleted from the working directory.
        Set<String> currentCommitFiles = Head().fileNameSet();
        if (currentCommitFiles != null) {
            for (String fileName : currentCommitFiles) {
                if (!join(CWD, fileName).exists() && !join(RemoveStageArea, fileName).exists()) {
                    System.out.println(fileName + "(deleted)");
                }
            }
        }

        //for files present in the working directory but neither staged for addition nor tracked.
        System.out.println("\n" + "=== Untracked Files ===");
        for (String fileName : CWDfileNames) {
            if (!isTracked(fileName) && !join(AddStageArea,fileName).exists()) {
                System.out.println(fileName);
            }
        }

        System.out.println();

    }

    /**Given a fileName(not SHA1), check if it's tracked in current commit*/
    private static boolean isTracked (String fileName) {
        return Head().getBlob(fileName) != null;
    }

    /**Return if a file's directory version is identical with its current commit version*/
    private static boolean identical (File directory, String fileName) {
        String directoryVersion = getBlob(directory,fileName);
        String HEADcommitVersion = Head().getBlob(fileName);
        if (Objects.equals(directoryVersion,HEADcommitVersion) == true) {
            return true;
        }
        return false;
    }


    /**Return a blob(aka: SHA1 value, represent version) of a file according to its content.*/
    private static String getBlob (File directory,String fileName) {
        File thefile = join(directory,fileName);
        if (thefile.isFile()) {
            return sha1(readContents(thefile));
        } else {
            return null;
        }

    }

    public static void gitletCheckout(String[] args) {
        if (args.length == 3 && args[1].equals("--")){
            checkoutFileFromCommit(Head(),args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkoutFileFromID(args[1],args[3]);
        } else if (args.length == 2) {
            checkoutbranch(args[1]);
        } else {
            throw new GitletException(
                    "\nplease provide correct format:" +
                    "\njava gitlet.Main checkout -- [file name]" +
                    "\njava gitlet.Main checkout [commit id] -- [file name]" +
                    "\njava gitlet.Main checkout [branch name]");
        }
    }

    private static void checkoutFileFromID (String shortCommitID, String fileName) {
        if (shortCommitID.length() != 6) {
            throw new GitletException("please provide a abbreviation version of commits, which is a unique 6 digits prefix of original commit ");
        }
        String longCommitID = getlongSHA1(Commit_Dir,shortCommitID);
        Commit commit = getCommit(longCommitID);
        if (commit == null) {
            throw new GitletException("No commit with that id exists.");
        }
        checkoutFileFromCommit(commit,fileName);
    }

    private static void checkoutFileFromCommit (Commit commit, String fileName) {
        String blobID = commit.getBlob(fileName);
        if (blobID == null) {
            throw new GitletException("File does not exist in that commit.");
        }
        String shortBlobID = blobID.substring(0,6);
        byte[] blobContent = readContents(join(Blobs_Dir,shortBlobID,blobID));
        File CWDfile = join(CWD,fileName);
        writeContents(CWDfile,blobContent);
    }

    private static void checkoutbranch (String branchName) {
        File branch = join(BRANCHS,branchName);
        if (!branch.exists()) {
            throw new GitletException("No such branch exists.");
        } else {
            String branchHeadCommitId = readContentsAsString(branch);
            Commit branchHead = getCommit(branchHeadCommitId);
            if (branchHead.equals(Head())) {
                throw new GitletException("No need to checkout the current branch.");
            }
            List<String> CWDfiles = plainFilenamesIn(CWD);
            Set<String> branchHeadCommitFiles = branchHead.fileNameSet();
            for (String file : CWDfiles) {
                if (!isTracked(file) & branchHeadCommitFiles.contains(file)) {
                    throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
            for (String file : branchHeadCommitFiles) {
                checkoutFileFromCommit(branchHead,file);
            }
            for (String file : CWDfiles) {
                if (isTracked(file) && !branchHeadCommitFiles.contains(file)) {
                    join(CWD,file).delete();
                }
            }

            clear(AddStageArea);
            moveHeadto(branchName);
        }
    }

    private static void moveHeadto (String branchName) {
        File branch = join(BRANCHS,branchName);
        writeContents(HEAD,GITLET_DIR.getName(),"/",BRANCHS.getName(),"/",branch.getName());
    }
    /**Clear all files in add or remove stageArea*/
    public static void clear (File directory) {
        if (directory.isDirectory() && (directory.equals(AddStageArea) || directory.equals(RemoveStageArea))) {
            List<String> fileNames = plainFilenamesIn(directory);
            for (String fileName : fileNames) {
                File file = join(directory, fileName);
                if (file.isFile()) {
                    file.delete();
                }
            }
        } else {
            throw new GitletException("Error: can only clear add or remove stageArea.");
        }
    }

    /**Returns a list of the names of all files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    public static List<String> FilenamesIn(File dir) {
        String[] files = dir.list();
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }
}
