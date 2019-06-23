/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.coreview.CoreViewFrame;
import pcgen.gui2.solverview.SolverViewFrame;
import pcgen.gui2.tools.DesktopBrowserLauncher;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.PCGenAction;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.dialog.CalculatorDialogController;
import pcgen.gui3.dialog.DebugDialog;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The PCGenActionMap is the action map for the PCGenFrame, and as such
 * hold all of the actions that the PCGenFrame uses. The purpose of this
 * class is to hold all of the regarding actions for the menubar, toolbar,
 * and accessory popup menus that may use them. Since all of the action
 * handlers are Action objects they can be disabled or enabled to cause
 * all buttons that use the actions to update themselves accordingly.
 */
public final class PCGenActionMap extends ActionMap
{

	//the File menu commands
	private static final String FILE_COMMAND = "file";
	private static final String NEW_COMMAND = FILE_COMMAND + ".new";
	private static final String OPEN_COMMAND = FILE_COMMAND + ".open";
	private static final String OPEN_RECENT_COMMAND = FILE_COMMAND + ".openrecent";
	private static final String CLOSE_COMMAND = FILE_COMMAND + ".close";
	private static final String CLOSEALL_COMMAND = FILE_COMMAND + ".closeall";
	private static final String SAVE_COMMAND = FILE_COMMAND + ".save";
	private static final String SAVEAS_COMMAND = FILE_COMMAND + ".saveas";
	private static final String SAVEALL_COMMAND = FILE_COMMAND + ".saveall";
	private static final String REVERT_COMMAND = FILE_COMMAND + ".reverttosaved";
	private static final String PARTY_COMMAND = FILE_COMMAND + ".party";
	private static final String OPEN_PARTY_COMMAND = PARTY_COMMAND + ".open";
	private static final String OPEN_RECENT_PARTY_COMMAND = PARTY_COMMAND + ".openrecent";
	private static final String CLOSE_PARTY_COMMAND = PARTY_COMMAND + ".close";
	private static final String SAVE_PARTY_COMMAND = PARTY_COMMAND + ".save";
	private static final String SAVEAS_PARTY_COMMAND = PARTY_COMMAND + ".saveas";
	private static final String EXIT_COMMAND = FILE_COMMAND + ".exit";
	//the Edit menu commands
	private static final String EDIT_COMMAND = "edit";
	static final String ADD_KIT_COMMAND = EDIT_COMMAND + ".addkit";
	private static final String TEMP_BONUS_COMMAND = EDIT_COMMAND + ".tempbonus";
	private static final String EQUIPMENTSET_COMMAND = EDIT_COMMAND + ".equipmentset";
	//the Source menu commands
	private static final String SOURCES_COMMAND = "sources";
	private static final String SOURCES_RELOAD_COMMAND = SOURCES_COMMAND + ".reload";
	private static final String SOURCES_UNLOAD_COMMAND = SOURCES_COMMAND + ".unload";
	private static final String INSTALL_DATA_COMMAND = SOURCES_COMMAND + ".installData";
	//the tools menu commands
	private static final String TOOLS_COMMAND = "tools";
	static final String LOG_COMMAND = TOOLS_COMMAND + ".log";
	private static final String LOGGING_LEVEL_COMMAND = TOOLS_COMMAND + ".loggingLevel";
	private static final String CALCULATOR_COMMAND = TOOLS_COMMAND + ".calculator";
	private static final String COREVIEW_COMMAND = TOOLS_COMMAND + ".coreview";
	private static final String SOLVERVIEW_COMMAND = TOOLS_COMMAND + ".solverview";
	//the help menu commands
	private static final String HELP_COMMAND = "help";
	private static final String HELP_DOCS_COMMAND = HELP_COMMAND + ".docs";
	private static final String HELP_OGL_COMMAND = HELP_COMMAND + ".ogl";
	private static final String HELP_ABOUT_COMMAND = HELP_COMMAND + ".about";
	private final PCGenFrame frame;

