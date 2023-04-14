# Problem 1

## How to Run

`bash run1.sh` to run efficiently or simply `javac Problem1.java` then `java Problem1` if you wish to test the time of `java Problem1`. To see print statements, simply change the value of the variable `DEBUG` to 1.

## Proof of Correctness and Efficiency

Each thread continues running until not only all presents have been added to the chain, but also the chain is completely empty (meaning no more gifts need a thank you card). The list locks down each function to ensure that the list can't be used unpredicatably. By generating tags for each present that are from 0 to `Integer.MAX_VALUE`, we limit the amount of collisions in the hash map which checks whether a gift with a certain tag has already been created. We could randomly generate an unordered list, but this would probably increase the runtime overall.

## Experimentation

After running 3 trials of this program with 4 threads and 500,000, the program took 1.131 seconds to run on average. If we increase the number of servants to 8, the program takes 1.184 seconds to run on average. Theoretically, the program should run faster with more servants but due to the random nature of whether servants are writing thank you notes, adding gifts, or finding a gift, this is probably because more need to properly utilized for adding presents. Going back to 4 servants, but increasing the number of presents to 1,000,000, takes about 2.028 seconds to run on average.

# Problem 2

## How to Run

`bash run2.sh` to run efficiently or simply `javac Problem2.java` then `java Problem2` if you wish to test the time of `java Problem2`. To see print statements, simply change the value of the variable `DEBUG` to 1.

## Proof of Correctness and Efficiency

Each thread controls how long it stops before taking a temperature reading and keeps track of how many temperature readings it has taken, thus limiting the number of shared resources and possible conflicts. By not calculating the mins and maxes in real time and instead relying on the shared resource to calculate it, we avoid doing calculating across threads mins and maxes and temperature differences. Finally, dividing each into different heaps means that while one heap is busy, another can be utilized by a different thread.

## Experimentation

Assuming, that a minute is represented using a second, the program takes 60.636 seconds to run (after averaging 3 trials). Overall, this is pretty accurate to the 60 seconds expected, with less than a second added to the runtime. If we double the number of threads to 16, the program takes 60.701 seconds to run (after again averaging 3 trials). This means that overall, the threads are avoiding collding with each other as they record their temperatures, no matter the number of threads used.
