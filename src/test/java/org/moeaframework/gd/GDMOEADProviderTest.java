package org.moeaframework.gd;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;

public class GDMOEADProviderTest {
	
	@Test
	public void test() {
		NondominatedPopulation result = new Executor()
				.withProblem("DTLZ2_3")
				.withAlgorithm("GD-MOEA/D")
				.withMaxEvaluations(10000)
				.run();
		
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() > 0);
	}

}
