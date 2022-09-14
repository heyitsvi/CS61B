package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
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
    private Commit parent;

    private String tree;

    //private TreeMap<String, String> map = new TreeMap<>();

    /* TODO: fill in the rest of this class. */

    Commit (String message, Commit parent, Date date) {
        this.message = message;
        //this.author = author;
        this.parent = parent;
        this.date = date;

        if (parent == null) {
            this.tree = null;
        }
    }

    static Commit initialCommit () {
        return new Commit("initial commit", null, new Date(0));
    }

    /*static void saveCommit (gitlet.Commit c, File dir) {

    }*/


}
