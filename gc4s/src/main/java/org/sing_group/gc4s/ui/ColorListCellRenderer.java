/*
 * #%L
 * GC4S components
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
package org.sing_group.gc4s.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * An implementation of {@code ListCellRenderer} to show colors.
 * 
 * @author hlfernandez
 *
 */
public class ColorListCellRenderer extends JButton
	implements ListCellRenderer<Color> {  
	private static final long serialVersionUID = 1L;

	boolean rendering = false;

	public ColorListCellRenderer() {
		setOpaque(true);
	}

	@Override
	public void setBackground(Color bg) {
		if (!rendering) {
			return;
		}

		super.setBackground(bg);
	}

	public Component getListCellRendererComponent(JList<? extends Color> list,
		Color value, int index, boolean isSelected, boolean cellHasFocus
	) {
		this.rendering = true;
		this.setText(" ");
		this.setBackground(value);
		this.rendering = false;
		return this;
	}
}