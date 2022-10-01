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

    private String tree;

    //private TreeMap<String, String> map = new TreeMap<>();


    Commit(String message, String parent, Date date, String tree) {
        this.message = message;
        this.parent = parent;
        this.date = date;
        this.tree = tree;
        //this.parent2 = parent2;
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

    static Commit initialCommit() {
        return new Commit("initial commit", null, new Date(0), null);
    }

    static Commit createCommit(String message, String parent, Date date, String tree) {
        return new Commit(message, parent, date, tree);
    }

}
