package ashjack.simukraftreloaded.folk.traits;

import ashjack.simukraftreloaded.core.Unused;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;

public class Trait 
{
	public String traitName;
	public String traitDesc;
	public FolkData theFolk;
	
	public Trait()
	{
		
	}
	
	/**
	 * Sets the name of the trait
	 */
	public void setTraitName(String name)
	{
		name = traitName;
	}
	
	/**
	 * Sets the description of the trait
	 */
	public void setTraitDescription(String description)
	{
		description = traitDesc;
	}
	
	/**
	 * Sets the icon of the trait
	 */
	@Unused
	public void setTraitIcon()
	{
		
	}

	/**
     * Tells the folk with this trait where their 'special' building is<br>
     * (if they have one)
     */
	public void hasSpecialBuilding(String buildingName, String visitingText)
	{
		Building specialBuilding = Building.getBuildingBySearch(buildingName);
		
		if(specialBuilding != null)
		{
			theFolk.gotoXYZ(specialBuilding.primaryXYZ, GotoMethod.WALK);
			theFolk.destination.doNotTimeout = true;
			theFolk.statusText = visitingText;
		}
	}
	
	/**
     * Tells the folk with this trait where their 'special' building is<br>
     * (if they have one)
     */
	public void hasSpecialBuilding(String buildingName)
	{
		Building specialBuilding = Building.getBuildingBySearch(buildingName);
		
		theFolk.gotoXYZ(specialBuilding.primaryXYZ, GotoMethod.WALK);
		theFolk.destination.doNotTimeout = true;
		theFolk.statusText = "Visiting the " + specialBuilding.displayName;
	}
	
}
