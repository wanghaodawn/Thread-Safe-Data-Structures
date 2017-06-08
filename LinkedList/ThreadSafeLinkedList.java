/**
 * Thread Safe Linked List built from scratch.
 *
 * @author     Hao Wang
 * 
 * 05/2017
 */

import java.util.Stack;
import java.util.concurrent.locks.*;

public class ThreadSafeLinkedList {

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
         * Next node.
         */
        public Node next;

        public Node(int val) {
            this.value = val;
            this.next = null;
            this.localLock = new ReentrantReadWriteLock();
        }

        public Node(int val, Node next) {
            this.value = val;
            this.next = next;
            this.localLock = new ReentrantReadWriteLock();
        }
    }

    /**
     * Instance Variable, the head of the Linked List.
     */
    private Node head;

    /**
     * Constructor of the Thread Safe Linked List.
     */
    ThreadSafeLinkedList() {
        this.head = new Node(-1, null);
    }

    /**
     * Clear the linked list.
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

    /**
     * Insert the Node into the list.
     *
     * @param      value  The value of the new node
     * @param      index  The index of the new node
     */
    public void insert(int value, int index) {
        if (index < 0) {
            throw new IllegalArgumentException("The index have to be non-negative!");
        }

        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;
            index--;

            // Find the target node
            while (node.next != null && index >= 0) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
                index--;
            }

            if (index > 0) {
                throw new IllegalArgumentException("The index excceeds the length of the Linked List!");
            }

            // Lock the writeLock of the next node
            node.localLock.writeLock().lock();
            // insert into last node
            node.next = new Node(value, node.next);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Unlock the writeLock of the previous last node
            if (node != null) {
                node.localLock.writeLock().unlock();
            }

            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }  
    }

    /**
     * Inser the node to the first of the Linked List.
     *
     * @param      value  The value of the new Node
     */
    public void insertFirst(int value) {
        insert(value, 0);
    }

    /**
     * Insert the node to the last of the linked List.
     *
     * @param      value  The value of the new node
     */
    public void insertLast(int value) {
        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;

            // Find the last node
            while (node.next != null) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
            }

            // Lock the writeLock of the last node
            node.localLock.writeLock().lock();
            // insert into last node
            node.next = new Node(value, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Unlock the writeLock of the previous last node
            if (node != null) {
                node.localLock.writeLock().unlock();
            }

            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }
    }

    /**
     * Remove the node with the given index from the list.
     *
     * @param      index  The index of the node to be removed
     */
    public void remove(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("The index have to be non-negative!");
        }

        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        Node nextNode = null;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;
            index--;

            // Find the last node
            while (node.next != null && index >= 0) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
                index--;
            }

            if (index > 0) {
                throw new IllegalArgumentException("The index excceeds the length of the Linked List!");
            }

            // Lock the writeLock of the last node
            nextNode = node;
            nextNode.localLock.writeLock().lock();

            // Now pop the stack to get the node before last node and lock its writeLock
            node = stack.pop();
            node.localLock.writeLock().lock();

            // Remove the target node
            node.next = nextNode.next;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Unlock the previous last node
            if (nextNode != null) {
                nextNode.localLock.writeLock().unlock();
            }

            // Unlock the writeLock of the previous last node
            if (node != null) {
                node.localLock.writeLock().unlock();
            }

            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }
    }

    /**
     * Remove the first node from the list.
     */
    public void removeFirst() {
        remove(0);
    }

    /**
     * Remove the last node from the list.
     */
    public void removeLast() {
        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        Node lastNode = null;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;

            // Find the last node
            while (node.next != null) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
            }

            // Lock the writeLock of the last node
            lastNode = node;
            lastNode.localLock.writeLock().lock();

            // Now pop the stack to get the node before last node and lock its writeLock
            node = stack.pop();
            node.localLock.writeLock().lock();

            // Remove the last node
            node.next = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Unlock the previous last node
            if (lastNode != null) {
                lastNode.localLock.writeLock().unlock();
            }

            // Unlock the writeLock of the previous last node
            if (node != null) {
                node.localLock.writeLock().unlock();
            }

            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }
    }

    /**
     * Get the node on the target index of the Linked List.
     *
     * @param      index  The index of the node to be returned
     *
     * @return     The node on the given index of the Linked List
     */
    public int get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("The index have to be non-negative!");
        }

        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        int res = -1;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;
            index--;

            // Find the last node
            while (node.next != null && index >= 0) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
                index--;
            }

            if (index > 0) {
                throw new IllegalArgumentException("The index excceeds the length of the Linked List!");
            }

            // Find the target node, then lock its readLock;
            node.localLock.readLock().lock();
            res = node.value;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Unlock the target node
            if (node != null) {
                node.localLock.readLock().unlock();
            }
            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }
        return res;
    }

    /**
     * Get the first node of the Linked List.
     *
     * @return     The first node of the Linked List
     */
    public int getFirst() {
        return get(0);
    }

    /**
     * Gets the last node of the Linked List.
     *
     * @return     The last node of the Linked List
     */
    public int getLast() {
        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        int res = -1;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;

            // Find the last node
            while (node.next != null) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
            }

            // Lock the read lock of last node
            node.localLock.readLock().lock();
            res = node.value;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release the read lock of last node
            if (node != null) {
                node.localLock.readLock().unlock();
            }
            
            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }
        return res;
    }

    /**
     * Determines if the Linked List is empty.
     *
     * @return     True if empty, False otherwise.
     */
    public boolean isEmpty() {
        return head.next == null;
    }

    /**
     * Get the size of the Linked List.
     *
     * @return     The size of the Linked List.
     */
    public int size() {
        // Use the stack to track the road and then pop them and unlock them
        Stack<Node> stack = new Stack<Node>();
        Node node = null;
        int size = 0;
        try {
            // First, lock and push the head
            head.localLock.readLock().lock();
            node = head;
            stack.push(head);
            node = node.next;

            // Find the last node
            while (node.next != null) {
                node.localLock.readLock().lock();
                stack.push(node);
                node = node.next;
                size++;
            }

            // Lock the read lock of last node
            node.localLock.readLock().lock();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release the read lock of last node
            if (node != null) {
                node.localLock.readLock().unlock();
            }

            // Unlock the readLock of all nodes in the stack
            while (!stack.isEmpty()) {
                Node curr = stack.pop();
                curr.localLock.readLock().unlock();
            }
        }
        return size;
    }

    /**
     * Display the Linked List.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String res = "";
        try {
            Node node = head.next;
            while (node != null) {
                node.localLock.readLock().lock();
                sb.append(node.value);
                node.localLock.readLock().unlock();
                sb.append(", ");
                node = node.next;
            }
            if (sb.length() > 2) {
                res = sb.substring(0, sb.length() - 2);
            } else {
                res = sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return res;
    }
}