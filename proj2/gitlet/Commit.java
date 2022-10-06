package gitlet;


import java.io.Serializable;
import java.util.Date;


/** Represents a gitlet commit object.
 *
 *  @author Vivek Singh
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    //private String author;
    private Date date;
    private String message;
    private String parent;
    private String parent2;
    private String tree;

    //private TreeMap<String, String> map = new TreeMap<>();


    Commit(String message, String parent, Date date, String tree, String parent2) {
        this.message = message;
        this.parent = parent;
        this.date = date;
        this.tree = tree;
        this.parent2 = parent2;
    }

    String getParent() {
        return this.parent;
    }

    String getMsg() {
        return this.message;
    }

    Date getDate() {
        return this.date;
    }

    String getTree() {
        return this.tree;
    }
    String getParent2() {
        return this.parent2;
    }

    static Commit initialCommit() {
        return new Commit("initial commit", null, new Date(0), null, null);
    }

    static Commit createCommit(String msg, String parent, Date d, String tree) {
        return new Commit(msg, parent, d, tree, null);
    }

    static Commit createMergeCommit(String msg, String parent, Date d, String tree, String p2) {
        return new Commit(msg, parent, d, tree, p2);
    }

}
