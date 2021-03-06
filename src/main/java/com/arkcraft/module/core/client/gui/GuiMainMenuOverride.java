package com.arkcraft.module.core.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import com.arkcraft.module.core.ARKCraft;

@SideOnly(Side.CLIENT)
public class GuiMainMenuOverride extends GuiScreen implements GuiYesNoCallback
{
	private static final AtomicInteger field_175373_f = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final Random rand = new Random();
	/**
	 * Counts the number of screen updates.
	 */
	private float updateCounter;
	/**
	 * The splash message.
	 */
	private String splashText;
	private GuiARKButton buttonResetDemo;
	/**
	 * Timer used to rotate the panorama, increases every tick.
	 */
	private int panoramaTimer;
	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private DynamicTexture viewportTexture;
	private boolean field_175375_v = true;
	/**
	 * The Object object utilized as a thread lock when performing non
	 * thread-safe operations
	 */
	private final Object threadLock = new Object();
	/**
	 * OpenGL graphics card warning.
	 */
	private String openGLWarning1;
	/**
	 * OpenGL graphics card warning.
	 */
	private String openGLWarning2;
	private String field_104024_v;
	private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation arkcraftTitle = new ResourceLocation(ARKCraft.MODID,
			"textures/gui/arkcraft_logo.png");
	/**
	 * An array of all the paths to the panorama pictures.
	 */
	private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] { new ResourceLocation(
			ARKCraft.MODID, "textures/gui/panorama_0.png"), new ResourceLocation(ARKCraft.MODID,
			"textures/gui/panorama_1.png"), new ResourceLocation(ARKCraft.MODID,
			"textures/gui/panorama_2.png"), new ResourceLocation(ARKCraft.MODID,
			"textures/gui/panorama_3.png"), new ResourceLocation(ARKCraft.MODID,
			"textures/gui/panorama_4.png"), new ResourceLocation(ARKCraft.MODID,
			"textures/gui/panorama_5.png") };
	public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;
	private ResourceLocation field_110351_G;
	/**
	 * Minecraft Realms button.
	 */
	private GuiARKButton realmsButton;
	private List<String> splashes = new ArrayList<String>();

	public GuiMainMenuOverride()
	{
		this.openGLWarning2 = field_96138_a;
		this.splashText = "missingno";
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(
					new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
							.getResource(splashTexts).getInputStream(), Charsets.UTF_8));
			String s;

			while ((s = reader.readLine()) != null)
			{
				s = s.trim();

				if (!s.isEmpty())
				{
					splashes.add(s);
				}
			}

			if (!splashes.isEmpty())
			{
				do
				{
					this.splashText = (String) splashes.get(rand.nextInt(splashes.size()));
				}
				while (this.splashText.hashCode() == 125780783);
			}
		}
		catch (IOException exction)
		{
			;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException ioexception)
				{
					;
				}
			}
		}

		this.updateCounter = rand.nextFloat();
		this.openGLWarning1 = "";

		if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported())
		{
			this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
			this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
			this.field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen()
	{
		++this.panoramaTimer;
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	/**
	 * Fired when a key is typed (except F11 who toggle full screen). This is
	 * the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character
	 * (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		this.viewportTexture = new DynamicTexture(256, 256);
		this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background",
				this.viewportTexture);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9)
		{
			this.splashText = "Happy birthday, ez!";
		}
		else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1)
		{
			this.splashText = "Happy birthday, Notch!";
		}
		else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
		{
			this.splashText = "Merry X-mas!";
		}
		else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
		{
			this.splashText = "Happy new year!";
		}
		else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
		{
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		boolean flag = true;
		int i = this.height / 4 + 48;

		if (this.mc.isDemo())
		{
			this.addDemoButtons(i, 24);
		}
		else
		{
			this.addSingleplayerMultiplayerButtons(i, 24);
		}

		this.buttonList.add(new GuiARKButton(0, this.width / 2 - 100, i + 72 + 12, 98, 20, I18n
				.format("menu.options", new Object[0])));
		this.buttonList.add(new GuiARKButton(4, this.width / 2 + 2, i + 72 + 12, 98, 20, I18n
				.format("menu.quit", new Object[0])));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, i + 72 + 12));
		Object object = this.threadLock;

		synchronized (this.threadLock)
		{
			this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
			this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
			int j = Math.max(this.field_92023_s, this.field_92024_r);
			this.field_92022_t = (this.width - j) / 2;
			this.field_92021_u = ((GuiARKButton) this.buttonList.get(0)).yPosition - 24;
			this.field_92020_v = this.field_92022_t + j;
			this.field_92019_w = this.field_92021_u + 24;
		}
	}

	/**
	 * Adds Singleplayer and Multiplayer buttons on Main Menu for players who
	 * have bought the game.
	 */
	private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_)
	{
		this.buttonList.add(new GuiARKButton(1, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1,
				I18n.format("Host / Local", new Object[0]))); // TODO lang
		this.buttonList.add(new GuiARKButton(2, this.width / 2 - 100, p_73969_1_, I18n.format(
				"Join ARK", new Object[0])));
		this.buttonList.add(this.realmsButton = new GuiARKButton(14, this.width / 2 - 100,
				p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online", new Object[0])));
		GuiARKButton fmlModButton = new GuiARKButton(6, this.width / 2 - 100,
				p_73969_1_ + p_73969_2_ * 2, I18n.format("fml.menu.mods"));
		fmlModButton.xPosition = this.width / 2 + 2;
		realmsButton.width = 98;
		fmlModButton.width = 98;
		this.buttonList.add(fmlModButton);
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */
	private void addDemoButtons(int p_73972_1_, int p_73972_2_)
	{
		this.buttonList.add(new GuiARKButton(11, this.width / 2 - 100, p_73972_1_, I18n.format(
				"menu.playdemo", new Object[0])));
		this.buttonList.add(this.buttonResetDemo = new GuiARKButton(12, this.width / 2 - 100,
				p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0])));
		ISaveFormat isaveformat = this.mc.getSaveLoader();
		WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

		if (worldinfo == null)
		{
			this.buttonResetDemo.enabled = false;
		}
	}

	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.id == 0)
		{
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5)
		{
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc
					.getLanguageManager()));
		}

		if (button.id == 1)
		{
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (button.id == 2)
		{
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 14 && this.realmsButton.visible)
		{
			this.switchToRealms();
		}

		if (button.id == 4)
		{
			this.mc.shutdown();
		}

		if (button.id == 6)
		{
			this.mc.displayGuiScreen(new net.minecraftforge.fml.client.GuiModList(this));
		}

		if (button.id == 11)
		{
			this.mc.launchIntegratedServer("Demo_World", "Demo_World",
					DemoWorldServer.demoWorldSettings);
		}

		if (button.id == 12)
		{
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null)
			{
				GuiYesNo guiyesno = GuiSelectWorld
						.func_152129_a(this, worldinfo.getWorldName(), 12);
				this.mc.displayGuiScreen(guiyesno);
			}
		}
	}

	private void switchToRealms()
	{
		RealmsBridge realmsbridge = new RealmsBridge();
		realmsbridge.switchToRealms(this);
	}

	public void confirmClicked(boolean result, int id)
	{
		if (result && id == 12)
		{
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			isaveformat.flushCache();
			isaveformat.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(this);
		}
		else if (id == 13)
		{
			if (result)
			{
				try
				{
					Class<?> oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(
							(Object) null, new Object[0]);
					oclass.getMethod("browse", new Class[] { URI.class }).invoke(object,
							new Object[] { new URI(this.field_104024_v) });
				}
				catch (Throwable throwable)
				{
					logger.error("Couldn\'t open link", throwable);
				}
			}

			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_)
	{
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		byte b0 = 8;

		for (int k = 0; k < b0 * b0; ++k)
		{
			GlStateManager.pushMatrix();
			float f1 = ((float) (k % b0) / (float) b0 - 0.5F) / 64.0F;
			float f2 = ((float) (k / b0) / (float) b0 - 0.5F) / 64.0F;
			float f3 = 0.0F;
			GlStateManager.translate(f1, f2, f3);
			GlStateManager
					.rotate(MathHelper.sin(((float) this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F,
							1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-((float) this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F,
					0.0F);

			for (int l = 0; l < 6; ++l)
			{
				GlStateManager.pushMatrix();

				if (l == 1)
				{
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 2)
				{
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 3)
				{
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 4)
				{
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (l == 5)
				{
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.mc.getTextureManager().bindTexture(titlePanoramaPaths[l]);
				worldrenderer.startDrawingQuads();
				worldrenderer.setColorRGBA_I(16777215, 255 / (k + 1));
				float f4 = 0.0F;
				worldrenderer.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double) (0.0F + f4),
						(double) (0.0F + f4));
				worldrenderer.addVertexWithUV(1.0D, -1.0D, 1.0D, (double) (1.0F - f4),
						(double) (0.0F + f4));
				worldrenderer.addVertexWithUV(1.0D, 1.0D, 1.0D, (double) (1.0F - f4),
						(double) (1.0F - f4));
				worldrenderer.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double) (0.0F + f4),
						(double) (1.0F - f4));
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox(float p_73968_1_)
	{
		this.mc.getTextureManager().bindTexture(this.field_110351_G);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.startDrawingQuads();
		GlStateManager.disableAlpha();
		byte b0 = 3;

		for (int i = 0; i < b0; ++i)
		{
			worldrenderer.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (i + 1));
			int j = this.width;
			int k = this.height;
			float f1 = (float) (i - b0 / 2) / 256.0F;
			worldrenderer.addVertexWithUV((double) j, (double) k, (double) this.zLevel,
					(double) (0.0F + f1), 1.0D);
			worldrenderer.addVertexWithUV((double) j, 0.0D, (double) this.zLevel,
					(double) (1.0F + f1), 1.0D);
			worldrenderer.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (1.0F + f1),
					0.0D);
			worldrenderer.addVertexWithUV(0.0D, (double) k, (double) this.zLevel,
					(double) (0.0F + f1), 0.0D);
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int p_73971_1_, int p_73971_2_, float partialTicks)
	{
		this.mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		this.drawPanorama(p_73971_1_, p_73971_2_, partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.startDrawingQuads();
		float f1 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
		float f2 = (float) this.height * f1 / 256.0F;
		float f3 = (float) this.width * f1 / 256.0F;
		worldrenderer.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.width;
		int l = this.height;
		worldrenderer.addVertexWithUV(0.0D, (double) l, (double) this.zLevel, (double) (0.5F - f2),
				(double) (0.5F + f3));
		worldrenderer.addVertexWithUV((double) k, (double) l, (double) this.zLevel,
				(double) (0.5F - f2), (double) (0.5F - f3));
		worldrenderer.addVertexWithUV((double) k, 0.0D, (double) this.zLevel, (double) (0.5F + f2),
				(double) (0.5F - f3));
		worldrenderer.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (0.5F + f2),
				(double) (0.5F + f3));
		tessellator.draw();
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.disableAlpha();
		this.renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		byte yLevel = 5;
		this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
		this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
		this.mc.getTextureManager().bindTexture(arkcraftTitle);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		float scale = 0.5F;

		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		short short1 = (short) (255 * scale);
		int logoX = this.width / 2 - short1 / 2;
		this.drawTexturedModalRect(logoX / scale, yLevel / scale, 0, 0, 256, 256);
		GlStateManager.popMatrix();

		worldrenderer.setColorOpaque_I(-1);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) (this.width / 2), (yLevel + 175.0F) * scale, 0.0F);
		float f1 = 1.8F - MathHelper
				.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
		f1 = f1 * 70.0F / (float) (this.fontRendererObj.getStringWidth(this.splashText) + 32);
		GlStateManager.scale(f1, f1, f1);
		this.drawCenteredString(this.fontRendererObj, this.splashText, 0, 0, -256);
		GlStateManager.popMatrix();

		java.util.List<String> brandings = com.google.common.collect.Lists
				.reverse(net.minecraftforge.fml.common.FMLCommonHandler.instance().getBrandings(
						true));
		for (int i = 0; i < brandings.size(); i++)
		{
			String brd = brandings.get(i);
			if (!com.google.common.base.Strings.isNullOrEmpty(brd))
			{
				this.drawString(this.fontRendererObj, brd, 2,
						this.height - (10 + i * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
			}
		}

		this.drawCenteredString(this.fontRendererObj, ARKCraft.NAME + " - " + ARKCraft.VERSION,
				this.width / 2, this.height - 20, 0xFFFF00);

		String s1 = "Copyright Mojang AB. Do not distribute!";
		this.drawString(this.fontRendererObj, s1,
				this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, -1);

		if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0)
		{
			drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2,
					this.field_92019_w - 1, 1428160512);
			this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t,
					this.field_92021_u, -1);
			this.drawString(this.fontRendererObj, this.openGLWarning2,
					(this.width - this.field_92024_r) / 2,
					((GuiARKButton) this.buttonList.get(0)).yPosition - 12, -1);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		Object object = this.threadLock;

		synchronized (this.threadLock)
		{
			if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w)
			{
				GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this,
						this.field_104024_v, 13, true);
				guiconfirmopenlink.disableSecurityWarning();
				this.mc.displayGuiScreen(guiconfirmopenlink);
			}
		}
	}
}