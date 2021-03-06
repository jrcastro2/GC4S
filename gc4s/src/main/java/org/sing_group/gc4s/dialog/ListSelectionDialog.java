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
package org.sing_group.gc4s.dialog;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.InvalidClassException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.sing_group.gc4s.input.list.ExtendedDefaultListModel;
import org.sing_group.gc4s.input.list.JParallelListsPanel;

/**
 * A {@code ListSelectionDialog} displays two parallel {@code JListPanel}s,
 * allowing to move elements between them.
 * 
 * @author hlfernandez
 *
 * @param <E> the type of the elements in the lists.
 */
public class ListSelectionDialog<E> extends AbstractInputJDialog {
	private static final long serialVersionUID = 1L;
	private static final String SELECTED_ITEMS = "Selected items";
	private static final String UNSELECTED_ITEMS = "Unselected items";

	private JPanel inputComponents;

	private List<E> selected;
	private String selectedItemsTitle;
	private ExtendedDefaultListModel<E> selectedListModel;
	private List<E> unselected;
	private String unselectedItemsTitle;
	private ExtendedDefaultListModel<E> unselectedListModel;

	/**
	 * Creates a new {@code ListSelectionDialog} with {@code selected} and
	 * {@code unselected} lists.
	 * 
	 * @param parent the parent {@code Window}.
	 * @param selected a {@code JList} with the initial selected elements.
	 * @param unselected a {@code JList} with the initial unselected elements.
	 * @throws InvalidClassException if list's model is not an 
	 * 	{@code ExtendedDefaultModel}.
	 */
	public ListSelectionDialog(Window parent, List<E> selected,
		List<E> unselected
	) throws InvalidClassException {
		this(parent, selected, unselected, SELECTED_ITEMS, UNSELECTED_ITEMS);
	}
	
	/**
	 * Creates a new {@code ListSelectionDialog} with {@code selected} and
	 * {@code unselected} lists.
	 * 
	 * @param parent the parent {@code Window}.
	 * @param selected a {@code JList} with the initial selected elements.
	 * @param unselected a {@code JList} with the initial unselected elements.
	 * @param selectedItemsTitle the title of the selected elements list.
	 * @param unselectedItemsTitle the title of the unselected elements list.
	 * @throws InvalidClassException if list's model is not an 
	 * 	{@code ExtendedDefaultModel}.
	 */
	public ListSelectionDialog(Window parent, List<E> selected,
		List<E> unselected, String selectedItemsTitle, String unselectedItemsTitle
	) throws InvalidClassException {
		super(parent);
		
		this.selected = selected;
		this.unselected = unselected;
		this.selectedItemsTitle = selectedItemsTitle;
		this.unselectedItemsTitle = unselectedItemsTitle;
		
		this.init();
	}
	
	private void init() throws InvalidClassException {
		unselectedListModel = new ExtendedDefaultListModel<E>();
		unselectedListModel.addElements(unselected);
		JList<E> unselectedList = new JList<>(unselectedListModel);
		unselectedList.setFixedCellWidth(200);

		selectedListModel = new ExtendedDefaultListModel<E>();
		selectedListModel.addElements(selected);
		JList<E> selectedList = new JList<>(selectedListModel);
		selectedList.setFixedCellWidth(200);

		JParallelListsPanel<E> parallelLists = new JParallelListsPanel<>(
			unselectedList, selectedList, unselectedItemsTitle, 
			selectedItemsTitle, false, false
		);
		
		this.inputComponents.setBorder(createEmptyBorder(5, 5, 5, 5));
		this.inputComponents.add(parallelLists, BorderLayout.CENTER);
	}

	@Override
	protected String getDialogTitle() {
		return "Item selection";
	}

	@Override
	protected String getDescription() {
		return "This dialog allows you to select items";
	}

	@Override
	protected JPanel getInputComponentsPane() {
		this.inputComponents = new JPanel(new BorderLayout());
		return this.inputComponents;
	}

	@Override
	public void setVisible(boolean b) {
		this.okButton.setEnabled(true);
		this.pack();
		super.setVisible(b);
	}

	/**
	 * Returns the selected elements.
	 * @return a {@code List} containing the selected elements.
	 */
	public List<E> getSelectedItems() {
		return extractElements(this.selectedListModel);
	}

	/**
	 * Returns the unselected elements.
	 * @return a {@code List} containing the unselected elements.
	 */
	public List<E> getUnselectedItems() {
		return extractElements(this.unselectedListModel);
	}

	private List<E> extractElements(DefaultListModel<E> listModel) {
		Enumeration<E> iterator = listModel.elements();
		List<E> toret = new LinkedList<>();
		while(iterator.hasMoreElements()) {
			toret.add(iterator.nextElement());
		}
		return toret;
	}
}