	public static final String MNU_TOOLS = "mnuTools"; //$NON-NLS-1$
	public static final String MNU_TOOLS_PREFERENCES = "mnuToolsPreferences"; //$NON-NLS-1$
	public static final String MNU_EDIT = "mnuEdit"; //$NON-NLS-1$
	public static final String MNU_FILE = "mnuFile"; //$NON-NLS-1$

	/**
	 * The context indicating what items are currently loaded/being processed in the UI
	 */
	private final UIContext uiContext;

	PCGenActionMap(PCGenFrame frame, UIContext uiContext)
	{
		this.uiContext = Objects.requireNonNull(uiContext);
		this.frame = frame;
		initActions();
	}

	private void initActions()
	{
		put(FILE_COMMAND, new FileAction());

		put(OPEN_PARTY_COMMAND, new OpenPartyAction());
		put(OPEN_RECENT_PARTY_COMMAND, new OpenRecentAction());
		put(CLOSE_PARTY_COMMAND, new ClosePartyAction());
		put(SAVE_PARTY_COMMAND, new SavePartyAction());
		put(SAVEAS_PARTY_COMMAND, new SaveAsPartyAction());

		put(EXIT_COMMAND, new ExitAction());

		put(EDIT_COMMAND, new EditAction());
		put(EQUIPMENTSET_COMMAND, new EquipmentSetAction());
		put(TEMP_BONUS_COMMAND, new TempBonusAction());
		put(LOG_COMMAND, new DebugAction());
		put(LOGGING_LEVEL_COMMAND, new LoggingLevelAction());
		put(CALCULATOR_COMMAND, new CalculatorAction());
		put(COREVIEW_COMMAND, new CoreViewAction());
		put(SOLVERVIEW_COMMAND, new SolverViewAction());
		put(SOURCES_RELOAD_COMMAND, new ReloadSourcesAction());
		put(SOURCES_UNLOAD_COMMAND, new UnloadSourcesAction());

		put(HELP_COMMAND, new HelpAction());
		put(HELP_DOCS_COMMAND, new DocsHelpAction());
		put(HELP_OGL_COMMAND, new OGLHelpAction());
		put(HELP_ABOUT_COMMAND, new AboutHelpAction());
	}

	private final class EditAction extends PCGenAction
	{

		private EditAction()
		{
			super(MNU_EDIT);
		}

	}

	private final class EquipmentSetAction extends PCGenAction
	{

		private EquipmentSetAction()
		{
			super("mnuEditEquipmentSet");
		}

	}

	private final class TempBonusAction extends PCGenAction
	{

		private TempBonusAction()
		{
			super("mnuEditTempBonus");
		}

	}

	private static final class DebugAction extends PCGenAction
	{

		private DebugDialog dialog;

		private DebugAction()
		{
			super("mnuToolsLog", LOG_COMMAND, "F10");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (dialog == null)
			{
				dialog = new DebugDialog();
			}
		}

	}

	private static final class CalculatorAction extends PCGenAction
	{

		private JFXPanelFromResource<CalculatorDialogController> dialog;

		private CalculatorAction()
		{
			super("mnuToolsCalculator", CALCULATOR_COMMAND, "F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (dialog == null)
			{
				dialog = new JFXPanelFromResource<>(CalculatorDialogController.class, "CalculatorDialog.fxml");
			}
			dialog.showAsStage(LanguageBundle.getString("mnuToolsCalculator"));
		}
	}

	private final class CoreViewAction extends CharacterAction
	{

		private CoreViewAction()
		{
			super("mnuToolsCoreView", COREVIEW_COMMAND, "Shift-F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			CharacterFacade cf = frame.getSelectedCharacterRef().get();
			CoreViewFrame cvf = new CoreViewFrame(cf);
			cvf.setVisible(true);
		}

	}

	private final class SolverViewAction extends CharacterAction
	{

		private SolverViewAction()
		{
			super("mnuToolsSolverView", SOLVERVIEW_COMMAND, "Ctrl-F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SolverViewFrame svf = new SolverViewFrame();
			svf.setVisible(true);
		}

	}

	private final class LoggingLevelAction extends PCGenAction
	{

