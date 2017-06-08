/**
 * Thread Safe HashMap, modified based on HashMap in Java Collections, the key cannot be null.
 *
 * @author     Hao Wang
 * 
 * 05/2017
 */

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.*;

public class ThreadSafeHashMap {

    /**
     * The Entry of the value of HashMap.
     */
    class Entry {
        public String value;
        public ReadWriteLock localLock;

        public Entry(String val) {
            this.value = val;
            this.localLock = new ReentrantReadWriteLock();
        }
    }

    /**
     * Instance Variable, the global hashmap.
     */
    private Map<String, Entry> map;

    /**
     * Instance Variable, the global lock.
     */
    private ReadWriteLock glocalLock;

    /**
     * Constructor of the Thread Safe HashMap
     */
    ThreadSafeHashMap() {
        this.map = new HashMap<String, Entry>();
        this.glocalLock = new ReentrantReadWriteLock();
    }

    /**
     * Clear the old HashMap and create the new one.
     */
    public void clear() {
        try {
            glocalLock.writeLock().lock();
            map = new HashMap<String, Entry>();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            glocalLock.writeLock().unlock();
        }
    }

    /**
     * Determines if the map contains the key.
     *
     * @param      key   The key
     *
     * @return     True if contains key, False otherwise.
     */
    public boolean containsKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        boolean res = false;

        try {
            glocalLock.readLock().lock();
            res = map.containsKey(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            glocalLock.readLock().unlock();
        }
        return res;
    }

    /**
     * Get the value stored in the HashMap.
     *
     * @param      key   The key
     *
     * @return     The String Value stored in the HashMap
     */
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        Entry entry = null;
        String res = null;

        try {
            glocalLock.readLock().lock();
            entry = map.get(key);
            entry.localLock.readLock().lock();
            res = entry.value;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entry != null) {
                entry.localLock.readLock().unlock();
            }
            glocalLock.readLock().unlock();
        }
        return res;
    }

    /**
     * Put the (key, value) pair into HashMap.
     *
     * @param      key    The key
     * @param      value  The value
     *
     * @return     Return the value inserted in the HashMap
     */
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null!");
        }

        Entry entry = new Entry(value);
        String res = null;
        try {
            glocalLock.writeLock().lock();
            entry.localLock.writeLock().lock();
            map.put(key, entry);
            res = value;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entry.localLock.writeLock().unlock();
            glocalLock.writeLock().unlock();
        }
        return res;
    }

    /**
     * Determines if the Linked List is empty.
     *
     * @return     True if empty, False otherwise.
     */
    public boolean isEmpty() {
        boolean res = false;

        try {
            glocalLock.readLock().lock();
            res = map.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            glocalLock.readLock().unlock();
        }
        return res;
    }

    /**
     * Remove the element with the given key from the HashMap.
     *
     * @param      key   The key
     *
     * @return     The String Value stored in the HashMap
     */
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        Entry entry = null;
        String res = null;
        try {
            glocalLock.writeLock().lock();
            entry = map.get(key);
            if (entry != null) {
                res = entry.value;
                entry.localLock.writeLock().lock();
                map.remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entry != null) {
                entry.localLock.writeLock().unlock();
            }
            glocalLock.writeLock().unlock();
        }
        return res;
    }

    /**
     * Get the size of the Linked List.
     *
     * @return     The size of the Linked List.
     */
    public int size() {
        int res = 0;

        try {
            glocalLock.readLock().lock();
            res = map.size();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            glocalLock.readLock().unlock();
        }
        
        return res;
    }

    /**
     * Display the HashMap.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            glocalLock.readLock().lock();
            for (String key : map.keySet()) {
                Entry entry = map.get(key);
                entry.localLock.readLock().lock();
                String value = entry.value;
                entry.localLock.readLock().unlock();
                sb.append("[");
                sb.append(key);
                sb.append(", ");
                sb.append("] ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            glocalLock.readLock().unlock();
        }
        return sb.toString().trim();
    }
}