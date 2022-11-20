package org.moeaframework.gd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.CommentedLineReader;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;
import org.moeaframework.util.weights.WeightGenerator;

public class GDMOEADProvider extends RegisteredAlgorithmProvider {
	
	public GDMOEADProvider() {
		super();
		
		register(this::newGDMOEAD, "GD-MOEAD", "GD-MOEA/D");
	}
	
	private Algorithm newGDMOEAD(TypedProperties properties, Problem problem) {
		//provide weights
		WeightGenerator weightGenerator = null;
		
		if (properties.contains("targets")) {
			weightGenerator = new GeneralizedDecomposition(loadTargets(properties.getString("targets", null)));
		} else {
			NormalBoundaryDivisions divisions = NormalBoundaryDivisions.fromProperties(properties, problem);
			
			weightGenerator = new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(
					problem.getNumberOfObjectives(), divisions));
		}
		
		int populationSize = weightGenerator.size();
		
		//enforce population size lower bound
		if (populationSize < problem.getNumberOfObjectives()) {
			System.err.println("increasing MOEA/D population size");
			populationSize = problem.getNumberOfObjectives();
		}

		Initialization initialization = new RandomInitialization(problem, populationSize);

		//default to de+pm for real-encodings
		String operator = properties.getString("operator", null);
		
		if ((operator == null) && checkType(RealVariable.class, problem)) {
			operator = "de+pm";
		}

		Variation variation = OperatorFactory.getInstance().getVariation(operator, properties, problem);
		
		int neighborhoodSize = 20;
		int eta = 2;
		
		if (properties.contains("neighborhoodSize")) {
			neighborhoodSize = Math.max(2, 
					(int)(properties.getDouble("neighborhoodSize", 0.1) * populationSize));
		}
		
		if (neighborhoodSize > populationSize) {
			neighborhoodSize = populationSize;
		}
		
		if (properties.contains("eta")) {
			eta = Math.max(2, (int)(properties.getDouble("eta", 0.01) * populationSize));
		}
		
		return new MOEAD(
				problem,
				neighborhoodSize,
				weightGenerator,
				initialization,
				variation,
				properties.getDouble("delta", 0.9),
				eta,
				(int)properties.getDouble("updateUtility", -1));
	}
	
	private List<double[]> loadTargets(String resource) {
		File file = new File(resource);
		Population population = null;
		
		try {
			if (file.exists()) {
				population = PopulationIO.readObjectives(file);
			} else {
				try (InputStream input = getClass().getResourceAsStream("/" + resource)) {
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
	
	private boolean checkType(Class<? extends Variable> type, Problem problem) {
		Solution solution = problem.newSolution();
		
		for (int i=0; i<solution.getNumberOfVariables(); i++) {
			if (!type.isInstance(solution.getVariable(i))) {
				return false;
			}
		}
		
		return true;
	}

}
