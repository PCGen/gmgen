/*
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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.io.ExportException;
import pcgen.io.ExportHandler;
import pcgen.io.ExportUtilities;
import pcgen.util.Logging;
import pcgen.util.fop.FopTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code BatchExporter} manages character sheet output to a
 * file. It is capable of outputting either a single character or a party 
 * to an output file based on a suitable export template.
 * <p>
 * BatchExporter is intended to be used for both batch export of characters 
 * and as a library for other code to easily provide output capability. When
 * used in batch mode an instance should be created for the template and 
 * one of the export methods called. When used as a library the static methods
 * should be used and supplied with preloaded characters.  
 *
 * 
 */
public final class BatchExporter
{

	private BatchExporter()
	{
	}

	/**
	 * Write a PDF character sheet for the character to the output file. The 
	 * character sheet will be built according to the template file. If the 
	 * output file exists it will be overwritten.
	 *    
	 * @param character The already loaded character to be output.
	 * @param outFile The file to which the character sheet is to be written. 
	 * @param templateFile The file that has the export template definition.  
	 * @return true if the export was successful, false if it failed in some way.
	 */
	public static boolean exportCharacterToPDF(CharacterFacade character, File outFile, File templateFile)
	{

		String templateExtension = FilenameUtils.getExtension(templateFile.getName());

		boolean isTransformTemplate =
				"xslt".equalsIgnoreCase(templateExtension) || "xsl".equalsIgnoreCase(templateExtension);

		boolean useTempFile =
				PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF, false);
		String outFileName = FilenameUtils.removeExtension(outFile.getAbsolutePath());
		File tempFile = new File(outFileName + (isTransformTemplate ? ".xml" : ".fo"));
		try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(outFile));
		     ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		     OutputStream exportOutput = useTempFile
					//Output to both the byte stream and to the temp file.
					? new TeeOutputStream(byteOutputStream, new FileOutputStream(tempFile)) : byteOutputStream)
		{
			FopTask task;
			if (isTransformTemplate)
			{
				exportCharacter(character, exportOutput);
				InputStream inputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
				task = FopTask.newFopTask(inputStream, templateFile, fileStream);
			}
			else
			{
				exportCharacter(character, templateFile, exportOutput);
				InputStream inputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
				task = FopTask.newFopTask(inputStream, null, fileStream);
			}
			character.setDefaultOutputSheet(true, templateFile);
			task.run();
			if (StringUtils.isNotBlank(task.getErrorMessages()))
			{
				Logging.errorPrint("BatchExporter.exportCharacterToPDF failed: " //$NON-NLS-1$
					+ task.getErrorMessages());
				return false;
			}
		}
		catch (final IOException | ExportException e)
		{
			Logging.errorPrint("BatchExporter.exportCharacterToPDF failed", e); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 * Get a temporary file name for outputting a character using a particular
	 * output template.
	 * @param templateFile The output template that will be used.
	 * @return The temporary file, or null if it could not be created.
	 */
	public static File getTempOutputFilename(File templateFile)
	{
		String extension =
				ExportUtilities.getOutputExtension(templateFile.getName(), ExportUtilities.isPdfTemplate(templateFile));

		try
		{
			// create a temporary file to view the character output
			return File.createTempFile(Constants.TEMPORARY_FILE_NAME, '.' + extension, SettingsHandler.getTempPath());
		}
		catch (final IOException ioe)
		{
			ShowMessageDelegate.showMessageDialog("Could not create temporary preview file.", "PCGen",
				MessageType.ERROR);
			Logging.errorPrint("Could not create temporary preview file.", ioe);
			return null;
		}

	}

	/**
	 * Remove any temporary xml files produced while outputting characters. 
	 */
	static void removeTemporaryFiles()
	{
		final boolean cleanUp = UIPropertyContext.getInstance().initBoolean(UIPropertyContext.CLEANUP_TEMP_FILES, true);

		if (!cleanUp)
		{
			return;
		}

		final String aDirectory = SettingsHandler.getTempPath() + File.separator;
		new File(aDirectory).list((aFile, aString) -> {
			try
			{
				if (aString.startsWith(Constants.TEMPORARY_FILE_NAME))
				{
					final File tf = new File(aFile, aString);
					tf.delete();
				}
			}
			catch (final Exception e)
			{
				Logging.errorPrint("removeTemporaryFiles", e);
			}

			return false;
		});
	}

	/**
	 * Exports a character to an OuputStream using the default template for the character's game
	 * mode. This is more generic
	 * method than writing to a file and the same effect can be achieved by passing in a
	 * FileOutputStream.
	 *
	 * @param character the loaded CharacterFacade to export
	 * @param outputStream the OutputStream that the character will be exported to
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ExportException if there is an export exception
	 */
	private static void exportCharacter(CharacterFacade character, OutputStream outputStream)
		throws IOException, ExportException
	{
		exportCharacter(character, getXMLTemplate(character), outputStream);
	}

	/**
	 * Exports a character to an OutputStream using the given template file. The template file is
	 * used to determine the type of character sheet that will be generated. This is more generic
	 * method than writing to a file and the same effect can be achieved by passing in a
	 * FileOutputStream.
	 *
	 * @param character the loaded CharacterFacade to export
	 * @param templateFile the export template file for the ExportHandler to use
	 * @param outputStream the OutputStream that the character will be exported to
	 * @throws IOException
	 * @throws ExportException
	 */
	private static void exportCharacter(CharacterFacade character, File templateFile, OutputStream outputStream)
		throws IOException, ExportException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")))
		{
			character.export(new ExportHandler(templateFile), bw);
		}
	}

	private static File getXMLTemplate(CharacterFacade character)
	{
		Path path = Path.of(ConfigurationSettings.getSystemsDir(), "gameModes",
				character.getDataSet().getGameMode().getName(), "base.xml.ftl");
		File template = new File(path.toUri());
		if (!template.exists())
		{
			template = new File(ConfigurationSettings.getOutputSheetsDir(), "base.xml.ftl");
		}
		return template;
	}

}
