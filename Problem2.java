// Nathanael Gaulke
// Assignment 3, Problem 2: Atmospheric Temperature Reading Module

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

class Sensor implements Runnable {
    public int id;
    private int sensorReadings;

    Sensor(int id) {
        this.id = id;
        this.sensorReadings = 0;
    }

    @Override
    public void run() {
        // Need to create 60 temperature readings in an hour
        int min = 0, max = 0;
        while (sensorReadings != 60) {
            try {
                // simulates a minute
                // program should be roughly this amount * 60
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("Sleep error");
            }
            int temp = ThreadLocalRandom.current().nextInt(-100, 71);
            if (Problem2.DEBUG == 1)
                System.out.println("Sensor " + id + "'s temperature reading: " + temp);
            if (sensorReadings == 0 || (sensorReadings % 10) == 0) {
                min = max = temp;
            } else {
                if (temp < min) {
                    min = temp;
                }
                if (temp > max) {
                    max = temp;
                }
            }
            // Add to heaps (determine later top 5, bottom 5 later)
            Problem2.minHeap.add(temp);
            Problem2.maxHeap.add(temp);
            sensorReadings++;
            if ((sensorReadings % 10) == 0) {
                if (Problem2.DEBUG == 1)
                    System.out.println("Difference found in Sensor " + id + ": " + (max - min));
                Problem2.maxDiff.add((max - min));
            }
        }
    }
}

// Used to create max Headp
class MaxComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer x, Integer y) {
        return y - x;
    }
}

class Heap {
    private PriorityQueue<Integer> h;
    private Lock lock = new ReentrantLock();

    public Heap(int minMax) {
        // min
        if (minMax == 0)
            h = new PriorityQueue<>();
        // max
        else {
            h = new PriorityQueue<>(11, new MaxComparator());
        }
    }

    public boolean add(int temp) {
        lock.lock();
        try {
            return h.add(temp);
        } finally {
            lock.unlock();
        }
    }

    public int remove() {
        lock.lock();
        try {
            int retval = h.peek();
            h.remove(h.peek());
            return retval;
        } finally {
            lock.unlock();
        }
    }

    public int[] firstFive() {
        int[] retval = new int[5];
        if (h.size() < 5) {
            return retval;
        }
        for (int i = 0; i < 5; i++) {
            retval[i] = this.remove();
        }
        return retval;
    }

    public int size() {
        return h.size();
    }
}

public class Problem2 {
    public static final int NUMBER_SENSORS = 8;
    public static final int DEBUG = 0;
    public static Heap minHeap;
    public static Heap maxHeap;
    public static Heap maxDiff;

    public static void main(String[] args) throws Exception {
        minHeap = new Heap(0);
        maxHeap = new Heap(1);
        maxDiff = new Heap(1);
        Thread[] th = new Thread[NUMBER_SENSORS];
        for (int i = 0; i < NUMBER_SENSORS; i++) {
            Sensor s = new Sensor(i + 1);
            th[i] = new Thread(s);
            th[i].start();
        }
        for (int i = 0; i < NUMBER_SENSORS; i++) {
            th[i].join();
        }
        if (60 * NUMBER_SENSORS != maxHeap.size() || maxHeap.size() != minHeap.size()) {
            System.err.println("Error, incorrect number of readings processed");
            return;
        }
        System.out.println("Hour 1 Report:");
        System.out.println("Lowest temperature readings: " + Arrays.toString(minHeap.firstFive()));
        System.out.println("Highest temperature readings: " + Arrays.toString(maxHeap.firstFive()));
        System.out.println("Biggest 10-minute temperature: " + maxDiff.remove());
    }
}
