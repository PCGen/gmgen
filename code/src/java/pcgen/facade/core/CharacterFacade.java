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
package pcgen.facade.core;

import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.meta.CorePerspective;
import pcgen.core.AbilityCategory;
import pcgen.core.Deity;
import pcgen.core.EquipmentModifier;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.VariableProcessor;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.io.ExportException;
import pcgen.io.ExportHandler;

/**
 * The CharacterFacade interface provides a key role in separation
 * of the core and the UI layers. The UI can only operate on this
 * interface the core provides the implementation. This class
 * is heavily event driven, ie, any changes that occur to the
 * model will result to an event being fired that a listener
 * can pick up on. To operate like this, all values returned
 * from this class, with a couple of exceptions, are models that can
 * be listenered to. Two of the most commonly used models are
 * the {@code ReferenceFacade} and the {@code ListFacade}
 *
 * Note: This facade returns references to items of interest.
 * These allow not only the values to be retrieved but also
 * interested parties to register as listeners for changes to the valiues.
 * @see pcgen.facade.util.ListFacade
 * @see ReferenceFacade
 */
public interface CharacterFacade extends CompanionFacade
{

	public InfoFactory getInfoFactory();

	public ReferenceFacade<Gender> getGenderRef();

	public void setAlignment(PCAlignment alignment);

	public ReferenceFacade<PCAlignment> getAlignmentRef();

	public void setGender(Gender gender);

	public void setGender(String gender);

	/**
	 * @param stat The stat to retrieve the base for
	 * @return A reference to the base score for the stat
	 */
	public ReferenceFacade<Number> getScoreBaseRef(PCStat stat);

	public void addAbility(AbilityCategory category, AbilityFacade ability);

	public void removeAbility(AbilityCategory category, AbilityFacade ability);

	/**
	 * Note: This method should never return null. If the character does not possess
	 * any abilities in the parameter category, this method should create a new
	 * DefaultGenericListModel for that category and keep a reference to it for future use.
	 * @param category
	 * @return a List of Abilities the character posseses in the specified category
	 */
	public ListFacade<AbilityFacade> getAbilities(AbilityCategory category);

	/**
	 * This returns the number of times that a given class has been taken by this character.
	 * @param c a ClassFacade
	 * @return the total level of a class
	 */
	public int getClassLevel(PCClass c);

	public int getRemainingSelections(AbilityCategory category);

	/**
	 * Set the cash held by the character.
	 * @param newVal The new amount for the character's funds.
	 */
	public void setFunds(BigDecimal newVal);

	/**
	 * @param allowed Is the character allowed to spend more funds than they have.
	 */
	public void setAllowDebt(boolean allowed);

	public boolean isAutomatic(Language language);

	public void addTemplate(PCTemplate template);

	public void removeTemplate(PCTemplate template);

	public ListFacade<PCTemplate> getTemplates();

	/**
	 * adds a temp bonus to the character
	 * @param bonus the bonus to add
	 */
	public void addTempBonus(TempBonusFacade bonus);

	/**
	 * removes a bonus from the character
	 * @param bonus the bonus to remove
	 */
	public void removeTempBonus(TempBonusFacade bonus);

	/**
	 * This returns a DataSetFacade that contains all
	 * of the sources that this character was loaded with.
	 * The returned DataSetFacade can be used to browse all
	 * of the other facades available for this character.
	 * @return the DataSetFacade for this character
	 */
	public DataSetFacade getDataSet();

	/**
	 * @return a reference to this character's Race
	 */
	@Override
	public ReferenceFacade<Race> getRaceRef();

	/**
	 * Sets this character's race
	 * @param race
	 */
	public void setRace(Race race);

	/**
	 * @return a reference to this character's tab name
	 */
	public ReferenceFacade<String> getTabNameRef();

	/**
	 * @return a reference to this character's name
	 */
	@Override
	public ReferenceFacade<String> getNameRef();

	/**
	 * Sets this character's name
	 * @param name the name of the character
	 */
	public void setName(String name);

	/**
	 * @return a reference to this character's player's name
	 */
	public ReferenceFacade<String> getPlayersNameRef();

