/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jefix.synth.conditional;

import java.io.File;
import java.net.URL;

import fr.inria.lille.jefix.SourceLocation;
import fr.inria.lille.jefix.patch.Level;
import fr.inria.lille.jefix.patch.Patch;
import fr.inria.lille.jefix.synth.InputOutputValues;
import fr.inria.lille.jefix.synth.RepairCandidate;
import fr.inria.lille.jefix.synth.Synthetizer;
import fr.inria.lille.jefix.synth.smt.constraint.ConstraintSolver;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.InputModelBuilder;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConditionalSynthetizer implements Synthetizer {

	private final ConditionalsConstraintModelBuilder constraintbuilder;

	public ConditionalSynthetizer(final File sourceFolder, final SourceLocation sourceLocation) {
		this.constraintbuilder = new ConditionalsConstraintModelBuilder(sourceFolder, sourceLocation);
	}

	/**
	 * @see fr.inria.lille.jefix.synth.Synthetizer#buildPatch(java.net.URL[], java.lang.String[])
	 */
	@Override
	public Patch buildPatch(final URL[] classpath, final String[] testClasses) {

		InputOutputValues data = this.constraintbuilder.buildFor(classpath, testClasses);
		InputModelBuilder modelBuilder = new InputModelBuilder(data);
		ConstraintSolver constraintSolver = new ConstraintSolver();
		Level level = Level.CONSTANTS;
		InputModel model = modelBuilder.buildFor(level);
		RepairCandidate newRepair = constraintSolver.solve(model);

		while (null != newRepair && level != Level.MULTIPLICATION) {
			level = level.next();
			model = modelBuilder.buildFor(level);
			newRepair = constraintSolver.solve(model);
		}
		throw new UnsupportedOperationException("Undefined method ConditionalSynthetizer.buildPatch");
	}
}
