package org.sing_group.gc4s.utilities;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

/**
 * Class that generates color gradients.
 * 
 * @author mrjato
 *
 */
public class Gradient {
	/**
	 * Creates an array of Color objects for use as a gradient, using a linear
	 * interpolation between the two specified colors. The "from" and "to" colors
	 * will be the initial and final colors of the gradient. This means that a
	 * gradient with 2 steps will return only the "from" and "to" colors.
	 * 
	 * If {@code numSteps} is equal to 1, the "from" color will be returned as the
	 * unique color in the gradient.
	 * 
	 * @param colorFrom
	 *            Color used as the initial color of the gradient.
	 * @param colorTo
	 *            Color used as the final color of the gradient.
	 * @param numSteps
	 *            The number of steps in the gradient.
	 * 
	 * @return a {@code Color[]} with the gradient.
	 * @throws IllegalArgumentException
	 *             if {@code numSteps} is lower than 1.
	 */
	public static Color[] createGradient(final Color colorFrom, final Color colorTo, final int numSteps) {
		if (numSteps < 1)
			throw new IllegalArgumentException("numSteps must be higher than 0");
		if (numSteps == 1)
			return new Color[] { colorFrom };
		
		final Function<ToIntFunction<Color>, IntUnaryOperator> interpolationBuilder = getPrimary -> {
			final int primaryFrom = getPrimary.applyAsInt(colorFrom);
			final int primaryTo = getPrimary.applyAsInt(colorTo);
			
			final int distance = primaryTo - primaryFrom;
			
			return step -> (int) (primaryFrom + (step / (double) (numSteps - 1)) * distance);
		};
		
		final IntUnaryOperator redInterpolation = interpolationBuilder.apply(Color::getRed);
		final IntUnaryOperator greenInterpolation = interpolationBuilder.apply(Color::getGreen);
		final IntUnaryOperator blueInterpolation = interpolationBuilder.apply(Color::getBlue);
		final IntUnaryOperator alphaInterpolation = interpolationBuilder.apply(Color::getAlpha);
		
		final IntFunction<Color> colorInterpolator = step -> new Color(
			redInterpolation.applyAsInt(step),
			greenInterpolation.applyAsInt(step),
			blueInterpolation.applyAsInt(step),
			alphaInterpolation.applyAsInt(step)
		);
		
		return IntStream.range(0, numSteps)
			.mapToObj(colorInterpolator)
		.toArray(Color[]::new);
	}
}