	/**
	 * @param name The name of the player
	 */
	public void setPlayersName(String name);

	/**
	 * @return a reference to this character's handedness string
	 */
	public ReferenceFacade<Handed> getHandedRef();

	/**
	 * @param handedness The new handedness string for the character
	 */
	public void setHanded(Handed handedness);

	/**
	 * @see CharacterFacade#setFile(File)
	 * @return a reference to the character's file
	 */
	@Override
	public ReferenceFacade<File> getFileRef();

	/**
	 * Sets the file that this character will be saved to.
	 * @see CharacterFacade#getFileRef()
	 * @param file the File to associate with this character
	 */
	public void setFile(File file);

	public ReferenceFacade<Deity> getDeityRef();

	public void setDeity(Deity deity);

	/**
	 * @return The domains that the character knows
	 */
	public ListFacade<DomainFacade> getDomains();

	/**
	 * Add a domain to the list of those the character knows.
	 * @param domain The domain to add.
	 */
	public void addDomain(DomainFacade domain);

	/**
	 * Remove a domain from the list of those the character knows.
	 * @param domain The domain to remove.
	 */
	public void removeDomain(DomainFacade domain);

	public ReferenceFacade<Integer> getRemainingDomainSelectionsRef();

	public ListFacade<Handed> getAvailableHands();

	public ListFacade<Gender> getAvailableGenders();

	/**
	 * @return The domains which the character has access to.
	 */
	public ListFacade<DomainFacade> getAvailableDomains();

	public ListFacade<Language> getLanguages();

	/**
	 * Write the character details, as defined by the export handler to the writer.
	 * 
	 * @param theHandler The ExportHandler that defines how the output will be formatted.
	 * @param buf The writer the character details are to be output to.
	 * @throws ExportException If the export fails.
	 */
	public void export(ExportHandler theHandler, BufferedWriter buf) throws ExportException;

	/**
	 * gets the UIDelegate that this character uses to display messages
	 * and choosers
	 * @return the UIDelegate that this character uses
	 */
	public UIDelegate getUIDelegate();

	/**
	 * @return The facade for character levels for this character.
	 */
	public CharacterLevelsFacade getCharacterLevelsFacade();

	/**
	 * @return The facade for description for this character.
	 */
	public DescriptionFacade getDescriptionFacade();

	/**
	 * Set the character's current experience point value
	 * @param xp The new XP value to be set
	 */
	public void setXP(final int xp);

	/**
	 * Set the character's character type.
	 * *
	 * @param characterType The character type to be set
	 */
	public void setCharacterType(String characterType);

	/**
	 * Set the character's age in years.
	 * @param age The new age to be set.
	 */
	public void setAge(final int age);

	/**
	 * @return A reference to the age of the character
	 */
	public ReferenceFacade<Integer> getAgeRef();

	/**
	 * @return A list of the defined age categories.  
	 */
	public ListFacade<String> getAgeCategories();

	/**
	 * Set the character's age category. Will also reset their age if the age category 
	 * has changed.
	 * @param ageCat The new age category to be set
	 */
	public void setAgeCategory(final String ageCat);

	/**
	 * @return A reference to the age category of the character.
	 */
	public ReferenceFacade<String> getAgeCategoryRef();

	/**
	 * Check if the character meets all requirements to be of the object.
	 * @param infoFacade The object to be checked.
	 * @return True if the character qualifies for the object, false if not.
	 */
	public boolean isQualifiedFor(InfoFacade infoFacade);

	/**
	 * Check if the character meets all requirements to take the domain.
	 * @param domain The domain to be checked.
	 * @return True if the character can take the domain, false if not.
	 */
	public boolean isQualifiedFor(DomainFacade domain);

	/**
	 * Check if the character meets all requirements to take the deity.
	 * @param deity The deity to be checked.
	 * @return True if the character can take the deity, false if not.
	 */
	public boolean isQualifiedFor(Deity deity);

	/**
	 * Is the modifier able to be added to the item of equipment?
	 * @param equipFacade The equipment item being modified.
	 * @param eqMod The equipment modifier to be checked.
	 * @return True if it can be added, false if not.
	 */
	public boolean isQualifiedFor(EquipmentFacade equipFacade, EquipmentModifier eqMod);

