package gitlet;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.File;
import java.util.*;
import static gitlet.Commit.*;
import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * * The structure and command of the repository are mostly designed here.
 * *The structure of a Repository is as follows:
 * * .gitlet/ -- top level folder for all persistent data. located in my Proj2 folder.
 * *    - COMMIT_DIR/ -- folder containing all Commits.
 * *    - BLOBS_DIR/ -- folder containing all staged bolb.
 * *    - StageArea/ -- has two directory to temporarily store files.
 * *       -AddStage/ -- stage file for addition.
 * *       -removeStage/ -- stage file for removal.
 * *    - branchHeads/ -- folder containing all branch head files.
 * *       branch head files are named in their branch name and write commitID in the file.
 * *    - HEAD/ -- a file whose contend is a file path point to the current branch head file.
 *
 * @author liuguangyang
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * Has two subdirectory to temporarily store files.
     */
    public static final File STAGEAREA_DIR = join(GITLET_DIR, "stageArea");
    /**
     * A directory to stage file for addition.
     */
    public static final File ADDSTAGEAREA_DIR = join(STAGEAREA_DIR, "addStageArea");
    /**
     * A directory to stage file for removal.
     */
    public static final File REMOVESTAGEAREA_DIR = join(STAGEAREA_DIR, "removeStageArea");
    /**
     * A file whose contend is a file path point to the current branch head file.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /**A directory containing all branch head files
    which are named in their branch name and write commitID in the file.*/
    public static final File BRANCHS = join(GITLET_DIR, "branchHeads");


    /*Creates a new Gitlet version-control system in the current directory.
    This system will automatically start with one commit:
    a commit that contains no files and has the commit message initial commit.
    It will have a single branch: master, which initially points to this initial commit,
    and master will be the current branch. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.err.println("A Gitlet version-control system already " +
                    "exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();

            BRANCHS.mkdir();
            moveHeadto("master");

            COMMIT_DIR.mkdir();
            //Create a commit, and save into Commit_Dir;
            Commit initCommit = new Commit();
            BLOBS_DIR.mkdir();
            STAGEAREA_DIR.mkdir();
            ADDSTAGEAREA_DIR.mkdir();
            REMOVESTAGEAREA_DIR.mkdir();
        }

    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * For this reason, adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites the previous entry
     * in the staging area with the new contents.
     * The staging area should be somewhere in .gitlet.
     * The file will no longer be staged for removal (see gitlet rm),
     * if it was at the time of the command.
     * If the current working version of the file is identical to the version in current commit,
     * do not stage it to be added,
     * and remove it from the staging area if it is already there
     */
    public static void gitletadd(String fileName) {
        File fileInCWD = join(CWD, fileName);
        if (!fileInCWD.exists()) {
            System.err.println("File does not exist.");
            return;
        }

        if (identical(CWD, fileName)) {
            join(ADDSTAGEAREA_DIR, fileName).delete();
        } else {
            stageFileForAdd(fileName);
        }
        join(REMOVESTAGEAREA_DIR, fileName).delete();
    }

    /**
     * Stage a file from CWD to stageArea fore add.
     */
    private static void stageFileForAdd(String fileName) {
        File fileInCWD = join(CWD, fileName);
        byte[] fileContent = readContents(fileInCWD);
        String blobID = sha1(fileContent);
        String shortBlobID = blobID.substring(0, 6);

        File stageFile = join(ADDSTAGEAREA_DIR, fileName);
        writeContents(stageFile, blobID);

        File singleBlobDir = join(BLOBS_DIR, shortBlobID);
        singleBlobDir.mkdir();
        File blobfile = join(singleBlobDir, blobID);
        writeContents(blobfile, fileContent);
    }

    /**Make a commit and save into file.*/
    public static void gitletCommit(String message) {
        Commit newcommit = new Commit(message, null);
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for removal
     * and remove the file from the working directory if the user has not already done so.
     * (do not remove it unless it is tracked in the current commit).
     */
    public static void gitletrm(String fileName) {
        File sameFileInAddStage = join(ADDSTAGEAREA_DIR, fileName);

        if (!sameFileInAddStage.exists() && !isTracked(fileName)) {
            System.err.println("No reason to remove the file.");
            return;
        }

        if (sameFileInAddStage.exists()) {
            sameFileInAddStage.delete();
        }

        if (isTracked(fileName)) {
            //stage it for removal.
            String blobID = head().getBlob(fileName);
            File stageFile = join(REMOVESTAGEAREA_DIR, fileName);
            writeContents(stageFile, blobID);
            //delete it form current working directory.
            restrictedDelete(join(CWD, fileName));
        }
    }

    /**
     * Print log form HEAD to initial commit.
     * log information: commit, time, message
     */
    public static void log() {
        String commitID = headCommitID();
        while (commitID != null) {
            printlog(commitID);
            commitID = Commit.getCommit(commitID).getParent();
        }
    }

    /**
     * Print log information: commit, time, message
     * take a commitID as argment.
     */
    private static void printlog(String commitID) {
        Commit commit = getCommit(commitID);
        String secondParent = commit.getSecondParent();
        System.out.println("===");
        System.out.println("commit " + commitID);
        if (secondParent != null) {
            System.out.println("Merge:" + " " + commitID.substring(0, 7)
                    + " " + secondParent.substring(0, 7));
        }
        System.out.println("Date: " + commit.getTime());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /**
     * Print logs of all the commits ever made.
     */
    public static void globallog() {
        List<String> commitFileNames = fileNamesIn(COMMIT_DIR);
        for (String commitFileName : commitFileNames) {
            String longCommitID = getlongCommitID(commitFileName);
            printlog(longCommitID);
        }
    }

    /**
     * Prints out the commit ids of all commits that have the given commit message, one per line.
     */
    public static void find(String[] args) {
        if (args.length != 2) {
            System.err.println("please provide a message.");
            return;
        }
        String message = args[1];
        List<String> commitFileNames = fileNamesIn(COMMIT_DIR);
        int findTimes = 0;
        for (String commitFileName : commitFileNames) {
            String longCommitID = getlongCommitID(commitFileName);
            String commitMessage = Commit.getCommit(longCommitID).getMessage();
            if (commitMessage.equals(message)) {
                System.out.println(longCommitID);
                findTimes++;
            }
        }
        if (findTimes == 0) {
            System.err.println("Found no commit with that message.");
        }
    }

    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch (just as in real Git).
     */
    public static void branch(String[] args) {
        if (args.length != 2) {
            System.err.println("Error: please provide a branchName");
            return;
        }
        String branchName = args[1];
        File branchHeads = join(BRANCHS, branchName);
        if (branchHeads.exists()) {
            System.err.println("A branch with that name already exists.");
            return;
        } else {
            writeContents(branchHeads, headCommitID());
        }
    }

    /**
     * Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch.
     */
    public static void rmBranch(String[] args) {
        if (args.length != 2) {
            System.err.println("Error: please provide a branchName");
            return;
        }
        String branchName = args[1];
        File branchHeadFile = join(BRANCHS, branchName);
        if (branchHeadFile.exists()) {
            if (activeBranch().getName().equals(branchName)) {
                System.err.println("Cannot remove the current branch.");
                return;
            } else {
                branchHeadFile.delete();
            }
        } else {
            System.err.println("A branch with that name does not exist.");
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     * An example of the exact format it should follow is as follows.
     */
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
        List<String> addStageFileNames = plainFilenamesIn(ADDSTAGEAREA_DIR);
        for (String fileName : addStageFileNames) {
            System.out.println(fileName);
        }

        System.out.println("\n" + "=== Removed Files ===");
        List<String> rmStageFileNames = plainFilenamesIn(REMOVESTAGEAREA_DIR);
        for (String fileName : rmStageFileNames) {
            System.out.println(fileName);
        }

        System.out.println("\n" + "=== Modifications Not Staged For Commit ===");

        //Tracked in the current commit, changed in the working directory, but not staged; or
        List<String> fileNamesInCWD = plainFilenamesIn(CWD);
        for (String fileName : fileNamesInCWD) {
            if (isTracked(fileName) && !identical(CWD, fileName)
                    && !addStageFileNames.contains(fileName)) {
                System.out.println(fileName + "(modified)");
            }
        }

        //Staged for addition, but with different contents than in the working directory; or
        //Staged for addition, but deleted in the working directory;
        for (String fileName : addStageFileNames) {
            File fileInCWD = join(CWD,fileName);
            File addStage = join(ADDSTAGEAREA_DIR,fileName);
            String addVersion = readContentsAsString(addStage);
            if (!fileInCWD.exists()) {
                System.out.println(fileName + "(deleted)");
            } else if (!(getBlob(CWD, fileName).equals(addVersion))) {
                System.out.println(fileName + "(modified)");
            }
        }

        //Not staged for removal, but tracked in the current commit
        // and deleted from the working directory.
        Set<String> currentCommitFiles = head().fileNameSet();
        if (currentCommitFiles != null) {
            for (String fileName : currentCommitFiles) {
                File removeStage = join(REMOVESTAGEAREA_DIR,fileName);
                if (!join(CWD, fileName).exists() && !removeStage.exists()) {
                    System.out.println(fileName + "(deleted)");
                }
            }
        }

        //for files present in the working directory but neither staged for addition nor tracked.
        System.out.println("\n" + "=== Untracked Files ===");
        for (String fileName : fileNamesInCWD) {
            if (!isTracked(fileName) && !join(ADDSTAGEAREA_DIR, fileName).exists()) {
                System.out.println(fileName);
            }
        }

        System.out.println();

    }

    /**
     * Given a fileName(not SHA1), check if it's tracked in current commit
     */
    private static boolean isTracked(String fileName) {
        if (head() == null) {
            return false;
        }
        return head().getBlob(fileName) != null;
    }

    /**
     * Return if a file's directory version is identical with its current commit version
     */
    private static boolean identical(File directory, String fileName) {
        String directoryVersion = getBlob(directory, fileName);
        String headCommitVersion = head().getBlob(fileName);
        if (Objects.equals(directoryVersion, headCommitVersion) == true) {
            return true;
        }
        return false;
    }

    /**
     * Return a blob(SHA1 value) of a file in CWD according to its content.
     */
    private static String getBlob(File directory, String fileName) {
        File thefile = join(directory, fileName);
        if (thefile.isFile()) {
            return sha1(readContents(thefile));
        } else {
            return null;
        }

    }

    /**
     * Checkout has 3 possible use cases.
     * 1.Takes the version of the file as it exists in the head commit
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     *
     * 2.Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     *
     * 3.Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     *
     * At the end, the given branch will now be considered the current branch (HEAD).
     * Tracked files in the current branch but not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch.
     */
    public static void gitletCheckout(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            checkoutFileFromCommit(head(), args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkoutFileFromID(args[1], args[3]);
        } else if (args.length == 2) {
            checkoutbranch(args[1]);
        } else {
            System.err.println("Incorrect operands.");
        }
    }

    /**
     * Checkout a file form a commitID.
     */
    private static void checkoutFileFromID(String commitID, String fileName) {
        String longCommitID = getlongCommitID(commitID);
        Commit commit = getCommit(longCommitID);
        checkoutFileFromCommit(commit, fileName);
    }

    /**
     * Chekcout a file form a commit object.
     */
    private static void checkoutFileFromCommit(Commit commit, String fileName) {
        if (commit == null) {
            System.err.println("No commit with that id exists.");
            return;
        }
        String blobID = commit.getBlob(fileName);
        if (blobID == null) {
            System.err.println("File does not exist in that commit.");
            return;
        }
        String shortBlobID = blobID.substring(0, 6);
        byte[] blobContent = readContents(join(BLOBS_DIR, shortBlobID, blobID));
        File CWDfile = join(CWD, fileName);
        writeContents(CWDfile, blobContent);
    }

    /**
     * Checkout branch from a branchName.
     */
    private static void checkoutbranch(String branchName) {
        File branch = join(BRANCHS, branchName);
        if (branch.exists()) {
            if (branchName.equals(activeBranch().getName())) {
                System.err.println("No need to checkout the current branch.");
                return;
            }
            String branchHeadCommitId = readContentsAsString(branch);
            boolean result = checkoutAllfilesFromID(branchHeadCommitId);
            if (result == true) {
                moveHeadto(branchName);
            }
        } else {
            System.err.println("No such branch exists.");
        }
    }

    /**
     * CheckoutAllfiles from a long commitID and clear stageArea
     */
    private static boolean checkoutAllfilesFromID(String longCommitID) {
        Commit branchHead = getCommit(longCommitID);
        if (branchHead == null) {
            System.err.println("No commit with that id exists.");
            return false;
        }
        List<String> filenamesInCWD = plainFilenamesIn(CWD);
        Set<String> branchHeadCommitFiles = branchHead.fileNameSet();

        if (branchHeadCommitFiles == null) {
            for (String file : filenamesInCWD) {
                if (isTracked(file)) {
                    join(CWD, file).delete();
                }
            }
        }

        if (branchHeadCommitFiles != null) {
            for (String file : filenamesInCWD) {
                if (!isTracked(file) & branchHeadCommitFiles.contains(file)) {
                    System.err.println("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                    return false;
                }
            }
            for (String file : branchHeadCommitFiles) {
                checkoutFileFromCommit(branchHead, file);
            }
            for (String file : filenamesInCWD) {
                if (isTracked(file) && !branchHeadCommitFiles.contains(file)) {
                    join(CWD, file).delete();
                }
            }
        }
        clear(ADDSTAGEAREA_DIR);
        return true;
    }

    /**
     * Write a branch name into the HEAD file.
     */
    private static void moveHeadto(String branchName) {
        File branch = join(BRANCHS, branchName);
        writeContents(HEAD, GITLET_DIR.getName(), "/", BRANCHS.getName(), "/", branch.getName());
    }

    /**
     * Clear all files in add or remove stageArea
     */
    public static void clear(File directory) {
        if (directory.isDirectory() &&
                (directory.equals(ADDSTAGEAREA_DIR) || directory.equals(REMOVESTAGEAREA_DIR))) {
            List<String> fileNames = plainFilenamesIn(directory);
            for (String fileName : fileNames) {
                File file = join(directory, fileName);
                if (file.isFile()) {
                    file.delete();
                }
            }
        } else {
            System.err.println("Error: can only clear add or remove stageArea.");
        }
    }

    /**
     * Returns a list of the names of all files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    public static List<String> fileNamesIn(File dir) {
        String[] files = dir.list();
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    public static void gitletreset(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect operands.");
            return;
        }
        String commitID = args[1];
        String longCommitID = getlongCommitID(commitID);
        if (longCommitID == null) {
            System.err.println("No commit with that id exists.");
            return;
        }
        if (checkoutAllfilesFromID(longCommitID) == true) {
            writeContents(activeBranch(), longCommitID);
        }
    }

    /**Merges files from the given branch into the current branch.
     * This method is a bit complicated, so here’s a more detailed description. */
    public static void gitletmerge(String branchName) {
        //Given branch: name, ID, commit
        File givenBranch = join(BRANCHS, branchName);
        if (!givenBranch.exists()) {
            System.err.println("A branch with that name does not exist.");
            return;
        }
        String givenID = readContentsAsString(givenBranch);
        if (givenID.equals(headCommitID())) {
            System.err.println("Cannot merge a branch with itself.");
            return;
        }

        if (ADDSTAGEAREA_DIR.list().length != 0 || REMOVESTAGEAREA_DIR.list().length != 0) {
            System.err.println("You have uncommitted changes.");
            return;
        }

        //splitPoint: ID
        String splitPoint = splitPoint(headCommitID(), givenID);
        if (splitPoint.equals(givenID)) {
            System.err.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPoint.equals(headCommitID())) {
            checkoutbranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        TreeMap<String,String> fileTochange = fileCheck(splitPoint,headCommitID(),givenID);

        //If an untracked file in the current commit would be overwritten or deleted by the merge,
        // print There is an untracked file in the way; delete it,
        // or add and commit it first. and exit; perform this check before doing anything else.
        List<String> filesInCWD = plainFilenamesIn(CWD);
        for (String file : filesInCWD) {
            if (fileTochange.containsKey(file) && !isTracked(file)) {
                System.err.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for (String file : fileTochange.keySet()) {
            String operation = fileTochange.get(file);
            if (operation.equals("add")) {
                checkoutFileFromID(givenID, file);
                gitletadd(file);
            } else if (operation.equals("remove")) {
                gitletrm(file);
            } else if (operation.equals("conflict")) {
                File fileInCWD = join(CWD, file);
                String headContent = readContensFromeBlob(head().getBlob(file));
                String givenContent = readContensFromeBlob(getCommit(givenID).getBlob(file));
                writeContents(fileInCWD, "<<<<<<< HEAD\n" +
                        headContent + "=======\n" + givenContent + ">>>>>>>\n");
                System.out.println("Encountered a merge conflict.");
                gitletadd(file);
            }
        }
        //If merge would generate an error because the commit that it does has no changes in it,
        // just let the normal commit error message for this go through.
        String logMessage = "Merged " + givenBranch.getName() +
                " into " + activeBranch().getName() + ".";
        Commit mergedCommit = new Commit(logMessage, givenID);
    }

    /**
     * Given a blobID, return its contents in String format.
     * if the content has no "/n", add "/n" adn then return.
     */
    private static String readContensFromeBlob(String blobID) {
        if (blobID == null) {
            return null;
        }
        String shortID = blobID.substring(0, 6);
        File blob = join(BLOBS_DIR, shortID, blobID);
        String content = readContentsAsString(blob);
        if (content.contains("\n")) {
            return content;
        } else {
            String contentWithNewline = content + "\n";
            return contentWithNewline;
        }
    }

    /**Return a set of files In multiple given commit ID.*/
    private static TreeSet<String> fileSet(String... commitID) {
        TreeSet<String> fileSet = new TreeSet<>();
        for (String id : commitID) {
            Set<String> files = getCommit(id).fileNameSet();
            if (files == null) {
                continue;
            }
            for (String file : files) {
                fileSet.add(file);
            }
        }
        return fileSet;
    }

    /**Check files of the 3 commit, and return a TreeMap containing files need to be changed.
     * key: fileName; value: "add", "rm" or "conflict".*/
    private static TreeMap<String,String> fileCheck (String spID, String headID, String branchID) {
        TreeSet<String> fileSet = fileSet(spID,headID,branchID);
        TreeMap<String, String> fileTochange = new TreeMap<>();
        for (String file : fileSet) {
            String splitBlob = getCommit(spID).getBlob(file);
            String headBlob = getCommit(headID).getBlob(file);
            String givenBlob = getCommit(branchID).getBlob(file);
            /*1.Any files that have been modified in the given branch since the split point
            but not modified in the current branch since the split point should be changed to their versions in the given branch*/
            if (!Objects.equals(splitBlob, givenBlob) && Objects.equals(splitBlob, headBlob)) {
                fileTochange.put(file, "add");
            }
            /*3.Any files that have been modified in both the current and given branch in the same way
            (i.e., both files now have the same content or were both removed) are left unchanged by the merge.*/
            if (!Objects.equals(splitBlob, headBlob) && !Objects.equals(splitBlob, givenBlob)) {
                /*8.Any files modified in different ways in the current and given branches are in conflict. */
                if (!Objects.equals(headBlob, givenBlob)) {
                    fileTochange.put(file, "conflict");
                }
            }
            /*5.Any files that were not present at the split point and are present only in the given branch should be checked out and staged.*/
            if (splitBlob == null && headBlob == null && givenBlob != null) {
                fileTochange.put(file, "add");
            }
            /*6.Any files present at the split point, unmodified in the current branch, and absent in the given branch should be removed (and untracked).*/
            if (splitBlob != null && Objects.equals(splitBlob, headBlob) && givenBlob == null) {
                fileTochange.put(file, "remove");
            }
            //Stay as they are:
            /*2.Any files that have been modified in the current branch but not in the given branch since the split point should stay as they are. */
            /*4.Any files that were not present at the split point and are present only in the current branch should remain as they are.*/
            /*7.Any files present at the split point, unmodified in the given branch, and absent in the current branch should remain absent.*/
        }
        return fileTochange;
    }
}
