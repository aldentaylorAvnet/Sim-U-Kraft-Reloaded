package ashjack.simukraftreloaded.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLightBox extends Block
{
    private IIcon icons[];

    public BlockLightBox()
    {
        super(Material.wood);
        setLightLevel(1.0F);
        this.setCreativeTab(CreativeTabs.tabMisc);
        setStepSound(Block.soundTypeWood);
        setHardness(2F);
        setResistance(1.0F);
        setTickRandomly(true);
        setBlockName("SUKlight");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[8];
        icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightWhite");
        icons[1] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightRed");
        icons[2] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightOrange");
        icons[3] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightYellow");
        icons[4] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightGreen");
        icons[5] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightBlue");
        icons[6] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightPurple");
        icons[7] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLightRainbow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
    	return meta < 8 && meta >= 0 ? icons[meta] : icons[0];
    }

    public BlockLightBox idDropped(int par1, Random par2Random, int par3)
    {
        return this;
    }

    @Override
    public int damageDropped(int j)
    {
        return j;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Block blockId, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int meta = 0; meta < 8; ++meta)
        {
            par3List.add(new ItemStack(blockId, 1, meta));
        }
    }

    @Override
    public int getBlockColor()
    {
        return 0xffffff;
    }
}
