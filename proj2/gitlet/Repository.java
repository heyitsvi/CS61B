package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Repository contains all of the operational commands in Gitlet.
 *  Includes methods for setting up the .gitlet directory structure and staging area.
 *
 *  @author Vivek Singh
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOB_DIR = join(OBJECTS_DIR, "blobs");
    public static final File TREE_DIR = join(OBJECTS_DIR, "trees");
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REF_DIR, "heads");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File INDEX = join(GITLET_DIR, "index");
    public static final File MASTER = join(HEADS_DIR, "master");

    public static List<String> listOfFileNamesCWD;

    /** Check if a Git Directory already exists */
    public static boolean checkGitDirExists() {
        return GITLET_DIR.exists();
    }

    /** Check if Index (i.e Staging area) already exists */
    public static boolean checkIndexExists() {
        return INDEX.exists();
    }

    /** Check if the same branch already exists in the directory */
    public static boolean branchExists(String branch) {
        /**List<String> listOfFileNames = plainFilenamesIn(HEADS_DIR);
        return listOfFileNames.contains(branch);*/
        return fileExistsInDir(branch, HEADS_DIR);
    }

    /** Check if File exists in CWD */
    public static boolean checkFileExists(String fileName) {
        listOfFileNamesCWD = plainFilenamesIn(CWD);

        return listOfFileNamesCWD.contains(fileName);
    }

    /** Check if the staging area is empty */
    public static boolean isIndexEmpty() {
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        return stagingTree.map.isEmpty();
    }

    /** Check if the checkout branch is the current active branch */
    public static boolean isCurrentBranch(String branch) {
        return getActiveBranch().equals(branch);
    }

    /** Checks in Index to see if the file has been staged */
    public static boolean fileExistsInIndex(String fileName) {
        gitlet.Tree t = readObject(INDEX, gitlet.Tree.class);
        return t.map.containsKey(fileName);
    }
    /** Check if the same file (i.e.with the same SHA val) exists in the latest commit. */
    public static boolean sameFileInLatestCommit(String fileName, String SHA) {
        String shaInMaster = returnHEADPointer();
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(shaInMaster);

        if (latestCommitTreeObj == null || !latestCommitTreeObj.map.containsKey(fileName)) {
            return false;
        }

        return latestCommitTreeObj.map.get(fileName).equals(SHA);
    }

    /** Create an object file inside Gitlet with the file name equal to the sha value of its contents.
     * Can be for a commit, blob or tree object.
     **/
    public static File createObjectFile(Object o, File dir) {
        String sha = sha1(o);
        File shaObj = join(dir, sha);
        try {
            shaObj.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return shaObj;
    }

    /** Create a commit obj file in the Commit DIR */
    public static File createCommitObj(byte[] o) {
        return createObjectFile(o, COMMIT_DIR);
    }

    /** Create a blob obj file in the Blob DIR */
    public static File createBlobObj(Object o) {
        return createObjectFile(o, BLOB_DIR);
    }

    /** Create a blob obj file in the Tree DIR */
    public static File createTreeObj(byte[] o) {
        return createObjectFile(o, TREE_DIR);
    }

    /** Get contents as byte array from file in CWD */
    public static byte[] getContentsFromFile(String fileName) {
        File filePath = join(CWD, fileName);
        return readContents(filePath);
    }

    /** Get the commit object from a directory */
    public static gitlet.Commit getCommitObj(String fileName, File DIR) {
        File filePath = join(DIR, fileName);
        return readObject(filePath, gitlet.Commit.class);
    }

    /** Get the tree object from a commit */
    public static gitlet.Tree getCommitTreeObj(gitlet.Commit c) {
        String treeSHA = c.getTree();

        if (treeSHA == null) {
            return null;
        }
        File filePath = join(TREE_DIR,treeSHA);
        return readObject(filePath, gitlet.Tree.class);
    }

    /** Get the latest commit obj */
    public static gitlet.Commit getLatestCommitObj(String fileName) {
        return getCommitObj(fileName, COMMIT_DIR);
    }

    /** Get the tree object from the latest commit */
    public static gitlet.Tree getLatestCommitTreeObj(String fileName) {
        return getCommitTreeObj(getLatestCommitObj(fileName));
    }

    /** Clear the staging area by removing all the (fileName : SHA val) mappings */
    public static void clearStagingArea() {
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        stagingTree.map.clear();
        writeObject(INDEX, stagingTree);
    }

    /** Add file to INDEX unless the file remains unchanged from previous commit
     * @param fileName name of the file
     */
    public static void addToIndex(String fileName) {
        gitlet.Tree indexObj = readObject(INDEX, gitlet.Tree.class);

        String contents = readContentsAsString(join(CWD,fileName));

        String blobSHA = sha1(contents);

        if (isIndexEmpty()) {
            boolean fileExists = sameFileInLatestCommit(fileName, blobSHA);
            if (fileExists) {
                System.exit(0);
            }
        }

        if (!indexObj.map.containsKey(fileName) || !indexObj.map.get(fileName).equals(blobSHA)){
            File blobObjFile = createBlobObj(contents);
            writeContents(blobObjFile, contents);
            indexObj.map.put(fileName, blobSHA);
            writeObject(INDEX, indexObj);
        }

    }

    public static gitlet.Tree mergeObjs(gitlet.Tree o1,gitlet.Tree o2) {
        gitlet.Tree t = gitlet.Tree.createTree();
        if (o1 == null) {
            t.map.putAll(o2.map);
            return t;
        }

        t.map.putAll(o1.map);
        t.map.putAll(o2.map);
        return t;
    }

    /** Create a new commit with the given message */
    public static void createANewCommit(String msg) {
        String prevCommitSHA = returnHEADPointer();
        gitlet.Tree prevCommitTreeObj = getLatestCommitTreeObj(prevCommitSHA);
        gitlet.Tree indexTreeObj = readObject(INDEX, gitlet.Tree.class);
        removeFilesFromCommit(indexTreeObj.removeSet, prevCommitTreeObj);


        gitlet.Tree newTreeObj = mergeObjs(prevCommitTreeObj, indexTreeObj);
        byte[] serialiseTreeObj = serialize(newTreeObj);
        String newObjSHA = sha1(serialiseTreeObj);

        File path = createTreeObj(serialiseTreeObj);
        writeObject(path, newTreeObj);

        gitlet.Commit newCommit = gitlet.Commit.createCommit(msg, prevCommitSHA, new Date(), newObjSHA);
        byte[] serialisedCommit = serialize(newCommit);

        File newCommitFile = createCommitObj(serialisedCommit);
        writeObject(newCommitFile, newCommit);

        updateActiveBranch(serialisedCommit);
        clearStagingArea();
    }

    /** Creates a new branch in the HEADS_DIR */
    public static void createBranch(String branch) {
        String activeBranchID = returnHEADPointer();

        File newBranch = join(HEADS_DIR, branch);

        try {
            newBranch.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        writeContents(newBranch, activeBranchID);

    }

    /** Overwrite the file in a directory with its previous version from a commit */
    public static void overwriteFile(String fileName, String savedFile, File DIR) {
        File f = join(DIR, fileName);
        String contents = readContentsAsString(join(BLOB_DIR, savedFile));
        writeContents(f, contents);
    }

    /** Check if the file exists in the given directory */
    public static boolean fileExistsInDir(String fileName, File dir) {
        File f =  join(dir, fileName);
        return f.exists();
    }
    public static void checkoutFile(String commitID, String fileName) {
        if (!fileExistsInDir(commitID, COMMIT_DIR)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        gitlet.Commit commitObj = readObject(join(COMMIT_DIR, commitID), gitlet.Commit.class);
        gitlet.Tree commitTreeObj = readObject(join(TREE_DIR, commitObj.getTree()), gitlet.Tree.class);

        if (commitTreeObj != null) {
            if (commitTreeObj.map.containsKey(fileName)) {
                overwriteFile(fileName, commitTreeObj.map.get(fileName), CWD);
            } else {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
        }
    }
    public static void createFileWithContents(File fileName, File contentPath) {
        try {
            fileName.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String contents = readContentsAsString(contentPath);

        writeContents(fileName, contents);
    }

    public static void checkoutBranch(String branch) {
        String commitID = readContentsAsString(join(HEADS_DIR, branch));
        gitlet.Tree branchTreeObj = getCommitTreeObj(getCommitObj(commitID, COMMIT_DIR));
        gitlet.Tree latestTreeObj = getLatestCommitTreeObj(returnHEADPointer());
        Set<String> trackedFiles = latestTreeObj.map.keySet();

        if (branchTreeObj != null) {
             Set<String> branchFiles = branchTreeObj.map.keySet();
             List<String> listOfFiles = plainFilenamesIn(CWD);
             for (String file : branchFiles) {
                 if (listOfFiles.contains(file)) {
                     overwriteFile(file, branchTreeObj.map.get(file), CWD);
                 } else {
                     String fileSHA = branchTreeObj.map.get(file);
                     File contentPath = join(BLOB_DIR, fileSHA);
                     createFileWithContents(join(CWD,file), contentPath);
                 }
             }

            for (String file : trackedFiles) {
                if (!branchFiles.contains(file)) {
                    restrictedDelete(file);
                }
            }
        }

        clearStagingArea();
        gitlet.Repository.changeActiveBranch(branch);
    }
    /** Check if the latest commit in the current branch tracks this file */
    public static boolean fileInHEADCommit(String fileName) {
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(returnHEADPointer());
        if (latestCommitTreeObj == null) {
            return false;
        }
        return latestCommitTreeObj.map.containsKey(fileName);
    }

    /** Remove the files from the commit if they are staged for removal
     * @param removeFiles Set containing the names of the files that need to be removed.
     * @param commitTree The Tree Object that
     * */
    public static void removeFilesFromCommit(Set<String> removeFiles, gitlet.Tree commitTree) {
        for (String file : removeFiles) {
            commitTree.map.remove(file);
        }
        removeFiles.clear();
    }

    public static String shaOfFile(String fileName) {
        File f = join(CWD, fileName);
        return sha1(readContents(f));
    }

    /** Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit, stage it for removal
     *  and remove the file from the working directory if the user has not already done so.
     * @param fileName Name of the file that needs to be staged for removal.
     */

    public static void removeFile(String fileName) {
        gitlet.Tree t = readObject(INDEX, gitlet.Tree.class);

        if (fileInHEADCommit(fileName)) {
            t.removeSet.add(fileName);
            restrictedDelete(join(CWD, fileName));
        }

        if (fileExistsInIndex(fileName)) {
            t.map.remove(fileName);
        }

        writeObject(INDEX, t);
    }

    /** Format the date to the specified format
     * @param d Date object that needs to formatted.
     * */
    public static String formatDate(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return df.format(d);
    }

    /** Generate the log msg from the commit object
     * @param c Commit object that contains info to display.
     * @return Log Message
     * */
    public static String generateLogMsg(gitlet.Commit c) {
        String commit = sha1(serialize(c));
        String msg = c.getMsg();
        Date date = c.getDate();
        String formattedDate = formatDate(date);

        return "=== \n" +
                "commit " + commit + "\n" +
                "Date: " + formattedDate + "\n" +
                msg + "\n";
    }

    /** Traverse commits starting from the HEAD commit to the initial commit and display their log msg */
    public static void printLog() {
        String prevCommitSHA = returnHEADPointer();

        gitlet.Commit prevCommitObj = getLatestCommitObj(prevCommitSHA);

        while (prevCommitObj.getParent() != null) {
            System.out.println(generateLogMsg(prevCommitObj));
            prevCommitObj = getLatestCommitObj(prevCommitObj.getParent());
        }
        System.out.println(generateLogMsg(prevCommitObj));
    }

    /** Get the list of all commit files in lexicographical order and print the commits */
    public static void printAllCommits() {
        List<String> files = plainFilenamesIn(COMMIT_DIR);

        for (String file : files) {
            File f = join(COMMIT_DIR, file);
            gitlet.Commit commitObj = readObject(join(COMMIT_DIR, file), gitlet.Commit.class);
            System.out.println(generateLogMsg(commitObj));
        }
    }

    /** Get the list of all commit files in lexicographical order and print the commits
     * that contain the specified msg.
     * @param msg The msg to search for
     * */
    public static void findMsgInCommits(String msg) {
        List<String> files = plainFilenamesIn(COMMIT_DIR);
        boolean flag = false;
        for (String file : files) {
            File f = join(COMMIT_DIR, file);
            gitlet.Commit commitObj = readObject(join(COMMIT_DIR, file), gitlet.Commit.class);

            if (commitObj.getMsg().equals(msg)) {
                System.out.println(file);
                flag = true;
            }
        }

        if (!flag) {
            System.out.println("Found no commit with that message.");
        }

    }

    /** Display general into about the repository like branches, staged files, files staged for removal
     *  modified files and untracked files.
     */
    public static void printStatus() {
        List<String> files = plainFilenamesIn(HEADS_DIR);
        String branch = getActiveBranch();
        System.out.println("=== Branches ===");
        for (String file : files) {
            if (file.equals(branch)) {
                System.out.println("*" + file);
            } else {
                System.out.println(file);
            }
        }
        System.out.println(" ");
        gitlet.Tree latestObj = readObject(INDEX, gitlet.Tree.class);
        System.out.println("=== Staged Files ===");
        if (latestObj != null) {
            for (String key : latestObj.map.keySet()) {
                System.out.println(key);
            }
        }
        System.out.println(" ");
        System.out.println("=== Removed Files ===");
        if (latestObj != null) {
            for (String key : latestObj.removeSet) {
                System.out.println(key);
            }
        }
        System.out.println(" ");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(" ");
        System.out.println("=== Untracked Files ===");
        System.out.println(" ");
    }


    /** Updates Master to point to the latest commit
     * @param c The commit object you want the master to point at.
     * */

    /**public static void updateMaster(Object c) {
        String sha = sha1(c);
        writeContents(MASTER, sha);
    }
    public static String returnMasterPointer() {
        return readContentsAsString(MASTER);
    }*/

    /** Update the active branch to point to latest commit */
    public static void updateActiveBranch(Object c) {
        String branch = getActiveBranch();
        String sha = sha1(c);
        writeContents(join(HEADS_DIR, branch), sha);
    }

    /** Checkout to the given Branch */
    public static void changeActiveBranch(String branch) {
        writeContents(HEAD, branch);
    }

    /** Get the branch that HEAD is pointing at */
    public static String getActiveBranch() {
        return readContentsAsString(HEAD);
    }

    /** Return the most recent commit id of the branch HEAD points at */
    public static String returnHEADPointer() {
        String branch = readContentsAsString(HEAD);
        return readContentsAsString(join(HEADS_DIR, branch));
    }

    public static void setupStagingArea(String fileName) {

        /* Create the index file in .gitlet dir. */
        try {
            INDEX.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* Create the Index (i.e. Staging) Tree object */
        gitlet.Tree indexObj =  gitlet.Tree.createTree();

        String contents = readContentsAsString(join(CWD, fileName));
        /* Get the contents from file, create a blob object and write contents to that object */

        File blobObjFile = createBlobObj(contents);

        writeContents(blobObjFile, contents);

        /* Map the file name to the sha of the blob obj and write to INDEX */

        String blobSHA = sha1(contents);

        indexObj.map.put(fileName, blobSHA);

        writeObject(INDEX, indexObj);

     }

    /**
     * Sets up the .gitlet folder dir in the CWD
     * The .gitlet directory.
     * .gitlet
     *      objects
     *          commits (dir)
     *          blobs   (dir)
     *          trees   (dir)
     *      refs
     *          heads
     *              master (file)
     *      HEAD (file)
     *      INDEX (file)
     *
     * Creates the initial commit
     * Creates the head, master references
     */

    public static void setupGitlet() {

        /* Initialise files and directories for gitlet. */
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        TREE_DIR.mkdir();
        REF_DIR.mkdir();
        HEADS_DIR.mkdir();
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            MASTER.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* Create the first commit. */
        gitlet.Commit c = gitlet.Commit.initialCommit();

        /* Serialise the commit, hash it using SHA1 and create a commit object. */
        byte[] serialisedCommit = serialize(c);

        File f = createCommitObj(serialisedCommit);

        writeContents(HEAD, "master");

        /* update master to point to latest commit. */
        updateActiveBranch(serialisedCommit);

        /* Save the commit object in objects/commits dir. */
        writeObject(f, c);
    }
}
