package org.moeaframework.gd;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

public class Example {
	
	public static void main(String[] args) {
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
				.withProperty("targets", "pf/DTLZ2.3D.pf")
				.runSeeds(50));
		
		analyzer.printAnalysis();

	}

}
