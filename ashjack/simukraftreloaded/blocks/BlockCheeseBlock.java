package ashjack.simukraftreloaded.blocks;

import ashjack.simukraftreloaded.core.ModSimukraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockCheeseBlock extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
	public BlockCheeseBlock() {
		super(Material.cactus);
		setBlockName("SUKcheeseBlock");
		setBlockTextureName(ModSimukraft.modid + ":" + "cheeseBlock");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	  @SideOnly(Side.CLIENT)
	  @Override
	    public void registerBlockIcons(IIconRegister iconRegister)
	    {
	        icons = new IIcon[1];
	        icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:cheeseblock");
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public IIcon getIcon(int side, int meta)      // getBlockTextureFromSideAndMetadata
	    {
	        return icons[0];
	    }
	    
	   
}
