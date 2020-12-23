package lazyTrees;

import java.util.NoSuchElementException;

public class LazySearchTree<E extends Comparable<? super E>>
        implements Cloneable {
    protected int mSizeHard, mSize;
    protected LazySTNode mRoot;
    protected String name;

    /**
     * Initialize a lazy tree
     */
    public LazySearchTree() {
        clear();
    }

    /***
     * Only extra credit uses name;
     * @param name name of the tree
     */
    public LazySearchTree(String name) {
        this.name = name;
        clear();
    }

    /**
     * @return if the tree is empty
     */
    public boolean empty() {
        return (mSizeHard == 0 && mSize == 0);
    }

    /**
     * @return Soft size of the tree
     */
    public int size() {
        return mSize;
    }

    /**
     * @return hard size of the tree
     */
    public int sizeHard() {
        return mSizeHard;
    }

    /**
     * clear the tree, make it brand new
     */
    public void clear() {
        mSizeHard = 0;
        mSize = 0;
        mRoot = null;
    }

    /***
     *
     * @param x data to be removed
     * @return really removed data
     */
    public LazySTNode hardRemove(E x) {
        return hardRemove(mRoot, x);
    }

    /***
     * visit not deleted data
     * @param func printing functor
     */
    public void traverseSoft(PrintObject<E> func) {
        traverseSoft(func, mRoot);
    }

    /***
     *
     * visit all nodes
     * @param func printing functor
     */
    public void traverseHard(PrintObject<E> func) {
        traverseHard(func, mRoot);
    }

    /***
     *
     * @return the smallest data that is not deleted
     */
    public E findMin() {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMin(mRoot).data;
    }

    /***
     *
     * @return Largest undeleted data
     */
    public E findMax() {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMax(mRoot).data;
    }

    /***
     *
     * @param x data to be found
     * @return found data
     */
    public E find(E x) {
        LazySTNode resultNode;
        resultNode = find(mRoot, x);
        if (resultNode == null)
            throw new NoSuchElementException();
        return resultNode.data;
    }

    /**
     * Find out if the tree contains target data
     *
     * @param x target data
     * @return
     */
    public boolean contains(E x) {
        return find(mRoot, x) != null;
    }

    /***
     *
     * @param x data to be insert
     * @return if the insert was successful
     */
    public boolean insert(E x) {
        int oldSize = mSizeHard;
        mRoot = insert(mRoot, x);
        return (mSizeHard != oldSize);
    }

    /***
     *
     * @param x data to be removed
     * @return if soft remove was successful
     */
    public boolean remove(E x) {
        int oldSize = mSizeHard;
        mRoot = remove(mRoot, x);
        return (mSizeHard != oldSize);
    }

    /***
     *
     * @param root starting node
     * @return the soft min node
     */
    protected LazySTNode findMin(LazySTNode root) {
        if (root == null)
            return null;
        LazySTNode tmp = findMin(root.lftChild);
        if (tmp != null)
            return tmp;
        if (!root.deleted)
            return root;

        return findMin(root.rtChild);
    }

    /***
     *
     * @param root starting node
     * @return the hard min node
     */
    protected LazySTNode findMinHard(LazySTNode root) {
        if (root == null)
            return null;
        if (root.lftChild == null)
            return root;
        return findMinHard(root.lftChild);
    }

    /***
     *
     * @param root starting node
     * @return the hard max node
     */
    protected LazySTNode findMaxHard(LazySTNode root) {
        if (root == null)
            return null;
        if (root.rtChild == null)
            return root;
        return findMaxHard(root.rtChild);
    }

    /***
     *
     * @param root starting node
     * @return the soft max node
     */
    protected LazySTNode findMax(LazySTNode root) {
        if (root == null)
            return null;

        LazySTNode tmp = findMax(root.rtChild);

        if (tmp != null)
            return tmp;
        if (!root.deleted)
            return root;

        return findMax(root.lftChild);
    }

    /***
     * Insert data, act differently if the node was soft deleted
     * @param root starting point
     * @param x data to be inserted
     * @return inserted node
     */
    protected LazySTNode insert(LazySTNode root, E x) {
        int compareResult;
        if (root == null) {
            mSize++;
            mSizeHard++;
            return new LazySTNode(x, null, null);
        }

        compareResult = x.compareTo(root.data);

        if (compareResult < 0)
            root.lftChild = insert(root.lftChild, x);
        else if (compareResult > 0)
            root.rtChild = insert(root.rtChild, x);
        else if (root.deleted) {
            root.deleted = false;
            mSize++;
            return root;
        }

        return root;
    }

    /***
     * Soft remove element
     * @param root
     * @param x
     * @return soft removed node
     */
    protected LazySTNode remove(LazySTNode root, E x) {
        int compareResult;
        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);

        if (compareResult < 0)
            root.lftChild = remove(root.lftChild, x);
        else if (compareResult > 0)
            root.rtChild = remove(root.rtChild, x);
        else if (compareResult == 0 && !root.deleted) {
            root.deleted = true;
            mSize--;
            return root;
        }

        return root;
    }

    /***
     * traverse all notes
     * @param func functor
     * @param treeNode starting point
     */
    protected <F extends Traverser<? super E>>
    void traverseHard(F func, LazySTNode treeNode) {
        if (treeNode == null)
            return;

        traverseHard(func, treeNode.lftChild);
        func.visit(treeNode.data);
        traverseHard(func, treeNode.rtChild);
    }

    /***
     * traverse undeleted nodes
     * @param func functor
     * @param treeNode current/starting point
     */
    protected <F extends Traverser<? super E>>
    void traverseSoft(F func, LazySTNode treeNode) {
        if (treeNode == null)
            return;

        traverseSoft(func, treeNode.lftChild);
        if (!treeNode.deleted)
            func.visit(treeNode.data);
        traverseSoft(func, treeNode.rtChild);
    }

    /***
     * find the node if it's undeleted
     * @param root staring/ current point
     * @param x data needed to be found
     * @return the founded node
     */
    protected LazySTNode find(LazySTNode root, E x) {
        int compareResult;
        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            return find(root.lftChild, x);
        if (compareResult > 0)
            return find(root.rtChild, x);
        if (compareResult == 0 && root.deleted)
            return null;

        return root;   // found
    }

    /**
     * find the node even it's marked deleted
     *
     * @param root starting/ current point
     * @param x    data need to be found
     * @return the founded node
     */
    protected LazySTNode hardFind(LazySTNode root, E x) {
        int compareResult;
        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            return hardFind(root.lftChild, x);
        if (compareResult > 0)
            return hardFind(root.rtChild, x);
        return root;   // found
    }

    /***
     * Helper method, make the tree more readable
     */
    public void display() {
        System.out.println("Size: " + mSize + " HardSize: " + mSizeHard);
        BTreePrinter.printNode(mRoot);
    }

    /***
     * really remove an element
     * @param root starting/ current point
     * @param x data need removal
     * @return hard removed node
     */
    public LazySTNode hardRemove(LazySTNode root, E x) {
        int compareResult;
        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            root.lftChild = hardRemove(root.lftChild, x);
        else if (compareResult > 0)
            root.rtChild = hardRemove(root.rtChild, x);
        else if (root.lftChild != null && root.rtChild != null) {
            LazySTNode rightMin = findMin(root.rtChild);

            if (rightMin != null) {
                root.data = rightMin.data;
                root.deleted = rightMin.deleted;
            }

            root.rtChild = hardRemove(root.rtChild, root.data);
        } else {
            mSizeHard--;

            if (root == mRoot) {
                root = (root.lftChild == null) ? root.rtChild : root.lftChild;
                mRoot = root;
            } else
                root = (root.lftChild == null) ? root.rtChild : root.lftChild;

        }
        return root;
    }

    /***
     * get name, serve extra credit
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Public garbage collection, call the private one
     *
     * @return return true if collection was successful
     */
    public boolean collectGarbage() {
        collectGarbage(mRoot);
        return mSize == mSizeHard;
    }

    /**
     * Recursively visit every node in the tree and delete it if it has been soft deleted
     *
     * @param root the node that's been working on
     */
    private void collectGarbage(LazySTNode root) {
        if (root == null)
            return;

        collectGarbage(root.lftChild);
        collectGarbage(root.rtChild);

        if (root.deleted)
            hardRemove(root.data);
    }

    /***
     * The node class, I did not forget to make it private after debugging, I think you might be curious about my display
     */
    public class LazySTNode {
        public E data;
        public LazySTNode lftChild, rtChild;
        public boolean deleted;

        /**
         * Initializer
         *
         * @param d   data
         * @param lft lftChild
         * @param rt  rtChild
         */
        public LazySTNode(E d, LazySTNode lft, LazySTNode rt) {
            lftChild = lft;
            rtChild = rt;
            data = d;
            deleted = false;
        }

        /***
         * The default constructor
         */
        public LazySTNode() {
            this(null, null, null);
        }
    }

}
