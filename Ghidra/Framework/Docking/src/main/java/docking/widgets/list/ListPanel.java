/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package docking.widgets.list;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionListener;

/**
 * This class provides a panel that contains a JList component.
 * @param <T> The type for the items in this list
 */
public class ListPanel<T> extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_WARNING = "You must first select an item from the list.";
	private ListSelectionListener listSelectionListener;
	private ActionListener doubleClickActionListener;
	private MouseListener mouseListener;
	private KeyListener keyListener;
	private JScrollPane scrollpane;
	private JList<T> list;

	/**
	 * Constructs a new ListPanel.
	 */
	public ListPanel() {
		super();

		setLayout(new BorderLayout());

		// Enforce a minimum size, for some amount of consistency.  The magic value was
		// discovered via trial-and-error.
		list = new JList<>() {
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.width = Math.max(d.width, 200);
				return d;
			}
		};

		scrollpane = new JScrollPane(list);

		// the next two lines of code cause the scroll bar not to work properly
		// list.setBorder(emptyborder);
		// scrollpane.setBorder(compoundborder);

		add(scrollpane, BorderLayout.CENTER);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (doubleClickActionListener != null) {
					if (e.getClickCount() == 2) {
						int index = list.locationToIndex(e.getPoint());
						if (index >= 0) {
							Object o = list.getModel().getElementAt(index);
							ActionEvent ev = new ActionEvent(this, e.getID(), o.toString());
							doubleClickActionListener.actionPerformed(ev);
							list.repaint();
						}
					}
				}
			}
		});
	}

	/**
	 * Sets the selection mode for the list.
	 * See JList for allowed Selection modes
	 * @param selectionMode the selectionMode to use.
	 */
	public void setSelectionMode(int selectionMode) {
		list.setSelectionMode(selectionMode);
	}

	/**
	 * Places a title just above the scrolling list.
	 * @param listTitle the title to use.
	 */
	public void setListTitle(String listTitle) {
		if (listTitle == null) {
			Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			setBorder(border);
		}
		else {
			TitledBorder tBorder = new TitledBorder(listTitle);
			Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			setBorder(new CompoundBorder(tBorder, emptyBorder));
		}
	}

	/**
	 * Adds a {@link ListSelectionListener}
	 * @param listener the listener to add
	 */
	public void addListSelectionListener(ListSelectionListener listener) {
		list.addListSelectionListener(listener);
	}

	/**
	 * Removes a {@link ListSelectionListener}
	 * @param listener the listener to remove
	 */
	public void removeListSelectionListener(ListSelectionListener listener) {
		list.removeListSelectionListener(listener);
	}

	/**
	 * Returns true if no list items are selected.
	 * @return true if no list items are selected.
	 */
	public boolean isSelectionEmpty() {
		return list.isSelectionEmpty();
	}

	/**
	 * Returns the first selected value in the list or null if nothing is selected.
	 * @return the first selected value in the list or null if nothing is selected.
	 */
	public T getSelectedValue() {
		return list.getSelectedValue();
	}

	/**
	 * Get the index of the selected item in the list.
	 * @return the index of the selected item in the list.
	 */
	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}

	/**
	 * Select the item at the given index.
	 * @param i the index at which to get the item.
	 */
	public void setSelectedIndex(int i) {
		list.setSelectedIndex(i);
	}

	/**
	 * Selects the item.
	 * @param item the item to select
	 */
	public void setSelectedValue(T item) {
		list.setSelectedValue(item, true);
	}

	/**
	 * Returns an array of all the selected items.
	 * @return an array of all the selected items.
	 */
	public java.util.List<T> getSelectedValues() {
		return list.getSelectedValuesList();
	}

	/** 
	 * replaces the list contents with the new list.
	 * @param dataList the new list for the contents.
	 */
	public void refreshList(T[] dataList) {
		list.setListData(dataList);
		list.clearSelection();
	}

	/**
	 * Sets the list data
	 * @param data the data
	 */
	public void setListData(T[] data) {
		list.setListData(data);
	}

	/**
	 * Sets a list model for the internal list to use.
	 * @param listModel the list model to use.
	 */
	public void setListModel(ListModel<T> listModel) {
		list.setModel(listModel);
		list.clearSelection();
	}

	/**
	 * Get the list model for the list.
	 * @return the list model for the list.
	 */
	public ListModel<T> getListModel() {
		return (list.getModel());
	}

	/**
	 * Return the JList component.
	 * @return the JList component.
	 */
	public JList<T> getList() {
		return list;
	}

	/**
	 * Get the cell renderer for the list.
	 * @param r the cell renderer to use.
	 */
	public void setCellRenderer(ListCellRenderer<T> r) {
		list.setCellRenderer(r);
	}

	/**
	 * Sets the listener to be notified when the selection changes.
	 * @param listener the Listener to be notified.  If listener can be null, which
	 * means no one is to be notified.
	 */
	public void setListSelectionListener(ListSelectionListener listener) {
		if (listSelectionListener != null) {
			list.removeListSelectionListener(listSelectionListener);
		}
		if (listener != null) {
			list.addListSelectionListener(listener);
		}
		listSelectionListener = listener;
	}

	/**
	 * Sets the listener to be notified whenever a list item is doubleClicked.
	 * @param listener the Listener to be notified.  If listener can be null, which
	 * means no one is to be notified.
	 */

	public final void setDoubleClickActionListener(ActionListener listener) {
		doubleClickActionListener = listener;

	}

	/**
	 * Set the mouse listener for the list.
	 * @param l the mouse listener to set.
	 */
	public void setMouseListener(MouseListener l) {
		if (mouseListener != null) {
			list.removeMouseListener(mouseListener);
		}
		list.addMouseListener(l);
		mouseListener = l;
	}

	public void setKeyListener(KeyListener l) {
		if (keyListener != null) {
			list.removeKeyListener(keyListener);
		}
		list.addKeyListener(l);
		keyListener = l;
	}

	/**
	 * Displays a standard warning message about no selected objects
	 * in the list.  
	 */
	public void issueWarning() {
		JOptionPane.showMessageDialog(null, DEFAULT_WARNING, "Warning",
			JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Displays any warning message.
	 * @param msg the warning message to display.
	 * @param title the title of the dialog to display.
	 */
	public void issueWarning(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Scroll viewport such that the index is visible.
	 * @param index the index of the item in the list to make visible.
	 */
	public void ensureIndexIsVisible(int index) {
		list.ensureIndexIsVisible(index);
	}

	/**
	 * Simple test for ListPanel class.
	 * @param args test args not used
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		}
		catch (Exception exc) {
			System.out.println("Error loading L&F: " + exc);
		}

		final JFrame frame = new JFrame("ListPanel");
		frame.getContentPane().setLayout(new GridLayout(1, 1));

		final ListPanel<String> lbp = new ListPanel<>();
		final DefaultListModel<String> listModel = new DefaultListModel<>();
		frame.getContentPane().add(lbp);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		listModel.addElement("Ellen");
		listModel.addElement("Bill");
		listModel.addElement("Mike");
		listModel.addElement("Dennis");
		lbp.setListModel(listModel);
		lbp.setListTitle("Developers");

		frame.pack();
		frame.setVisible(true);
	}

}
