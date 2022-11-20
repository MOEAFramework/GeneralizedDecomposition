# Generalized Decomposition

Demonstrates how to use generalized decomposition (GD) within the [MOEA Framework](http://github.com/MOEAFramework/MOEAFramework).
GD determines the correct Chebychev (Tchebycheff) scalarizing function weights that attain solutions near a given set of target
points.  The weights produced by GD can be fed into optimization algorithms, such as MOEA/D, to produce more desirable results
compared to randomly-generated weights.

The example provided in this repo creates and compares MOEA/D against this new version with generalized decomposition, named GD-MOEA/D.
