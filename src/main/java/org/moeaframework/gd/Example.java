package org.moeaframework.gd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;
import org.moeaframework.util.weights.WeightGenerator;

public class Example {
	
	public static void main(String[] args) {
		AlgorithmFactory.getInstance().addProvider(new RegisteredAlgorithmProvider() {
			
			{
				register(Example::newGDMOEAD, "GD-MOEAD", "GD-MOEA/D");
			}
			
		});
		
		Executor executor = new Executor()
				.withProblem("DTLZ2_3")
				.withMaxEvaluations(10000);
		
		Analyzer analyzer = new Analyzer()
				.withSameProblemAs(executor)
				.includeHypervolume()
				.includeGenerationalDistance()
				.showStatisticalSignificance();
		
		// collect data for regular MOEA/D
		analyzer.addAll("MOEA/D", executor
				.withAlgorithm("MOEA/D")
				.runSeeds(50));
		
		// collect data for MOEA/D with Generalized Decomposition - by targeting the Pareto front
		// points, we should get extremely close to the optimum
		analyzer.addAll("GD-MOEA/D", executor
				.withAlgorithm("GD-MOEA/D")
				.withProperty("operator", "de+pm") // this is the default for MOEA/D
				.withProperty("targets", "DTLZ2.3D.pf")
				.runSeeds(50));
		
		analyzer.printAnalysis();

	}
	
	private static Algorithm newGDMOEAD(TypedProperties properties, Problem problem) {
		int populationSize;
		
		//provide weights
		WeightGenerator weightGenerator = null;
		
		if (properties.contains("targets")) {
			try {
				Population population = PopulationIO.readObjectives(
						new File(properties.getString("targets", null)));
				
				List<double[]> targets = new ArrayList<double[]>();
				
				for (Solution solution : population) {
					targets.add(solution.getObjectives());
				}
				
				populationSize = targets.size();
				weightGenerator = new GeneralizedDecomposition(targets);
			} catch (IOException e) {
				throw new FrameworkException("failed to load targets", e);
			}
		} else {
			NormalBoundaryDivisions divisions = NormalBoundaryDivisions.fromProperties(properties, problem);
			
			populationSize = divisions.getNumberOfReferencePoints(problem);
			weightGenerator = new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(
					problem.getNumberOfObjectives(), divisions));
		}
		
		//enforce population size lower bound
		if (populationSize < problem.getNumberOfObjectives()) {
			System.err.println("increasing MOEA/D population size");
			populationSize = problem.getNumberOfObjectives();
		}

		Initialization initialization = new RandomInitialization(problem, populationSize);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);
		
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

}