	//
	//	/**
	//	 * @param category the category of the ability
	//	 * @param ability the ability that has choices
	//	 * @return a String which represents the choices made for this ability
	//	 */
	//	public String getAbilityChoiceDisplayString(AbilityCategoryFacade category, AbilityFacade ability);

	public SpellSupportFacade getSpellSupport();

	/**
	 * Sets the file containing the portrait image
	 * @param file a File containing the portrait image
	 */
	public void setPortrait(File file);

	/**
	 * Retrieve the current export state of the BiographyField.
	 * 
	 * @param field The BiographyField to be examined
	 * @return true if the field should be exported, false if it should be suppressed from export.
	 */
	public boolean getExportBioField(BiographyField field);

	/**
	 * Set the export state of the BiographyField.
	 * 
	 * @param field The BiographyField 
	 * @param export if the field should be exported, false if it should be suppressed from export.
	 */
	public void setExportBioField(BiographyField field, boolean export);

	/**
	 * @return a reference to this character's skin color.
	 */
	public ReferenceFacade<String> getSkinColorRef();

	/**
	 * @param color the skin color to set.
	 */
	public void setSkinColor(String color);

	/**
	 * @return a reference to this character's hair color.
	 */
	public ReferenceFacade<String> getHairColorRef();

	/**
	 * @param color the hair color to set.
	 */
	public void setHairColor(String color);

	/**
	 * @return a reference to this character's eye color.
	 */
	public ReferenceFacade<String> getEyeColorRef();

	/**
	 * @param color the eye color to set.
	 */
	public void setEyeColor(String color);

	/**
	 * @return a reference to this character's height.
	 */
	public ReferenceFacade<Integer> getHeightRef();

	/**
	 * @param height the height to set.
	 */
	public void setHeight(int height);

	/**
	 * @return a reference to this character's weight.
	 */
	public ReferenceFacade<Integer> getWeightRef();

	/**
	 * @param weight the weight to set.
	 */
	public void setWeight(int weight);

	/**
	 * @return true if the character has been changed and needs to be saved.
	 */
	public boolean isDirty();

	/**
	 * @return The kits that have been applied to the character 
	 */
	public DefaultListFacade<Kit> getKits();

	/**
	 * Add a kit to the character. This will test the kit is valid and warn the 
	 * user if there are potential errors before applying the kit. 
	 * @param object The kit to be added
	 */
	public void addKit(Kit object);

	/**
	 * Record the default output sheet for this character.
	 * @param pdf Is this the PDF sheet?
	 * @param outputSheet The new default.
	 */
	public void setDefaultOutputSheet(boolean pdf, File outputSheet);

	/**
	 * Return the default output sheet for this character.
	 * @param pdf Is this the PDF sheet?
	 * @return The default output sheet.
	 */
	public String getDefaultOutputSheet(boolean pdf);

	public CompanionSupportFacade getCompanionSupport();

	/**
	 * @return a character stub representing this character's master
	 */
	public CharacterStubFacade getMaster();

	/**
	 * @return the type of companion the current character is, or null if not a companion
	 */
	@Override
	public String getCompanionType();

	/**
	 * @return the variable processor for the current character
	 */
	public VariableProcessor getVariableProcessor();

	/**
	 * @return calculate a variable for the current character
	 */
	public Float getVariable(final String variableString, final boolean isMax);

	/**
	 * Advise the character facade that it is being closed.
	 */
	public void closeCharacter();

	/**
	 * Identify if this character facade is a facade for the supplied character.
	 * @param pc The character to check for.
	 * @return True if this is a facade for the supplied character, false otherwise.
	 */
	public boolean matchesCharacter(PlayerCharacter pc);

	public List<CoreViewNodeFacade> getCoreViewTree(CorePerspective pers);

	CharID getCharID();

	/**
	 * Return true if the feature with the given name is enabled for this PC; false
	 * otherwise.
	 */
	public boolean isFeatureEnabled(String feature);
}
