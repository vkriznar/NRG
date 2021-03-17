# NRG
Repository for all homework assignments for class NRG

# Homework 1
Point interpolation using Shepard's methods and Octree

## How to run
1. Checkout repository with ``git clone git@github.com:vkriznar/NRG.git``
2. Enter directory with code ``cd src/main/java``
3. Compile java code ``javac Main.java``
4. Run for basic method ``java Main < ../../../data/input1k.txt > ../../../data/output.raw --method basic --p 0.5 --min-x -1.5 --min-y -1.5 --min-z -1 --max-x 1.5 --max-y 1.5 --max-z 1 --res-x 128 --res-y 128 --res-z 64``
5. Run for modified method ``java Main < ../../../data/input1k.txt > ../../../data/output.raw --method modified --r 0.5 --min-x -1.5 --min-y -1.5 --min-z -1 --max-x 1.5 --max-y 1.5 --max-z 1 --res-x 128 --res-y 128 --res-z 64``
