package Lab8;

import Lab7.URLDepthPair;

import java.util.*;

public class URLPool {
    public static int max_depth;
    private LinkedList<URLDepthPair> pendingURLs;
    public LinkedList<URLDepthPair> processedURLs;
    public ArrayList<String> seenURLs = new ArrayList<String>();

    public int waitingThreads;

    public URLPool(int d) {
        waitingThreads = 0;
        pendingURLs = new LinkedList<URLDepthPair>();
        processedURLs = new LinkedList<URLDepthPair>();
        max_depth = d;
    }

    public synchronized int getWaitThreads() {
        return waitingThreads;
    }

    public synchronized int size() {
        return pendingURLs.size();
    }

    public synchronized void decrimentWaitingThreads() {
        waitingThreads--;
    }

    //добавить в пул ссылку
    public synchronized boolean put(URLDepthPair depthPair) {
        boolean added = false;

        if (depthPair.getDepth() < max_depth && !processedURLs.contains(depthPair.getURL())) {
            pendingURLs.addLast(depthPair);
            added = true;
            this.notify();
        } else {
            addSeenURL(depthPair);
        }

        return added;
    }

    //получить из пула ссылку
    public synchronized URLDepthPair get() {

        URLDepthPair myDepthPair = null;
        if (pendingURLs.size() == 0) {
            waitingThreads++;
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                System.err.println("InterruptedException: " + e.getMessage());
                return null;
            }
        }
        if(waitingThreads>0)waitingThreads--;
        myDepthPair = pendingURLs.pop();
        addSeenURL(myDepthPair);
        processedURLs.add(myDepthPair);
        return myDepthPair;
    }

    private synchronized void addSeenURL(URLDepthPair dp) {
        if(!seenURLs.contains(dp.toString()))
            seenURLs.add(dp.toString());
    }

    public synchronized ArrayList<String> getSeenList() {
        return seenURLs;
    }
}
