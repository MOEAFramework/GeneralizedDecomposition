package org.moeaframework.gd;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

public class GDMOEADProvider extends RegisteredAlgorithmProvider {
	
	public GDMOEADProvider() {
		super();
		
		register(this::newGDMOEAD, "GD-MOEAD", "GD-MOEA/D");
	}
	
	private Algorithm newGDMOEAD(TypedProperties properties, Problem problem) {
		GDMOEAD algorithm;
		
		if (properties.contains("targets")) {
			algorithm = new GDMOEAD(problem, properties.getString("targets"));
		} else {
			algorithm = new GDMOEAD(problem, NormalBoundaryDivisions.fromProperties(properties, problem));
		}
		
		algorithm.applyConfiguration(properties);
		return algorithm;
	}

}