		private LoggingLevelAction()
		{
			super("mnuLoggingLevel");
		}

	}

	private final class FileAction extends PCGenAction
	{

		private FileAction()
		{
			super(MNU_FILE);
		}

	}

	private final class OpenAction extends PCGenAction
	{

		private OpenAction()
		{
			super("mnuFileOpen", OPEN_COMMAND, "shortcut O", Icons.Open16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOpenCharacterChooser();
		}

	}

	private final class OpenRecentAction extends PCGenAction
	{

		private OpenRecentAction()
		{
			super("mnuOpenRecent");
		}

	}

	private final class CloseAction extends CharacterAction
	{

		private CloseAction()
		{
			super("mnuFileClose", CLOSE_COMMAND, "shortcut W", Icons.Close16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeCharacter(frame.getSelectedCharacterRef().get());
		}

	}

	private final class CloseAllAction extends CharacterAction
	{

		private CloseAllAction()
		{
			super("mnuFileCloseAll", CLOSEALL_COMMAND, Icons.CloseAll16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeAllCharacters();
		}

	}

	private final class SaveAction extends PCGenAction implements ReferenceListener<CharacterFacade>
	{

		private final FileRefListener fileListener = new FileRefListener();

		private SaveAction()
		{
			super("mnuFileSave", SAVE_COMMAND, "shortcut S", Icons.Save16);
			ReferenceFacade<CharacterFacade> ref = frame.getSelectedCharacterRef();
			ref.addReferenceListener(this);
			checkEnabled(ref.get());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			final CharacterFacade pc = frame.getSelectedCharacterRef().get();
			if (pc == null)
			{
				return;
			}
			frame.saveCharacter(pc);
		}

		@Override
		public void referenceChanged(ReferenceEvent<CharacterFacade> e)
		{
			CharacterFacade oldRef = e.getOldReference();
			if (oldRef != null)
			{
				oldRef.getFileRef().removeReferenceListener(fileListener);
			}
			checkEnabled(e.getNewReference());
		}

		private void checkEnabled(CharacterFacade character)
		{
			if (character != null)
			{
				ReferenceFacade<File> file = character.getFileRef();
				file.addReferenceListener(fileListener);
				setEnabled(file.get() != null);
			}
			else
			{
				setEnabled(false);
			}
		}

		private final class FileRefListener implements ReferenceListener<File>
		{

			@Override
			public void referenceChanged(ReferenceEvent<File> e)
			{
				setEnabled(e.getNewReference() != null);
			}

		}

	}

	private final class SaveAsAction extends CharacterAction
	{

		private SaveAsAction()
		{
			super("mnuFileSaveAs", SAVEAS_COMMAND, "shift-shortcut S", Icons.SaveAs16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSaveCharacterChooser(frame.getSelectedCharacterRef().get());
		}

	}

	private final class SaveAllAction extends CharacterAction
	{

		private SaveAllAction()
		{
			super("mnuFileSaveAll", SAVEALL_COMMAND, Icons.SaveAll16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.saveAllCharacters();
		}

	}

	private final class PartyAction extends PCGenAction
	{

		private PartyAction()
		{
			super("mnuFileParty");
		}

	}

	private final class OpenPartyAction extends PCGenAction
	{

		private OpenPartyAction()
		{
			super("mnuFilePartyOpen", OPEN_PARTY_COMMAND, Icons.Open16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOpenPartyChooser();
		}

	}

	private final class ClosePartyAction extends PCGenAction
	{

		private ClosePartyAction()
		{
			super("mnuFilePartyClose", CLOSE_PARTY_COMMAND, Icons.Close16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeAllCharacters();
		}

	}

	private final class SavePartyAction extends CharacterAction
	{

		private SavePartyAction()
		{
			super("mnuFilePartySave", SAVE_PARTY_COMMAND, Icons.Save16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (frame.saveAllCharacters() && !CharacterManager.saveCurrentParty())
			{
				frame.showSavePartyChooser();
			}
		}

	}

	private final class SaveAsPartyAction extends CharacterAction
	{

