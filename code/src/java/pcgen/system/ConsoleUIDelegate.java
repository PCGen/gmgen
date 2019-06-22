/**
 * Copyright James Dempsey, 2012
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
 *
 */
package pcgen.system;

import java.util.Optional;

import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.util.Logging;

/**
 * The Class {@code ConsoleUIDelegate} displays messages to the console
 * and returns default choices. It is used when PCGen is running in batch mode 
 * without any windows shown.
 *
 * 
 */
public class ConsoleUIDelegate implements UIDelegate
{

	@Override
	public Boolean maybeShowWarningConfirm(String title, String message, String checkBoxText, PropertyContext context,
		String contextProp)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
		return false;
	}

	@Override
	public void showErrorMessage(String title, String message)
	{
		Logging.log(Logging.ERROR, title + " - " + message);
	}

	@Override
	public void showInfoMessage(String title, String message)
	{
		Logging.log(Logging.INFO, title + " - " + message);
	}

	@Override
	public boolean showWarningConfirm(String title, String message)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
		return true;
	}

	@Override
	public void showWarningMessage(String title, String message)
	{
		Logging.log(Logging.WARNING, title + " - " + message);
	}

	@Override
	public boolean showGeneralChooser(ChooserFacade chooserFacade)
	{
		return false;
	}

	@Override
	public Optional<String> showInputDialog(String title, String message, String initialValue)
	{
		return Optional.empty();
	}

}
