/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 */
package pcgen.facade.core;

import pcgen.core.PCClass;
import pcgen.facade.util.ListFacade;

public interface SpellSupportFacade
{

	/**
	 * Refresh the available and known spells list in response to an action 
	 * such as levelling up. 
	 */
	public void refreshAvailableKnownSpells();

	// -------------------------- Interfaces ----------------------------------------

	public static interface SuperNode
	{
	}

	public static interface RootNode extends SuperNode
	{
		public String getName();
	}

	public static interface SpellNode extends SuperNode
	{

		public PCClass getSpellcastingClass();

		public String getSpellLevel();

		public SpellFacade getSpell();

		/**
		 * Returns the name of the root of this node's tree. The returned string may be null
		 * such as for available spells which use the spellcasting class as the root node instead.
		 * In the case of prepared spells this root node may be the spell list name and for spellbooks
		 * the root is the spell book name etc...
		 * @return the root node string
		 */
		public RootNode getRootNode();

		/**
		 * @return The number of occurrences of the spell that are held.
		 */
		public int getCount();

		/**
		 * Adjust the number of occurrences held of the spell.
		 * @param num The number of occurrences to add.
		 */
		public void addCount(int num);

	}

	/**
	 * @return the list of spell books
	 */
	public ListFacade<String> getSpellbooks();

}
