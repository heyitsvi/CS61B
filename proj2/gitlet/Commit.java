package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class


/** Represents a gitlet commit object.
 *
 *  @author Vivek Singh
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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

    /* TODO: fill in the rest of this class. */

    Commit (String message, String parent, Date date, String tree) {
        this.message = message;
        this.parent = parent;
        this.date = date;
        this.tree = tree;
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

    static Commit initialCommit () {
        return new Commit("initial commit", null, new Date(0), null);
    }

    static Commit createCommit(String message, String parent, Date date, String tree) {
        return new Commit(message, parent, date, tree);
    }

    /*static void saveCommit (gitlet.Commit c, File dir) {

    }*/


}
