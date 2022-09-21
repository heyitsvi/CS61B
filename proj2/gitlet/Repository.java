package gitlet;

import java.io.File;
import java.io.IOException;
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
    public static File createObjectFile(byte[] o, File dir) throws IOException {
        String sha = sha1(o);
        File shaObj = join(dir, sha);
        shaObj.createNewFile();

        return shaObj;
    }

    /** Create a commit obj file in the Commit DIR */
    public static File createCommitObj(byte[] o) throws IOException {
        return createObjectFile(o, COMMIT_DIR);
    }

    /** Create a blob obj file in the Blob DIR */
    public static File createBlobObj(byte[] o) throws IOException {
        return createObjectFile(o, BLOB_DIR);
    }

    /** Create a blob obj file in the Tree DIR */
    public static File createTreeObj(byte[] o) throws IOException {
        return createObjectFile(o, TREE_DIR);
    }

    /** Get contents as byte array from file in CWD */
    public static byte[] getContentsFromFile(String fileName) {
        File filePath = join(CWD, fileName);
        return readContents(filePath);
    }

    /** Get the latest commit obj */
    public static gitlet.Commit getLatestCommitObj(String fileName) {
        File filePath = join(COMMIT_DIR, fileName);
        return readObject(filePath, gitlet.Commit.class);
    }

    /** Get the tree object from the latest commit */
    public static gitlet.Tree getLatestCommitTreeObj(String fileName) {
        gitlet.Commit commitObj = getLatestCommitObj(fileName);
        String treeSHA = commitObj.getTree();

        if (treeSHA == null) {
            return null;
        }
        File filePath = join(TREE_DIR,treeSHA);
        return readObject(filePath, gitlet.Tree.class);
    }

    /** Clear the staging area by removing all the (fileName : SHA val) mappings */
    public static void clearStagingArea() {
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        stagingTree.map.clear();
        writeObject(INDEX, stagingTree);
    }

    /** Add file to INDEX unless the file remains unchanged from previous commit
     * @param fileName name of the file
     * @throws IOException
     */
    public static void addToIndex(String fileName) throws IOException {
        gitlet.Tree indexObj = readObject(INDEX, gitlet.Tree.class);

        byte[] serialisedBlob = serialize(getContentsFromFile(fileName));
        String blobSHA = sha1(serialisedBlob);

        if (isIndexEmpty()) {
            boolean fileExists = sameFileInLatestCommit(fileName, blobSHA);
            if (fileExists) {
                System.exit(0);
            }
        }

        if (!indexObj.map.containsKey(fileName) || !indexObj.map.get(fileName).equals(blobSHA)){
            File blobObjFile = createBlobObj(serialisedBlob);
            writeObject(blobObjFile, serialisedBlob);
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
    public static void createANewCommit(String msg) throws IOException {
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

    public static boolean fileInHEADCommit(String fileName) {
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(returnHEADPointer());
        if (latestCommitTreeObj == null) {
            return false;
        }
        return latestCommitTreeObj.map.containsKey(fileName);
    }

    public static void removeFilesFromCommit(Set<String> removeFiles, gitlet.Tree commitTree) {
        for (String file : removeFiles) {
            commitTree.map.remove(file);
        }
    }

    public static String shaOfFile(String fileName) {
        File f = join(CWD, fileName);
        return sha1(readContents(f));
    }

    /** Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit, stage it for removal
     *  and remove the file from the working directory if the user has not already done so.
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

    public static String generateLogMsg(gitlet.Commit c) {
        String commit = sha1(serialize(c));
        String msg = c.getMsg();
        Date date = c.getDate();

        return "=== \n" +
                "commit " + commit + "\n" +
                "Date: " + date + "\n" +
                msg + "\n";
    }

    public static void printLog() {
        String prevCommitSHA = returnHEADPointer();

        gitlet.Commit prevCommitObj = getLatestCommitObj(prevCommitSHA);

        while (prevCommitObj.getParent() != null) {
            System.out.println(generateLogMsg(prevCommitObj));
            prevCommitObj = getLatestCommitObj(prevCommitObj.getParent());
        }
        System.out.println(generateLogMsg(prevCommitObj));
    }


    /** Updates Master to point to the latest commit
     * @param c The commit object you want the master to point at.
     * */
    public static void updateMaster(Object c) {
        String sha = sha1(c);
        writeContents(MASTER, sha);
    }
    public static String returnMasterPointer() {
        return readContentsAsString(MASTER);
    }

    public static void updateActiveBranch(Object c) {
        String branch = getActiveBranch();
        String sha = sha1(c);
        writeContents(join(HEADS_DIR, branch), sha);
    }

    public static String getActiveBranch() {
        return readContentsAsString(HEAD);
    }

    public static String returnHEADPointer() {
        String branch = readContentsAsString(HEAD);
        return readContentsAsString(join(HEADS_DIR, branch));
    }

    /*public static void createStagingTree() {
        gitlet.Tree t =  gitlet.Tree.createTree();
    }*/


    public static void setupStagingArea(String fileName) throws IOException {

        /* Create the index file in .gitlet dir. */
        INDEX.createNewFile();

        /* Create the Index (i.e. Staging) Tree object */
        gitlet.Tree indexObj =  gitlet.Tree.createTree();

        /* Get the contents from file, serialise the contents and create a blob object */
        byte[] serialisedBlob = serialize(getContentsFromFile(fileName));

        File blobObjFile = createBlobObj(serialisedBlob);

        writeObject(blobObjFile, serialisedBlob);

        /* Map the file name to the sha of the blob obj and write to INDEX */

        String blobSHA = sha1(serialisedBlob);

        indexObj.map.put(fileName, blobSHA);

        //byte[] serialisedIndex = serialize(indexObj);

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
     * @throws IOException
     */

    public static void setupGitlet() throws IOException {

        /* Initialise files and directories for gitlet. */
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        TREE_DIR.mkdir();
        REF_DIR.mkdir();
        HEADS_DIR.mkdir();
        HEAD.createNewFile();
        MASTER.createNewFile();

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
