/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.gd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;
import org.moeaframework.util.weights.WeightGenerator;

public class GDMOEAD extends MOEAD {

	public GDMOEAD(Problem problem) {
		this(problem, NormalBoundaryDivisions.forProblem(problem));
	}
	
	public GDMOEAD(Problem problem, String targets) {
		this(problem, new GeneralizedDecomposition(loadTargets(targets)));
	}
	
	public GDMOEAD(Problem problem, NormalBoundaryDivisions divisions) {
		this(problem, new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(
				problem.getNumberOfObjectives(), divisions)));
	}
	
	public GDMOEAD(Problem problem, WeightGenerator weightGenerator) {
		super(problem,
				weightGenerator.size(),
				20, //neighborhoodSize
				weightGenerator,
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation(problem.isType(RealVariable.class)? "de+pm": null, problem),
				0.9, //delta
				2, //eta
				-1); //updateUtility
	}

	private static List<double[]> loadTargets(String resource) {
		try {
			File file = Resources.asFile(GDMOEAD.class, "/" + resource, ResourceOption.REQUIRED,
					ResourceOption.TEMPORARY, ResourceOption.FILE);
			
			Population population = Population.loadObjectives(file);
			
			List<double[]> targets = new ArrayList<double[]>();
			
			for (Solution solution : population) {
				targets.add(solution.getObjectives());
			}
			
			return targets;
		} catch (IOException e) {
			throw new FrameworkException("failed to load " + resource, e);
		}
	}
	
}
