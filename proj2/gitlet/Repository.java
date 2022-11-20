package gitlet;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static String initID = null;

    /** Check if a Git Directory already exists */
    public static boolean checkGitDirExists() {
        return GITLET_DIR.exists();
    }

    /** Check if Index (i.e Staging area) already exists */
    public static boolean indexExists() {
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
        List<String> listOfFileNamesCWD = plainFilenamesIn(CWD);

        return listOfFileNamesCWD.contains(fileName);
    }


    /** Check if the staging area is empty */
    public static boolean isIndexEmpty() {
        if (!INDEX.exists()) {
            return true;
        }
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        return stagingTree.getMap().isEmpty();
    }

    public static boolean fileStagedForRemoval(String file) {
        if (!INDEX.exists()) {
            return false;
        }
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        return stagingTree.getRemoveSet().contains(file);
    }
    public static boolean filesStagedForRemovalEmpty() {
        if (!INDEX.exists()) {
            return false;
        }
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        return stagingTree.getRemoveSet().isEmpty();
    }

    public static boolean newFilesTracked() {
        return !isIndexEmpty() || !filesStagedForRemovalEmpty();
    }

    /** Check if the checkout branch is the current active branch */
    public static boolean isCurrentBranch(String branch) {
        return getActiveBranch().equals(branch);
    }

    /** Checks in Index to see if the file has been staged */
    public static boolean fileExistsInIndex(String fileName) {
        gitlet.Tree t = readObject(INDEX, gitlet.Tree.class);
        return t.getMap().containsKey(fileName);
    }
    /** Check if the same file (i.e.with the same SHA val) exists in the latest commit. */
    public static boolean sameFileInLatestCommit(String fileName, String sha) {
        String shaInMaster = getLatestIDInHEAD();
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(shaInMaster);

        if (latestCommitTreeObj == null || !latestCommitTreeObj.getMap().containsKey(fileName)) {
            return false;
        }

        return latestCommitTreeObj.getMap().get(fileName).equals(sha);
    }

    /** Check if there are untracked files in CWD */
    public static boolean anyUntrackedFiles() {
        Set<String> result = getUntrackedFiles(CWD);
        if (result == null) {
            return false;
        }

        /**for (String file : result) {
            System.out.println("File : " + file);
        }*/
        return !result.isEmpty();
    }

    /** Check if the file is untracked */
    public static boolean isFileUntracked(String fileName) {
        Set<String> untrackedFiles = getUntrackedFiles(CWD);
        if (untrackedFiles == null) {
            return true;
        }
        return untrackedFiles.contains(fileName);
    }

    /**public static void stageFileForRemoval(String f) {
        gitlet.Tree t = readObject(INDEX, gitlet.Tree.class);
        t.getRemoveSet().add(f);
    }*/


    /** Create an object file inside Gitlet with file name equal to the sha value of its contents.
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

    /** Get the commit object from a directory */
    public static gitlet.Commit getCommitObj(String fileName, File dir) {
        File filePath = join(dir, fileName);
        return readObject(filePath, gitlet.Commit.class);
    }

    /** Get the untracked files in the given directory */
    public static Set<String> getUntrackedFiles(File dir) {
        List<String> filesInDir = plainFilenamesIn(dir);

        if (!INDEX.exists()) {
            return new HashSet<>(filesInDir);
        }

        if (filesInDir == null) {
            return null;
        }
        Set<String> allFilesInGit = new HashSet<>();
        Set<String> untrackedFiles = new HashSet<>();
        Set<String> filesTracked;
        Set<String> filesStaged;
        gitlet.Tree t = getLatestCommitTreeObj(getLatestIDInHEAD());
        if (t != null) {
            filesTracked = t.getMap().keySet();
            allFilesInGit.addAll(filesTracked);
            allFilesInGit.addAll(t.getRemoveSet());
            /**for (String file : filesTracked) {
                System.out.println("Tracked " + file);
            }*/
        }
        gitlet.Tree t2 = readObject(INDEX, gitlet.Tree.class);
        if (t2 != null) {
            filesStaged = t2.getMap().keySet();
            allFilesInGit.addAll(filesStaged);
            allFilesInGit.addAll(t2.getRemoveSet());
            /**for (String file : filesStaged) {
                System.out.println("Staged " + file);
            }*/
        }

        /**System.out.println("Files in git");
        for (String file : allFilesInGit) {
            System.out.println(file);
        }*/

        for (String file : filesInDir) {
            if (!allFilesInGit.contains(file)) {
                untrackedFiles.add(file);
            }
        }




        /**for (String file : filesInDir) {
            String fileContents = readContentsAsString(join(dir, file));
            String sha = sha1(fileContents);
            if (filesStaged != null && filesStaged.contains(file)) {
                if (!t2.getMap().get(file).equals(sha)) {
                    result.add(file);
                }
            } else if (filesTracked != null && filesTracked.contains(file)) {
                if (!t.getMap().get(file).equals(sha)) {
                    result.add(file);
                }
            } else {
                result.add(file);
            }
        }*/
        return new HashSet<>(untrackedFiles);
    }

    /** Get the tree object from a commit */
    public static gitlet.Tree getCommitTreeObj(gitlet.Commit c) {
        String treeSHA = c.getTree();

        if (treeSHA == null) {
            return null;
        }
        File filePath = join(TREE_DIR, treeSHA);
        return readObject(filePath, gitlet.Tree.class);
    }

    /** Get the latest commit obj */
    public static gitlet.Commit getLatestCommitObj(String commitID) {
        return getCommitObj(commitID, COMMIT_DIR);
    }

    /** Get the tree object from the latest commit */
    public static gitlet.Tree getLatestCommitTreeObj(String commitID) {
        return getCommitTreeObj(getLatestCommitObj(commitID));
    }

    /** Clear the staging area by removing all the (fileName : SHA val) mappings */
    public static void clearStagingArea() {
        gitlet.Tree stagingTree = readObject(INDEX, gitlet.Tree.class);
        stagingTree.getMap().clear();
        stagingTree.getRemoveSet().clear();
        writeObject(INDEX, stagingTree);
    }

    /** Add file to INDEX unless the file remains unchanged from previous commit
     * @param fileName name of the file
     */
    public static void addToIndex(String fileName) {
        gitlet.Tree indexObj = readObject(INDEX, gitlet.Tree.class);

        if (indexObj.getRemoveSet().contains(fileName)) {
            indexObj.getRemoveSet().remove(fileName);
            writeObject(INDEX, indexObj);
            System.exit(0);
        }

        String contents = readContentsAsString(join(CWD, fileName));

        String blobSHA = sha1(contents);

        if (isIndexEmpty()) {
            boolean fileExists = sameFileInLatestCommit(fileName, blobSHA);
            if (fileExists) {
                System.exit(0);
            }
        }

        if (!indexObj.getMap().containsKey(fileName)
                || !indexObj.getMap().get(fileName).equals(blobSHA)) {
            File blobObjFile = createBlobObj(contents);
            writeContents(blobObjFile, contents);
            indexObj.getMap().put(fileName, blobSHA);
            writeObject(INDEX, indexObj);
        }

    }
    /** Merge two objects and return a new object */
    public static gitlet.Tree mergeObjs(gitlet.Tree o1, gitlet.Tree o2) {
        gitlet.Tree t = gitlet.Tree.createTree();
        if (o1 == null) {
            t.getMap().putAll(o2.getMap());
            return t;
        }

        t.getMap().putAll(o1.getMap());
        t.getMap().putAll(o2.getMap());
        return t;
    }

    /** Merge index and the latest commit to create the new commit obj
     * Remove the files staged for removal
     * Add the files staged for addition to the new commit obj.
     */
    public static gitlet.Tree getNewCommitObj() {
        String prevCommitSHA = getLatestIDInHEAD();
        gitlet.Tree prevCommitTreeObj = getLatestCommitTreeObj(prevCommitSHA);
        gitlet.Tree indexTreeObj = readObject(INDEX, gitlet.Tree.class);
        removeFilesFromCommit(indexTreeObj.getRemoveSet(), prevCommitTreeObj);
        return mergeObjs(prevCommitTreeObj, indexTreeObj);
    }

    /** Create a new commit with the given message */
    public static void createANewCommit(String msg, String type, String branch) {
        gitlet.Tree newTreeObj = getNewCommitObj();
        byte[] serialiseTreeObj = serialize(newTreeObj);
        String newObjSHA = sha1(serialiseTreeObj);

        String parent = getLatestIDInHEAD();

        File path = createTreeObj(serialiseTreeObj);
        writeObject(path, newTreeObj);

        gitlet.Commit newCommit;

        if (type.equals("regular")) {
            newCommit = gitlet.Commit.createCommit(msg, parent, new Date(), newObjSHA);
        } else {
            newCommit = gitlet.Commit.createMergeCommit(msg, parent, new Date(), newObjSHA, branch);
        }
        byte[] serialisedCommit = serialize(newCommit);
        File newCommitFile = createCommitObj(serialisedCommit);
        writeObject(newCommitFile, newCommit);

        updateActiveBranch(serialisedCommit);
        clearStagingArea();
    }

    /** Creates a new branch in the HEADS_DIR */
    public static void createBranch(String branch) {
        String activeBranchID = getLatestIDInHEAD();

        File newBranch = join(HEADS_DIR, branch);

        try {
            newBranch.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeContents(newBranch, activeBranchID);

    }

    /** Overwrite the file in a directory with its previous version from a commit */
    public static void overwriteFile(String fileName, String savedFile, File dir) {
        File f = join(dir, fileName);
        String contents = readContentsAsString(join(BLOB_DIR, savedFile));
        writeContents(f, contents);
    }

    /** Check if the file exists in the given directory */
    public static boolean fileExistsInDir(String fileName, File dir) {
        File f =  join(dir, fileName);
        return f.exists();
    }

    public static String findFileInDir(String id, File dir) {
        List<String> listOfFiles = plainFilenamesIn(dir);
        for (String file : listOfFiles) {
            if (file.contains(id)) {
                return file;
            }
        }
        return "";
    }
    public static String checkIfCommitExists(String commitID) {
        return findFileInDir(commitID, COMMIT_DIR);
    }

    public static void checkoutFile(String commitID, String fileName) {
        gitlet.Tree treeObj = getCommitTreeObj(getCommitObj(commitID, COMMIT_DIR));

        if (treeObj != null) {
            if (treeObj.getMap().containsKey(fileName)) {
                overwriteFile(fileName, treeObj.getMap().get(fileName), CWD);
            } else {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
        }
    }

    public static void resetToCommit(String commitID) {
        //gitlet.Tree t1 = getLatestCommitTreeObj(getLatestIDInHEAD());
        gitlet.Tree t2 = getCommitTreeObj(getCommitObj(commitID, COMMIT_DIR));
        Set<String> filesToRemove = new HashSet<>(plainFilenamesIn(CWD));
        Set<String> filesOtherCommit;

        if (t2 != null) {
            filesOtherCommit = t2.getMap().keySet();
            for (String file : filesOtherCommit) {
                if (join(CWD, file).exists()) {
                    overwriteFile(file, t2.getMap().get(file), CWD);
                } else {
                    createFileWithContents(join(CWD, file), join(BLOB_DIR, t2.getMap().get(file)));
                }
            }
            filesToRemove.removeAll(filesOtherCommit);
        }

        //System.out.println("Files to remove");
        for (String file : filesToRemove) {
            //System.out.println(file);
            restrictedDelete(file);
        }

        if (INDEX.exists()) {
            //System.out.println("Cleared Index");
            clearStagingArea();
        }
        String branch = getActiveBranch();
        writeContents(join(HEADS_DIR, branch), commitID);
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
        gitlet.Tree latestTreeObj = getLatestCommitTreeObj(getLatestIDInHEAD());
        Set<String> trackedFiles;
        Set<String> branchFiles;

        if (branchTreeObj != null) {
            branchFiles = branchTreeObj.getMap().keySet();
            List<String> listOfFiles = plainFilenamesIn(CWD);
            for (String file : branchFiles) {
                if (listOfFiles.contains(file)) {
                    overwriteFile(file, branchTreeObj.getMap().get(file), CWD);
                } else {
                    String fileSHA = branchTreeObj.getMap().get(file);
                    File contentPath = join(BLOB_DIR, fileSHA);
                    createFileWithContents(join(CWD, file), contentPath);
                }
            }

            if (latestTreeObj != null) {
                trackedFiles = latestTreeObj.getMap().keySet();
                for (String file : trackedFiles) {
                    if (!branchFiles.contains(file)) {
                        restrictedDelete(file);
                    }
                }
            }
        } else {
            if (latestTreeObj != null) {
                trackedFiles = latestTreeObj.getMap().keySet();
                for (String file : trackedFiles) {
                    restrictedDelete(file);
                }
            }
        }

        if (INDEX.exists()) {
            clearStagingArea();
        }
        gitlet.Repository.changeActiveBranch(branch);
    }

    private static HashMap<String, Integer> createGraphMap() {
        HashMap<String, Integer> graphMap = new HashMap<>();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);

        int count  = 0;

        for (String commit : commits) {
            graphMap.put(commit, count++);
        }
        return graphMap;
    }

    private static void printMap(HashMap<String, Integer> map) {
        for (String key : map.keySet()) {
            System.out.println(key + " " + map.get(key));
        }
    }

    /**private static gitlet.GraphObj createACommitGraph( gitlet.GraphObj G) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        gitlet.GraphObj G = new gitlet.GraphObj(commits.size());
        HashMap<String, Integer> graphMap = createGraphMap();

    }**/

    private static int closestVertexToNodes(int[] distArr1, int[] distArr2, Set<Integer> set) {
        TreeMap<Integer, Integer> nodeDistances = new TreeMap<>();
        for (int i : set) {
            nodeDistances.put(i, distArr1[i] + distArr2[i]);
        }

        int minDistance = Collections.min(nodeDistances.values());
        for (int key : nodeDistances.keySet()) {
            if (nodeDistances.get(key) == minDistance) {
                return key;
            }
        }
        return -1;
    }
    public static String findSplitPoint(String branch) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        gitlet.GraphObj G = new gitlet.GraphObj(commits.size());
        HashMap<String, Integer> graphMap = createGraphMap();
        //printMap(graphMap);
        String headBranch = getLatestIDInHEAD();
        int source1 = graphMap.get(headBranch);
        //System.out.println("s1 : " + source1);
        String otherBranch = readContentsAsString(join(HEADS_DIR, branch));
        int source2 = graphMap.get(otherBranch);
        //System.out.println("s2 : " + source2);
        int dest = graphMap.get("c3c23d9fa62834d47f9bae0f0bbbd8dcd251e291");
        //System.out.println("dest : " + dest);
        for (String commit : commits) {
            int vertex1 = graphMap.get(commit);
            gitlet.Commit obj = getCommitObj(commit, COMMIT_DIR);

            if (obj.getParent() == null) {
                continue;
            }

            int vertex2 = graphMap.get(obj.getParent());
            G.addEdge(vertex1, vertex2);

            if (obj.getParent2() != null) {
                int vertex3 = graphMap.get(obj.getParent2());
                G.addEdge(vertex1, vertex3);
            }

        }
        /**for (int i = 0; i < G.getV(); i++) {
            for (int w : G.adj(i)) {
                System.out.println(i + "-->" + w);
            }
        }**/
        gitlet.Paths paths1 = new gitlet.Paths(G, source1, dest);
        gitlet.Paths paths2 = new gitlet.Paths(G, source2, dest);

        Set<Integer> currBranchSet = new HashSet<>();
        //System.out.println("Paths from S1 : ");
        for (List<Integer> path : paths1.allPaths()) {
            //System.out.println(path.toString());
            currBranchSet.addAll(path);
        }
        Set<Integer> otherBranchSet = new HashSet<>();
        //System.out.println("Paths from S2 : ");
        for (List<Integer> path : paths2.allPaths()) {
            //System.out.println(path.toString());
            otherBranchSet.addAll(path);
        }
        otherBranchSet.retainAll(currBranchSet);
        int latestCommonNode = dest;
        if (otherBranchSet.size() == 2) {
            for (int i : otherBranchSet) {
                if (i != dest) {
                    latestCommonNode = i;
                }
            }
        } else if (otherBranchSet.size() > 2) {
            otherBranchSet.remove(dest);
            int[] distFromHEAD = paths1.getDistances();
            int[] distFromOther = paths2.getDistances();
            latestCommonNode = closestVertexToNodes(distFromHEAD, distFromOther, otherBranchSet);
        }

        String splitID = null;

        for (String id : graphMap.keySet()) {
            if (graphMap.get(id).equals(latestCommonNode)) {
                splitID = id;
                break;
            }
        }
        //System.out.println("Split ID : " + splitID);
        return splitID;
    }

    /**public static Set<String> getDifference(Object o1, Object o2) {
        TreeMap<String, String> m1 = (TreeMap<String, String>) o1;
        TreeMap<String, String> m2 = (TreeMap<String, String>) o2;
        HashSet<String> removedFiles = new HashSet<>();
        for (String file : m1.keySet()) {
            if (!m2.containsKey(file)) {
                removedFiles.add(file);
            }
        }
        return removedFiles;
    }*/

    /**public static TreeMap<String, String> modifyFiles(Object o1, Object o2) {
        TreeMap<String, String> m1 = (TreeMap<String, String>) o1;
        TreeMap<String, String> m2 = (TreeMap<String, String>) o2;
        TreeMap<String, String> modifiedFiles = new TreeMap<>();

        for (String file : m1.keySet()) {
            if (m2.containsKey(file)) {
                // Check if the file has been modified in m2
                if (!m1.get(file).equals(m2.get(file))) {
                    overwriteFile(file, m2.get(file), CWD);
                    modifiedFiles.put(file, m2.get(file));
                }
            }
        }
        return modifiedFiles;
    }*/

    public static void createMergeCommit(String branch) {
        String msg  = "Merged " + branch + " into " + readContentsAsString(HEAD) + ".";
        String branchID = readContentsAsString(join(HEADS_DIR, branch));
        createANewCommit(msg, "merge", branchID);
    }

    public static boolean sameFileIn(String file, gitlet.Tree m1, gitlet.Tree m2) {
        if (!m2.getMap().containsKey(file)) {
            return false;
        }
        return m1.getMap().get(file).equals(m2.getMap().get(file));
    }

    public static boolean fileExistsIn(String fileName, Set<String> ... s) {
        for (Set<String> set : s) {
            if (set == null || !set.contains(fileName)) {
                return false;
            }
        }
        return true;
    }
    public static String mergeFileContents(String file, gitlet.Tree t1, gitlet.Tree t2) {
        File f1 = join(BLOB_DIR, t1.getMap().get(file));
        File f2 = join(BLOB_DIR, t2.getMap().get(file));
        String contents1 = readContentsAsString(f1);
        String contents2 =  readContentsAsString(f2);

        return "<<<<<<< HEAD\n"
                + contents1 + "=======\n"
                + contents2 + ">>>>>>>\n";
    }

    public static void merge(String splitC, String branch) {
        gitlet.Tree splitT = getCommitTreeObj(getCommitObj(splitC, COMMIT_DIR));
        gitlet.Tree currT = getCommitTreeObj(getCommitObj(getLatestIDInHEAD(), COMMIT_DIR));
        gitlet.Tree otherT = getCommitTreeObj(getCommitObj(latestCommitIn(branch), COMMIT_DIR));
        gitlet.Tree indexT = readObject(INDEX, gitlet.Tree.class);
        if (currT != null && otherT != null) {
            Set<String> currSet = new HashSet<>(currT.getMap().keySet());
            currSet.addAll(currT.getRemoveSet());
            Set<String> otherSet = new HashSet<>(otherT.getMap().keySet());
            otherSet.addAll(otherT.getRemoveSet());
            Set<String> splitSet = null;
            Set<String> allFiles = new HashSet<>(currSet);
            allFiles.addAll(otherSet);
            List<String> filesInDIR = plainFilenamesIn(CWD);
            Set<String> filesToRemove = new HashSet<>(filesInDIR);
            if (splitT != null) {
                splitSet = new HashSet<>(splitT.getMap().keySet());
                splitSet.addAll(splitT.getRemoveSet());
                allFiles.addAll(splitSet);
                filesToRemove.removeAll(splitSet);
            }
            filesToRemove.removeAll(currSet);
            filesToRemove.removeAll(otherSet);
            for (String file : filesToRemove) {
                restrictedDelete(file);
            }
            boolean conflictFlag = false;
            for (String file : allFiles) {
                if (fileExistsIn(file, splitSet, currSet, otherSet)) {
                    if (sameFileIn(file, splitT, currT) && !sameFileIn(file, splitT, otherT)) {
                        indexT.getMap().put(file, otherT.getMap().get(file));
                        overwriteFile(file, otherT.getMap().get(file), CWD);
                    } else if (fileExistsIn(file, currSet, otherSet)
                        && !sameFileIn(file, currT, otherT)) {
                        writeContents(join(CWD, file), mergeFileContents(file, currT, otherT));
                        addToIndex(file);
                        conflictFlag = true;
                    }
                } else if (fileExistsIn(file, splitSet) && !fileExistsIn(file, currSet)) {
                    if (fileExistsIn(file, otherSet) && currT.getRemoveSet().contains(file)
                        && !sameFileIn(file, splitT, otherT)) {
                        writeContents(join(CWD, file), mergeFileContents(file, currT, otherT));
                        addToIndex(file);
                        conflictFlag = true;
                    }
                } else if (fileExistsIn(file, splitSet, currSet)
                        && !fileExistsIn(file, otherSet)) {
                    if (sameFileIn(file, splitT, currT) && !otherT.getRemoveSet().contains(file)) {
                        indexT.getRemoveSet().add(file);
                        restrictedDelete(join(CWD, file));
                    } else if (!sameFileIn(file, splitT, currT)
                        && otherT.getRemoveSet().contains(file)) {
                        writeContents(join(CWD, file), mergeFileContents(file, currT, otherT));
                        addToIndex(file);
                        conflictFlag = true;
                    }
                } else if (!fileExistsIn(file, splitSet)) {
                    if (!fileExistsIn(file, currSet) && fileExistsIn(file, otherSet)) {
                        indexT.getMap().put(file, otherT.getMap().get(file));
                        overwriteFile(file, otherT.getMap().get(file), CWD);
                    } else if ((fileExistsIn(file, currSet) && fileExistsIn(file, otherSet)
                        && !sameFileIn(file, currT, otherT))
                        || ((fileExistsIn(file, currSet) && !fileExistsIn(file, otherSet)
                        && otherT.getRemoveSet().contains(file)))
                        || (!fileExistsIn(file, currSet) && fileExistsIn(file, otherSet)
                        && currT.getRemoveSet().contains(file))) {
                        writeContents(join(CWD, file), mergeFileContents(file, currT, otherT));
                        addToIndex(file);
                        conflictFlag = true;
                    }
                }
            }
            writeObject(INDEX, indexT);
            createMergeCommit(branch);
            if (conflictFlag) {
                System.out.println("Encountered a merge conflict.");
            }
        }
    }

    /** Check if the latest commit in the current branch tracks this file */
    public static boolean fileInHEADCommit(String fileName) {
        gitlet.Tree latestCommitTreeObj = getLatestCommitTreeObj(getLatestIDInHEAD());
        if (latestCommitTreeObj == null) {
            return false;
        }
        return latestCommitTreeObj.getMap().containsKey(fileName);
    }
    public static void removeBranch(String branch) {
        File f = join(HEADS_DIR, branch);
        if (!f.isDirectory()) {
            f.delete();
        }
    }

    /** Remove the files from the commit if they are staged for removal
     * @param removeFiles Set containing the names of the files that need to be removed.
     * @param commitTree The Tree Object that
     * */
    public static void removeFilesFromCommit(Set<String> removeFiles, gitlet.Tree commitTree) {
        for (String file : removeFiles) {
            commitTree.getMap().remove(file);
        }
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

        if (isFileUntracked(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        gitlet.Tree t = readObject(INDEX, gitlet.Tree.class);
        List<String> listOfFiles = plainFilenamesIn(CWD);

        if (fileInHEADCommit(fileName)) {
            t.getRemoveSet().add(fileName);

            if (listOfFiles.contains(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }

        }
        if (fileExistsInIndex(fileName)) {
            t.getMap().remove(fileName);
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
        String parent = c.getParent();
        String parent2 = c.getParent2();

        if (parent2 == null) {
            return "=== \n"
                    + "commit " + commit + "\n"
                    + "Date: " + formattedDate + "\n"
                    + msg + "\n";
        } else {
            return "=== \n"
                    + "commit " + commit + "\n"
                    + "Merge: " + parent.substring(0, 7) + " " + parent2.substring(0, 7)
                    + "\n" + "Date: " + formattedDate + "\n"
                    + msg + "\n";
        }
    }

    /** Traverse commits starting from the HEAD commit to the initial commit
     * and display their log msg */
    public static void printLog() {
        String latestCommitID = getLatestIDInHEAD();

        gitlet.Commit prevCommitObj = getLatestCommitObj(latestCommitID);

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

    /** Display general into about the repository like branches, staged files,
     * files staged for removal, modified files and untracked files.
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
        gitlet.Tree indexObj;
        if (!INDEX.exists()) {
            indexObj = null;
        } else {
            indexObj = readObject(INDEX, gitlet.Tree.class);
        }

        System.out.println(" ");
        System.out.println("=== Staged Files ===");
        if (indexObj != null) {
            for (String key : indexObj.getMap().keySet()) {
                System.out.println(key);
            }
        }

        System.out.println(" ");
        System.out.println("=== Removed Files ===");
        if (indexObj != null) {
            for (String key : indexObj.getRemoveSet()) {
                System.out.println(key);
            }
        }

        System.out.println(" ");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(" ");
        System.out.println("=== Untracked Files ===");
        System.out.println(" ");
    }

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
    public static String getLatestIDInHEAD() {
        String branch = readContentsAsString(HEAD);
        return readContentsAsString(join(HEADS_DIR, branch));
    }

    public static String latestCommitIn(String branch) {
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

        indexObj.getMap().put(fileName, blobSHA);

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
        initID = sha1(serialisedCommit);
        File f = createCommitObj(serialisedCommit);

        writeContents(HEAD, "master");

        /* update master to point to latest commit. */
        updateActiveBranch(serialisedCommit);

        /* Save the commit object in objects/commits dir. */
        writeObject(f, c);
    }
}
