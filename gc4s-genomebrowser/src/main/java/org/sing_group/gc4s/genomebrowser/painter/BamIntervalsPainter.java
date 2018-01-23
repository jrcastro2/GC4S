package org.sing_group.gc4s.genomebrowser.painter;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sing_group.gc4s.genomebrowser.GenomeBrowser;
import org.sing_group.gc4s.genomebrowser.GenomeBrowserUtil;
import org.sing_group.gc4s.genomebrowser.TrackOption;
import org.sing_group.gc4s.genomebrowser.grid.GenericInfo;
import org.sing_group.gc4s.genomebrowser.grid.GridInfo;
import org.sing_group.gc4s.genomebrowser.grid.IntervalInfo;

import es.cnio.bioinfo.pileline.core.BamIntervalsIndex;
import es.cnio.bioinfo.pileline.core.Interval;
import es.cnio.bioinfo.pileline.core.IntervalsIndex;

/**
 * A {@code Painter} implementation to render {@code .bam} files.
 * 
 * @author hlfernandez
 *
 */
public class BamIntervalsPainter implements Painter {
	private File file;
	private IntervalsIndex interval;
	private GenomeBrowser genomeBrowser;

	private int currentTrackHeight;
	private int offset;

	private TrackOption backGroundColorOption;
	private TrackOption trackColorOption;
	private TrackOption trackNameOption;
	private TrackOption maxDepthOption;
	
	/**
	 * Creates a new {@code BamIntervalsPainter} for the specified track file.
	 * 
	 * @param file a bam file
	 */
	public BamIntervalsPainter(File file) {
		this.file = file;
		this.initializeOptions();
	}
	
	@Override
	public void init(GenomeBrowser genomeBrowser)
		throws RuntimeException, IOException {
		this.genomeBrowser = genomeBrowser;
		this.interval = new BamIntervalsIndex(this.file.getName(), this.file);

		Set<String> fileSequences = this.interval.getSequences();
		Set<String> genomeSequences = genomeBrowser.getGenomeIndex()
			.getSequences();
		HashSet<String> intersect = new HashSet<String>();
		for (String s : fileSequences) {
			if (genomeSequences.contains(s))
				intersect.add(s);
		}
		double intersectValue = 
			(double) intersect.size() / (double) (fileSequences.size());

		if (intersectValue < 0.5) {
			throw new RuntimeException("Sequences in genome and track ("
				+ this.file.getName() + ") have poor overlapping.");
		}
	}

	@Override
	public Collection<TrackOption> getOptions() {
		return Arrays.asList(
			trackColorOption, backGroundColorOption, 
			trackNameOption, maxDepthOption
		);
	}

	@Override
	public void paint(Graphics2D g2, GenomeBrowser genomeBrowser, int offset) {
		this.offset = offset;
		GenomeBrowserUtil.drawString(g2, this.getTrackName(), 60,
			genomeBrowser.getTracksPanel(), this.offset);
		renderFile(g2, this.file, genomeBrowser);
	}

