/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.preferences;

import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.JFXPanelFromResource;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;

public final class PCGenPreferencesModel
{

	private PCGenPreferencesModel()
	{
	}

	/**
	 * A wrapper for a JFXPanel to pretend to be a PCGenPrefsPanel
	 * TODO: move this to a better place or figure out a way not to need this
	 */
	private static final class EmptyPrefPanel extends PCGenPrefsPanel
	{
		private final String title;

		public EmptyPrefPanel(String title, JFXPanel innerPanel)
		{
			this.title = title;
			this.add(innerPanel);
		}

		@Override
		public String getTitle()
		{
			return title;
		}

		@Override
		public void applyOptionValuesToControls()
		{
			// do nothing
		}

		@Override
		public void setOptionsBasedOnControls()
		{
			// do nothing
		}
	}

	static TreeItem<PCGenPrefsPanel> buildEmptyPanel(String title, String messageText)
	{
		final var panel =
				new JFXPanelFromResource<>(
						CenteredLabelPanelController.class,
						"CenteredLabelPanel.fxml"
				);
		panel.getController().setText(messageText);
		EmptyPrefPanel emptyPrefPanel = new EmptyPrefPanel(title, panel);
		return new TreeItem<>(emptyPrefPanel);
	}

}
