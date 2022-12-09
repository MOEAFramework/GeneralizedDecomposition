package org.moeaframework.gd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.CommentedLineReader;
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
		this(problem,
				new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(
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
		Population population = null;
		
		try {
			File file = new File(resource);
			
			if (file.exists()) {
				population = PopulationIO.readObjectives(file);
			} else {
				try (InputStream input = GDMOEAD.class.getResourceAsStream("/" + resource)) {
					if (input != null) {
						population = PopulationIO.readObjectives(new CommentedLineReader(new InputStreamReader(input)));
					}
				}
			}
		} catch (IOException e) {
			throw new FrameworkException("failed to load " + resource, e);
		}
		
		if (population == null) {
			throw new FrameworkException("could not find " + resource);
		}
		
		List<double[]> targets = new ArrayList<double[]>();
		
		for (Solution solution : population) {
			targets.add(solution.getObjectives());
		}
		
		return targets;
	}
	
}