	private void renderFile(Graphics2D g2, File current, GenomeBrowser genomeBrowser) {
		FontMetrics fm = g2.getFontMetrics();
		Line2D line = null;
		double ancho = (genomeBrowser.getTracksPanel().getWidth() * 0.75) / (genomeBrowser.getFinalPosition()-genomeBrowser.getInitialPosition());
		int trackPosition = 65;
    	Color currentColor = getTrackColor();
		g2.setColor(currentColor);

		HashMap<Integer,Long> floorToPosition = new HashMap<Integer, Long>();
		floorToPosition.put(0, new Long(0));
		int currentFloor = 0;
		Long lastRight;
		boolean outOfRangeEnd;
		boolean outOfRangeStart;
		
		int maxYposition = trackPosition;
		
		try {
			Iterator<Interval> result = this.interval.getOverlappingIntervals(
				genomeBrowser.getCurrentSequence(), 
				(int) genomeBrowser.getInitialPosition(),
				(int) genomeBrowser.getFinalPosition()
			);
		
			while (result.hasNext()) {
				Interval interval = result.next();
				outOfRangeEnd = false;
				outOfRangeStart = false;
				long start = interval.getStart();
				if (start < genomeBrowser.getInitialPosition()) {
					outOfRangeStart = true;
					start = genomeBrowser.getInitialPosition();
				}
				
				double xCoordinateStart= computeTrackLinePosition(start,ancho,genomeBrowser.getInitialPosition());
				
				long stop = interval.getStop() ;
				if (stop >  genomeBrowser.getFinalPosition())
					{
						outOfRangeEnd = true;
						stop =  genomeBrowser.getFinalPosition();
					}
				
				double xCoordinateStop = computeTrackLinePosition(stop+1,ancho,genomeBrowser.getInitialPosition());
				
				lastRight = floorToPosition.get(currentFloor);
				if (lastRight!=null)
				{
					if (lastRight >= start-1)
					{
						
						if (currentFloor < this.getMaxDepth() - 1) {
							trackPosition += 23;
							currentFloor++;
						} else {
							continue;
						}
					}

					if (lastRight < interval.getStart()) 
					{
						
						while (currentFloor > 0
							&& floorToPosition.get(currentFloor) != null
							&& floorToPosition.get(currentFloor) < start) {
							
							currentFloor--;
							trackPosition-=23;
						}
					}
				}
				
				floorToPosition.remove(currentFloor);
				floorToPosition.put(new Integer(currentFloor), stop);

				double lineXstart = genomeBrowser.getTracksPanel().getX(125) + xCoordinateStart;
				double lineXend = genomeBrowser.getTracksPanel().getX( 125) + xCoordinateStop;
				line = new Line2D.Double(
					lineXstart,
					genomeBrowser.getTracksPanel().getY(trackPosition, offset),
					lineXend,
					genomeBrowser.getTracksPanel().getY(trackPosition, offset)
				);
				g2.draw(line);

				if (!outOfRangeStart) {
					line = new Line2D.Double(
						genomeBrowser.getTracksPanel().getX(125) + xCoordinateStart,
						genomeBrowser.getTracksPanel().getY(trackPosition, offset)
							- 3,
						genomeBrowser.getTracksPanel().getX(125) + xCoordinateStart,
						genomeBrowser.getTracksPanel().getY(trackPosition, offset)
							+ 3);
					g2.draw(line);
				}
				if (!outOfRangeEnd) {
					line = new Line2D.Double(
						genomeBrowser.getTracksPanel().getX(125) + xCoordinateStop,
						genomeBrowser.getTracksPanel().getY(trackPosition, offset)
							- 3,
						genomeBrowser.getTracksPanel().getX(125) + xCoordinateStop,
						genomeBrowser.getTracksPanel().getY(trackPosition, offset)
							+ 3);
					g2.draw(line);
				}

				String data = interval.getData();
				
				boolean drawNucleotides = this.genomeBrowser.getTracksPanel().drawNucleotides(g2) > 0.0f;
				
				if (!drawNucleotides)
				{
					if(fm.stringWidth(data) + 4 < (xCoordinateStop - xCoordinateStart)) {
					g2.drawString(data, (int) (genomeBrowser.getTracksPanel().getX(125) + xCoordinateStart + ((xCoordinateStop - xCoordinateStart)/2) - (fm.stringWidth(data) / 2)), genomeBrowser.getTracksPanel().getY(trackPosition,offset) - 1);
					}
				} else {
					for(int i = 0; i < data.length(); i++) {
						String nucleotide = String.valueOf(data.toCharArray()[i]);
						g2.drawString(nucleotide, (int) (genomeBrowser.getTracksPanel().getX(125) + xCoordinateStart + (i*ancho) + ( (ancho/2) - (fm.stringWidth(nucleotide)/2))), genomeBrowser.getTracksPanel().getY(trackPosition -7));
					}
				}
				
				if (xCoordinateStop - xCoordinateStart > 1) {
					GenericInfo iI = new IntervalInfo(interval.getData(),
						String.valueOf(interval.getStart()),
						String.valueOf(interval.getStop()));
					GridInfo aux = new GridInfo(iI, GridInfo.INTERVALINFO);
					genomeBrowser.getTracksPanel().addCuadriculaInfo(
						(int) (genomeBrowser.getTracksPanel().getX(125)
							+ xCoordinateStart),
						genomeBrowser.getTracksPanel()
							.getY(trackPosition + offset - 65),
						aux);

					for (double x = genomeBrowser.getTracksPanel().getX(125)
						+ xCoordinateStart; x < genomeBrowser.getTracksPanel()
							.getX(125) + xCoordinateStop; x += genomeBrowser
								.getTracksPanel().getSquareWidth()) {
						GridInfo auxC = new GridInfo(iI, GridInfo.INTERVALINFO);
						genomeBrowser.getTracksPanel().addCuadriculaInfo((int) x,
							trackPosition
								- genomeBrowser.getTracksPanel().getSquareWidth() / 2
								+ offset - 65,
							auxC);
					}
				}

				if (trackPosition > maxYposition)
					maxYposition = trackPosition;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		trackPosition = maxYposition + 65;
		genomeBrowser.getTracksPanel().getY(trackPosition, offset);
	}

	private double computeTrackLinePosition(long base, double width,
		long initialPosition) {
		long shift = (base - initialPosition);
		return (shift * width);
	}

	@Override
	public void reset() {
		this.currentTrackHeight = 0;
	}

	@Override
	public synchronized int computeHeight(GenomeBrowser genomeBrowser) {
		if (this.currentTrackHeight == 0 && this.interval != null) {
			int maxFloor = 0;

			Iterator<Interval> result = this.interval.getOverlappingIntervals(
				genomeBrowser.getCurrentSequence(),
				(int) genomeBrowser.getInitialPosition(),
				(int) genomeBrowser.getFinalPosition());
			HashMap<Integer, Long> floorToPosition = new HashMap<Integer, Long>();
			floorToPosition.put(0, new Long(0));
			int currentFloor = 0;
			Long lastRight;
			maxFloor = 0;
			while (result.hasNext()) {
				Interval interval = result.next();
				long temp = interval.getStart();
				if (temp < genomeBrowser.getInitialPosition())
					temp = genomeBrowser.getInitialPosition();

				temp = interval.getStop();
				if (temp > genomeBrowser.getFinalPosition()) {
					temp = genomeBrowser.getFinalPosition();
				}

				lastRight = floorToPosition.get(currentFloor);
				if (lastRight != null) {

					if (lastRight >= interval.getStart() - 1) {

						if (currentFloor < this.getMaxDepth() - 1) {
							currentFloor++;
						} else {
							continue;
						}
					}

					if (lastRight < interval.getStart()) {

						while (currentFloor > 0
							&& floorToPosition.get(currentFloor) != null
							&& floorToPosition.get(currentFloor) < interval
								.getStart()) {

							currentFloor--;

						}
					}
				}

				floorToPosition.remove(currentFloor);
				floorToPosition.put(new Integer(currentFloor),
					interval.getStop());

				if (currentFloor > maxFloor) {
					maxFloor = currentFloor;
				}

			}

			int height = ((maxFloor+1) * 23) + 5;
			this.currentTrackHeight = height + 65;
		}
		return currentTrackHeight;
	}

	private void initializeOptions() {

		this.trackNameOption = new TrackOption() {

			String value = "";

			@Override
			public String getName() {
				return "Track name (empty takes default value): ";
			}

			@Override
			public Class<?> getType() {
				return String.class;
			}

			@Override
			public Object getValue() {
				return value;
			}

			@Override
			public void setValue(Object value) {
				this.value = (String) value;
			}
		};

		this.trackColorOption = new TrackOption() {
			private Color value = null;

			@Override
			public String getName() {
				return "Color";
			}

			@Override
			public Class<?> getType() {

				return Color.class;
			}

			@Override
			public Object getValue() {
				return this.value;
			}

			@Override
			public void setValue(Object value) {
				this.value = (Color) value;
			}
		};
		
		this.backGroundColorOption = new TrackOption() {

			private Color value;

			@Override
			public String getName() {

				return "Background Color: ";
			}

			@Override
			public Class<?> getType() {

				return Color.class;
			}

			@Override
			public Object getValue() {
				return this.value;
			}

			@Override
			public void setValue(Object value) {
				this.value = (Color) value;
			}
		};
		
		this.maxDepthOption = new TrackOption() {
			
			private int value = 5;
			
			@Override
			public String getName() {
				
				return "Max depth: ";
			}
			
			@Override
			public Class<?> getType() {
				
				return Integer.class;
			}
			
			@Override
			public Object getValue() {
				return this.value;
			}
			
			@Override
			public void setValue(Object value) {
				this.value = (Integer) value;
			}
		};
	}

	@Override
	public void setTrackColor(Color trackColor) {
		this.trackColorOption.setValue(trackColor);
	}

	@Override
	public Color getTrackColor() {
		return (Color) this.trackColorOption.getValue();
	}

	private int getMaxDepth() {
		return (Integer) this.maxDepthOption.getValue();
	}
	
	@Override
	public void setOptions(Collection<TrackOption> options) {
		if (options != null) {
			fillOptions(options);
		}
	}

	private void fillOptions(Collection<TrackOption> options) {
		for (TrackOption option : options) {
			if (option.getName().equals("Color")) {
				if (option.getValue() != null
					&& !option.getValue().equals(getTrackColor()
				)
				) {
					this.trackColorOption = option;
				}
			} else if (option.getName().contains("Track name")){
				if (!((String)option.getValue()).equals("")) {
					this.trackNameOption = option;
				}
			} else if (option.getName().startsWith("Background")) {
				if (option.getValue() != null) { 
					this.backGroundColorOption = option;
				}
			}
		}
	}

	
	@Override
	public Color getBackgroundColor() {
		return (Color) this.backGroundColorOption.getValue();
	}
	
	@Override
	public String getTrackName() {
		String trackName = (String) this.trackNameOption.getValue();
		if (trackName.isEmpty()) {
			return file.getName();
		} else {
			return trackName;
		}
	}
}
