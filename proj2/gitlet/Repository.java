package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Vivek Singh
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
    /** The .gitlet directory.
     * Structure
     * .gitlet
     *      objects
     *          commits (dir)
     *          blobs   (dir)
     *      refs
     *          heads
     *              master (file)
     *      HEAD (file)
     *      index (file)
     *
     * */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOB_DIR = join(OBJECTS_DIR, "blobs");
    public static final File REF_DIR = join(OBJECTS_DIR, "refs");

    public static final File HEADS_DIR = join(REF_DIR, "heads");

    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static final File INDEX = join(GITLET_DIR, "index");
    public static final File MASTER = join(HEADS_DIR, "master");


    /* TODO: fill in the rest of this class. */

    /** Check if a Git Directory already exists */
    public static boolean checkGitDirExists() {
        return GITLET_DIR.exists();
    }

    public static File createObjectFile(byte[] o, File dir) throws IOException {
        String sha = sha1(o);
        File shaObj = join(dir, sha);
        shaObj.createNewFile();

        return shaObj;
    }

    public static File createCommitObj(byte[] o) throws IOException {
        return createObjectFile(o, COMMIT_DIR);
    }

    public static void updateMaster(Object c) {
        String sha = sha1(c);
        writeContents(MASTER, sha);
    }

    /*public static void serialiseObjectToFile(Object c, File dir) {
        writeObject();
    }*/

    public static void setupGitlet() throws IOException {
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        REF_DIR.mkdir();
        HEADS_DIR.mkdir();
        HEAD.createNewFile();
        MASTER.createNewFile();
        INDEX.createNewFile();

        gitlet.Commit c = gitlet.Commit.initialCommit();
        byte[] serialisedCommit = serialize(c);

        File f = createCommitObj(serialisedCommit);

        updateMaster(serialisedCommit);
        writeObject(f, serialisedCommit);
    }
}
