# Generalized Decomposition

Extends the [MOEA Framework](http://github.com/MOEAFramework/MOEAFramework) with a version of MOEA/D using weights produced by
Generalized Decomposition (GD).

> Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013). "Generalized Decomposition."  Evolutionary Multi-Criterion
> Optimization, 7th International Conference, pp. 428-442.

Very briefly, MOEA/D typically uses randomly-generated weights that aim to produce a diverse set of solutions.  While newer
algorithms like NSGA-III have the option to supply "target points" where the algorithm can focus search, picking weights for MOEA/D
that produce solutions near a given target is non-trivial.  This is further complicated when the Pareto front shape is
disjoint / multimodal.  

With GD, we can take a target point (or points) and compute the ideal weights for MOEA/D or any other algorithm using the
Chebychev / Tchebycheff scalarizing function.  Alternatively, we can use GD to compute a set of weights that should yield
better and more uniformly-distributed results than randomly-generated weights.

## Installation

Add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>org.moeaframework</groupId>
    <artifactId>gd</artifactId>
    <version>1.1</version>
</dependency>
```

## Usage

Reference the `GD-MOEA/D` algorithm as you would any other:

```java

NondominatedPopulation result = new Executor()
		.withProblem("DTLZ2_3")
		.withAlgorithm("GD-MOEA/D")
		.withProperty("targets", "pf/DTLZ2.3D.pf")  // Target Pareto-optimal points
		.withMaxEvaluations(10000)
		.run();
```

There is an optional `targets` property.  When set, it will use the target points defined in a file.
If unset, it will default to uniformly-spaced points generated using the Normal Boundary Intersection method.

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
