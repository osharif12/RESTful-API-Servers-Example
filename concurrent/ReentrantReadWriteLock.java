package concurrent;

import java.util.HashMap;

/**
 * A custom reentrant read/write lock that allows:
 * 1) Multiple readers (when there is no writer). Any thread can acquire multiple read locks (if nobody is writing).
 * 2) One writer (when nobody else is writing or reading).
 * 3) A writer is allowed to acquire a read lock while holding the write lock.
 * 4) A writer is allowed to acquire another write lock while holding the write lock.
 * 5) A reader can not acquire a write lock while holding a read lock.
 *
 * Use ReentrantReadWriteLockTest to test this class.
 * The code is modified from the code of Prof. Rollins.
 */

public class ReentrantReadWriteLock {
    private HashMap<Long, Integer> readersMap; // long is threadID, int is #readers
    private HashMap<Long, Integer> writersMap;

    /**
     * Constructor for ReentrantReadWriteLock
     */
    public ReentrantReadWriteLock() {
        readersMap = new HashMap<>();
        writersMap = new HashMap<>();
    }

    /**
     * Return true if the current thread holds a read lock.
     *
     * @return true or false
     */
    public synchronized boolean isReadLockHeldByCurrentThread() {
        long threadId = Thread.currentThread().getId();

        if(readersMap.containsKey(threadId))
            if(readersMap.get(threadId) >= 1)
                return true;

        return false;
    }

    /**
     * Return true if the current thread holds a write lock.
     *
     * @return true or false
     */
    public synchronized boolean isWriteLockHeldByCurrentThread() {
        long threadId = Thread.currentThread().getId();

        if(writersMap.containsKey(threadId))
            if(writersMap.get(threadId) >= 1)
                return true;

        return false;
    }

    /**
     * Non-blocking method that attempts to acquire the read lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the read lock), and if they are true,
     * updates readers info.
     *
     * Note that if conditions are false (can not acquire the read lock at the moment), this method
     * does NOT wait, just returns false
     * @return
     */
    public synchronized boolean tryAcquiringReadLock() {
        long threadId = Thread.currentThread().getId();
        int readers = 0;

        if(writersMap.isEmpty()){ // if no writers
            if(readersMap.containsKey(threadId))    // if thread already has read locks
                readers = readersMap.remove(threadId);
            readers++;
            readersMap.put(threadId, readers);
            return true;
        }
        else{ // if there are writers
            if(writersMap.containsKey(threadId)){
                if(readersMap.containsKey(threadId)){
                    readers = readersMap.remove(threadId);
                }
                readers++;
                readersMap.put(threadId, readers);
                return true;
            }
            return false;
        }
    }

    /**
     * Non-blocking method that attempts to acquire the write lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the write lock), and if they are true,
     * updates writers info.
     *
     * Note that if conditions are false (can not acquire the write lock at the moment), this method
     * does NOT wait, just returns false
     *
     * @return
     */
    public synchronized boolean tryAcquiringWriteLock() {
        long threadId = Thread.currentThread().getId();
        int writers = 0;

        if(writersMap.isEmpty()){ // if there are no writers
            if(readersMap.isEmpty()){ // if there are no readers
                writers++;
                writersMap.put(threadId, writers);
                return true;
            }
            return false;
        }
        else{   // if a thread is holding a write lock, it can acquire another write lock on the same object
            if(writersMap.containsKey(threadId)){
                writers = writersMap.remove(threadId);
                writers++;
                writersMap.put(threadId, writers);  // now thread is holding two write locks
                return true;
            }
            else
                return false;
        }
    }

    /**
     * Blocking method that will return only when the read lock has been
     * acquired.
     * Calls tryAcquiringReadLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     *
     */
    public synchronized void lockRead() {

        while(!tryAcquiringReadLock()){
            try{
                wait(); // possibly change
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * Releases the read lock held by the calling thread. Other threads might
     * still be holding read locks. If no more readers after unlocking, calls notifyAll().
     */
    public synchronized void unlockRead() {
        long threadId = Thread.currentThread().getId();
        int readers;

        if(readersMap.containsKey(threadId)){
            readers = readersMap.remove(threadId);
            readers--;

            if(readers != 0)    // if readers = 0, do not put the readersMap object/thread back in
                readersMap.put(threadId, readers);
            else if(readersMap.isEmpty()) // if there are no more readers, notifyAll
                notifyAll();
        }

    }

    /**
     * Blocking method that will return only when the write lock has been
     * acquired.
     * Calls tryAcquiringWriteLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockWrite() {
        while(!tryAcquiringWriteLock()){
            try{
                wait();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Releases the write lock held by the calling thread. The calling thread
     * may continue to hold a read lock.
     * If the number of writers becomes 0, calls notifyAll.
     */

    public synchronized void unlockWrite() {
        long threadId = Thread.currentThread().getId();
        int writers;

        if(writersMap.containsKey(threadId)){
            writers = writersMap.remove(threadId);
            writers--;

            if(writers != 0)
                writersMap.put(threadId, writers);
            else if(writersMap.isEmpty())
                notifyAll();
        }
    }
}
