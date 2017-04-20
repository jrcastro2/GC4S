package es.uvigo.ei.sing.hlfernandez.demo;

import static es.uvigo.ei.sing.hlfernandez.demo.DemoUtils.createPanelAndCenterComponent;
import static es.uvigo.ei.sing.hlfernandez.demo.DemoUtils.showComponent;
import static es.uvigo.ei.sing.hlfernandez.visualization.ColorLegend.Orientation.VERTICAL;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import es.uvigo.ei.sing.hlfernandez.visualization.ColorLegend;

/**
 * An example showing the use of {@link ColorLegend}.
 * 
 * @author hlfernandez
 *
 */
public class ColorLegendDemo {

	public static void main(String[] args) {
		ColorLegend colorLegend = new ColorLegend(getColors(), VERTICAL);
		showComponent(
			createPanelAndCenterComponent(colorLegend),
			"ColorLegend demo"
		);
	}

	private static Map<String, Color> getColors() {
		Map<String, Color> colors = new HashMap<>();
		colors.put("Red", 	Color.RED);
		colors.put("Blue", 	Color.BLUE);
		colors.put("Green",	Color.GREEN);
		return colors;
	}
}