package com.arkcraft.module.item.common.items.weapons.component;


public class RangedCompLongneckRifleScoped extends RangedCompLongneckRifle
{
    private static int GuiID;

    public RangedCompLongneckRifleScoped(int ID)
    {
        super();
        setGuiID(ID);
    }

    @Override
    public boolean ifCanScope()
    {
        return true;
    }

    public static int getGuiID()
    {
        return GuiID;
    }

    public static void setGuiID(int guiID)
    {
        GuiID = guiID;
    }
}