		private SaveAsPartyAction()
		{
			super("mnuFilePartySaveAs", SAVEAS_PARTY_COMMAND, Icons.SaveAs16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSavePartyChooser();
		}

	}

	private final class ExitAction extends PCGenAction
	{

		private ExitAction()
		{
			super("mnuFileExit", EXIT_COMMAND, "shortcut Q");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.closePCGen();
		}

	}

	private final class ReloadSourcesAction extends PCGenAction implements ReferenceListener<SourceSelectionFacade>
	{

		private ReloadSourcesAction()
		{
			super("mnuSourcesReload", SOURCES_RELOAD_COMMAND, "shift-shortcut R");
			ReferenceFacade<SourceSelectionFacade> currentSourceSelectionRef =
					uiContext.getCurrentSourceSelectionRef();
			currentSourceSelectionRef.addReferenceListener(this);
			checkEnabled(currentSourceSelectionRef.get());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SourceSelectionFacade sources =
					uiContext.getCurrentSourceSelectionRef().get();
			if (sources != null)
			{
				frame.unloadSources();
				frame.loadSourceSelection(sources);
			}
		}

		@Override
		public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
		{
			checkEnabled(e.getNewReference());
		}

		private void checkEnabled(SourceSelectionFacade sources)
		{
			setEnabled(sources != null && !sources.getCampaigns().isEmpty());
		}

	}

	private final class UnloadSourcesAction extends PCGenAction implements ReferenceListener<SourceSelectionFacade>
	{

		private UnloadSourcesAction()
		{
			super("mnuSourcesUnload", SOURCES_UNLOAD_COMMAND, "shortcut U");
			ReferenceFacade<SourceSelectionFacade> currentSourceSelectionRef =
					uiContext.getCurrentSourceSelectionRef();
			currentSourceSelectionRef.addReferenceListener(this);
			checkEnabled(currentSourceSelectionRef.get());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.unloadSources();
		}

		@Override
		public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
		{
			checkEnabled(e.getNewReference());
		}

		private void checkEnabled(SourceSelectionFacade sources)
		{
			setEnabled(sources != null && !sources.getCampaigns().isEmpty());
		}

	}

	private final class HelpAction extends PCGenAction
	{

		private HelpAction()
		{
			super("mnuHelp", HELP_COMMAND);
		}

	}

	private final class DocsHelpAction extends PCGenAction
	{

		private DocsHelpAction()
		{
			super("mnuHelpDocumentation", HELP_DOCS_COMMAND, "F1", Icons.Help16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				DesktopBrowserLauncher.viewInBrowser(new File(ConfigurationSettings.getDocsDir(), "index.html"));
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not open docs in external browser", ex);
				JOptionPane.showMessageDialog(frame, LanguageBundle.getString("in_menuDocsNotOpenMsg"),
					LanguageBundle.getString("in_menuDocsNotOpenTitle"), JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private final class OGLHelpAction extends PCGenAction
	{

		private OGLHelpAction()
		{
			super("mnuHelpOGL", HELP_OGL_COMMAND);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOGLDialog();
		}

	}

	private final class AboutHelpAction extends PCGenAction
	{

		private AboutHelpAction()
		{
			super("mnuHelpAbout", HELP_ABOUT_COMMAND, Icons.About16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenFrame.showAboutDialog();
		}
	}


	private abstract class CharacterAction extends PCGenAction
	{

		private final ReferenceFacade<?> ref;

		private CharacterAction(String prop, String command, String accelerator)
		{
			this(prop, command, accelerator, null);
		}

		private CharacterAction(String prop, String command, Icons icon)
		{
			this(prop, command, null, icon);
		}

		private CharacterAction(String prop, String command, String accelerator, Icons icon)
		{
			super(prop, command, accelerator, icon);
			ref = frame.getSelectedCharacterRef();
			ref.addReferenceListener(new CharacterListener());
			setEnabled(ref.get() != null);
		}

		private final class CharacterListener implements ReferenceListener<Object>
		{

			@Override
			public void referenceChanged(ReferenceEvent<Object> e)
			{
				setEnabled(e.getNewReference() != null);
			}

		}

	}

}
