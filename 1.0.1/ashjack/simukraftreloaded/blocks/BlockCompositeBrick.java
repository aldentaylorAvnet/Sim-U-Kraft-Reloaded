package ashjack.simukraftreloaded.blocks;

import java.util.List;

import ashjack.simukraftreloaded.client.Gui.GuiBuildingConstructor;
import ashjack.simukraftreloaded.common.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCompositeBrick extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
	public BlockCompositeBrick(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	  @SideOnly(Side.CLIENT)
	  @Override
	    public void registerBlockIcons(IIconRegister iconRegister)
	    {
	        icons = new IIcon[1];
	        icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:compositebrick");
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public IIcon getIcon(int side, int meta)      // getBlockTextureFromSideAndMetadata
	    {
	        return icons[0];
	    }


}
