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
		
		// collect data for MOEA/D with Generalized Decomposition - by targeting the Pareto front points, we should
		// get extremely close to the optimum
		analyzer.addAll("GD-MOEA/D", executor
				.withAlgorithm("GD-MOEA/D")
				.withProperty("targets", "pf/DTLZ2.3D.pf")
				.runSeeds(50));
		
		analyzer.display();
	}

}
