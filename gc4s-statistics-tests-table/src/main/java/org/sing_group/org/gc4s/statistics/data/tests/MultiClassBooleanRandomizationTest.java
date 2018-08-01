/*
 * #%L
 * GC4S statistics tests table
 * %%
 * Copyright (C) 2014 - 2018 Hugo López-Fernández, Daniel Glez-Peña, Miguel Reboiro-Jato,
 * 			Florentino Fdez-Riverola, Rosalía Laza-Fidalgo, Reyes Pavón-Rial
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.sing_group.org.gc4s.statistics.data.tests;

import static java.util.Optional.of;

import java.util.Optional;

import es.uvigo.ei.sing.math.statistical.tests.RandomizationTestOfIndependence;

/**
 * An {@code AbstractBooleanTest} to compute the randomization test of
 * independence on a feature values set with two or more conditions.
 *
 * @author hlfernandez
 *
 */
public class MultiClassBooleanRandomizationTest extends AbstractBooleanTest {
	/**
	 * Creates a new {@code MultiClassBooleanRandomizationTest} instance.
	 */
	public MultiClassBooleanRandomizationTest() {
		super(new RandomizationTestOfIndependence());
	}

	@Override
	public String getName() {
		return "Randomization test of independence";
	}

	@Override
	public Optional<String> getAdditionalInfoUrl() {
		return of("http://udel.edu/~mcdonald/statrandind.html");
	}
}