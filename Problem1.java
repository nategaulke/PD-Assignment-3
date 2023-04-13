// Nathanael Gaulke
// Assignment 3, Problem 1: The Birthday Presents Party

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Node in the Linked list
class Gift {
    int id;
    Gift next;

    Gift(int id) {
        this.id = id;
        next = null;
    }

    Gift(int id, Gift next) {
        this.id = id;
        this.next = next;
    }
}

class LinkedList {
    private Gift head;
    private Lock lock = new ReentrantLock();
    public AtomicInteger size;

    public LinkedList() {
        size = new AtomicInteger(0);
    }

    public boolean add(int id) {
        Gift prev, curr;
        lock.lock();
        try {
            if (size.getAndIncrement() == 0) {
                Gift g = new Gift(id);
                head = g;
                return true;
            }
            Gift node = new Gift(id);
            if (id < head.id) {
                node.next = head;
                head = node;
                return true;
            }
            curr = head;
            while (curr.next != null && curr.next.id < id) {
                curr = curr.next;
            }
            node.next = curr.next;
            curr.next = node;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(int id) {
        Gift curr;
        lock.lock();
        try {
            if (size.get() == 0) {
                return false;
            }
            curr = head;
            while (curr != null && curr.id < id) {
                curr = curr.next;
            }
            if (curr != null && curr.id == id)
                return true;
            return false;
        } finally {
            lock.unlock();
        }
    }

    // Only can remove from head of list
    public int remove() {
        lock.lock();
        try {
            if (size.get() == 0)
                return -1;
            int val = head.id;
            head = head.next;
            size.decrementAndGet();
            return val;
        } finally {
            lock.unlock();
        }
    }
}

class Servant implements Runnable {
    public int id;

    Servant(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (Problem1.counter.get() < Problem1.NUMBER_PRESENTS || Problem1.list.size.get() > 0) {
            // Pick a random value
            int choice, randomNum = 0;
            if (Problem1.counter.get() < Problem1.NUMBER_PRESENTS)
                choice = ThreadLocalRandom.current().nextInt(1, 4);
            else
                choice = ThreadLocalRandom.current().nextInt(2, 4);
            switch (choice) {
                // If 1, go increment counter and add a present to the chain
                case 1:
                    Problem1.counter.getAndIncrement();
                    randomNum = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                    while (Problem1.set.contains(randomNum))
                        randomNum = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                    Problem1.set.add(randomNum);
                    Problem1.list.add(randomNum);
                    if (Problem1.DEBUG == 1)
                        System.out
                                .println(
                                        "Servant " + id + " added the gift tagged with " + randomNum + " to the list.");
                    break;
                // If 2, remove a present from the head to write a thank you note
                case 2:
                    int val = Problem1.list.remove();
                    if (val < 0 && Problem1.DEBUG == 1)
                        System.out
                                .println("Servant " + id + " tried to write a thank you note but the list was empty.");
                    else if (Problem1.DEBUG == 1)
                        System.out.println(
                                "Servant " + id + " wrote a thank you note for gift with the tag number " + val + ".");
                    break;
                // If 3, find a gift with a random number as the tag
                case 3:
                    randomNum = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                    if (Problem1.list.contains(randomNum) && Problem1.DEBUG == 1)
                        System.out
                                .println("Servant " + id + " found the gift tagged with" + randomNum + " in the list.");
                    else if (Problem1.DEBUG == 1)
                        System.out.println("Servant " + id + " could not find the gift tagged with " + randomNum
                                + " in the list.");
                    break;
            }
        }
    }
}

// Create own linked list implementation I guess

public class Problem1 {
    public static final int NUMBER_PRESENTS = 500000;
    public static final int NUMBER_SERVANTS = 4;
    public static LinkedList list;
    public static AtomicInteger counter;
    public static Set<Integer> set;
    public static final int DEBUG = 0;

    // Minotaur
    public static void main(String[] args) {
        list = new LinkedList();
        // for checking how many presents that need to be added (checking if threads
        // should still run)
        counter = new AtomicInteger();
        // Hashmap ensures unique id tags
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        set = map.newKeySet();
        for (int i = 0; i < NUMBER_SERVANTS; i++) {
            Servant s = new Servant(i + 1);
            Thread th = new Thread(s);
            th.start();
        }

    }
}