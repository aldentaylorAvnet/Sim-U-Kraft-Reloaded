package ashjack.simukraftreloaded.folk.traits;

import net.minecraft.init.Blocks;


public class Traits 
{
	public static Trait traitReligious = new TraitReligious();
	public static Trait traitWorkaholic = new TraitWorkaholic();
	public static Trait traitBrave = new TraitBrave();
	public static Trait traitDwarvenHeritage = new TraitDwarvenHeritage();
	public static Trait traitFriendly = new TraitFriendly();
	public static Trait traitGreenThumb = new TraitGreenThumb();
	public static Trait traitHatesOutdoors = new TraitHatesOutdoors();
	public static Trait traitNightOwl = new TraitNightOwl();
	public static Trait traitStrong = new TraitStrong();

	public static Trait[] traitList;
	public Traits()
	{
		traitList = new Trait[8];
		traitList[0] = traitReligious;
		traitList[1] = traitWorkaholic;
		traitList[2] = traitBrave;
		traitList[3] = traitDwarvenHeritage;
		traitList[4] = traitFriendly;
		traitList[5] = traitGreenThumb;
		traitList[6] = traitHatesOutdoors;
		traitList[7] = traitNightOwl;
		traitList[8] = traitStrong;
			}
	
}
