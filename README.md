# Generalized Decomposition

Extends the [MOEA Framework](http://github.com/MOEAFramework/MOEAFramework) with a version of MOEA/D using weights produced by
Generalized Decomposition (GD).

> Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013). "Generalized Decomposition."  Evolutionary Multi-Criterion
> Optimization, 7th International Conference, pp. 428-442.

Very briefly, MOEA/D typically uses randomly-generated weights that aim to produce a diverse set of solutions.  However, on
problems with disjoint or oddly shaped Pareto fronts, these weights might not lead to uniformly-spaced solutions.  Additionally,
modern algorithms like NSGA-III have the option to supply "target points", allowing the algorithm to focus and discover
solutions near these targets.

GD addresses these concerns by determining the correct weights (for the Chebychev / Tchebycheff scalarizing function used by
MOEA/D) needed to produce Pareto solutions near a given set of target points.  This way, if we know approximately the location or
shape of the Pareto front, we can direct the optimization algorithm towards that region.

## Usage

To use, reference the `GD-MOEA/D` algorithm and supply a `"targets"` property pointing to a file containing the target
points (objective values only).  If no `"targets"` is provided, it defaults to targeting uniformly-spaced points generated
by the Normal Boundary Intersection method.

```java

NondominatedPopulation result = new Executor()
		.withProblem("DTLZ2_3")
		.withAlgorithm("GD-MOEA/D")
		.withProperty("targets", "pf/DTLZ2.3D.pf")  // Target Pareto-optimal points
		.withMaxEvaluations(10000)
		.run();
```

## Bundling with the MOEA Framework

To use this algorithm within the MOEA Framework, first compile and package this project using Maven:

```

mvn package
```

Then, copy the `.jar` file, typically `target/gd-1.0.0.jar`, into the `lib/` directory of the MOEA Framework.
You can then reference this algorithm as demonstrated in the example above.


The resulting algorithm, called `GD-MOEA/D`, can be used within the MOEA Framework by including this library in the
classpath.

## License

Copyright 2009-2022 David Hadka and other contributors.  All rights reserved.

The MOEA Framework is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at your
option) any later version.

The MOEA Framework is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
