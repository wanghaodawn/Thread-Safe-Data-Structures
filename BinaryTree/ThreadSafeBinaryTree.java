/**
 * Thread Safe Binary Tree built from scratch.
 *
 * @author     Hao Wang
 * 
 * 05/2017
 */

import java.util.concurrent.locks.*;

public class ThreadSafeBinaryTree {

    /**
     * The Node of the Linked List.
     */
    class Node {
        /**
         * The value of the node.
         */
        public int value;
        /**
         * The local lock of the node.
         */
        public ReadWriteLock localLock;
        /**
         * Left node.
         */
        public Node left;
        /**
         * Right node.
         */
        public Node right;

        public Node(int val) {
            this.value = val;
            this.left = null;
            this.right = null;
            this.localLock = new ReentrantReadWriteLock();
        }
    }

    /**
     * Instance Variable, the head of the Linked List.
     */
    private Node root;

    /**
     * Constructor of the Thread Safe Linked List.
     */
    ThreadSafeBinaryTree() {
        this.head = new Node(-1, null);
    }

    /**
     * Clear the binary tree.
     */
    public void clear() {
        try {
            head.localLock.writeLock().lock();
            head.next = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            head.localLock.writeLock().unlock();
        }
    }

    public void insert(int val) {

    }

    /**
     * Determines if the Linked List is empty.
     *
     * @return     True if empty, False otherwise.
     */
    public boolean isEmpty() {
    }

    /**
     * Get the size of the Linked List.
     *
     * @return     The size of the Linked List.
     */
    public int size() {
    }
}