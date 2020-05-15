package com.github.iunius118.tolaserblade;

import com.github.iunius118.tolaserblade.client.model.ModelLaserBlade;
import com.github.iunius118.tolaserblade.client.renderer.ItemLaserBladeRenderer;
import com.github.iunius118.tolaserblade.client.renderer.LaserTrapEntityRenderer;
import com.github.iunius118.tolaserblade.entity.LaserTrapEntity;
import com.github.iunius118.tolaserblade.item.ItemLasarBlade;
import com.github.iunius118.tolaserblade.item.ItemLaserBlade;
import com.github.iunius118.tolaserblade.network.ServerConfigMessage;
import com.github.iunius118.tolaserblade.network.ToLaserBladePacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ToLaserBlade.MOD_ID,
        name = ToLaserBlade.MOD_NAME,
        version = ToLaserBlade.MOD_VERSION,
        dependencies = ToLaserBlade.MOD_DEPENDENCIES,
        updateJSON = ToLaserBlade.MOD_UPDATE_JSON_URL,
        guiFactory = "com.github.iunius118.tolaserblade.client.gui.ConfigGuiFactory",
        useMetadata = true)
@EventBusSubscriber
public class ToLaserBlade {
    public static final String MOD_ID = "tolaserblade";
    public static final String MOD_NAME = "ToLaserBlade";
    public static final String MOD_VERSION = "1.12.2-1.7.2.0";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[1.12.2-14.23.3.2655,)";
    public static final String MOD_UPDATE_JSON_URL = "https://raw.githubusercontent.com/Iunius118/ToLaserBlade/master/update.json";

    public static Logger logger;

    public static final ToolMaterial MATERIAL_LASAR = EnumHelper.addToolMaterial("LASAR", 3, 255, 12.0F, 1.0F, 22).setRepairItem(new ItemStack(net.minecraft.init.Blocks.REDSTONE_TORCH));
    public static final ToolMaterial MATERIAL_LASER = EnumHelper.addToolMaterial("LASER", 3, 32767, 12.0F, 3.0F, 22).setRepairItem(new ItemStack(net.minecraft.init.Items.REDSTONE));

    public static final String NAME_ITEM_LASAR_BLADE = "lasar_blade";
    public static final ModelResourceLocation MRL_ITEM_LASAR_BLADE = new ModelResourceLocation(MOD_ID + ":" + NAME_ITEM_LASAR_BLADE, "inventory");

    public static final String NAME_ITEM_LASER_BLADE = "laser_blade";
    public static final ModelResourceLocation MRL_ITEM_LASER_BLADE = new ModelResourceLocation(MOD_ID + ":" + NAME_ITEM_LASER_BLADE + "_2d", "inventory");
    public static final ResourceLocation RL_OBJ_ITEM_LASER_BLADE = new ResourceLocation(MOD_ID, "item/laser_blade.obj");
    public static final ResourceLocation RL_OBJ_ITEM_LASER_BLADE_1 = new ResourceLocation(MOD_ID, "item/laser_blade_1.obj");
    public static final ResourceLocation RL_TEXTURE_ITEM_LASER_BLADE = new ResourceLocation(MOD_ID, "items/laser_blade");

    public static final String NAME_ENTITY_LASER_TRAP = "laser_trap";

    public static boolean hasShownUpdate = false;

    public static final ToLaserBladeConfig config = new ToLaserBladeConfig();

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
        proxy.Init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @ObjectHolder(MOD_ID)
    public static class ITEMS {
        public static final Item lasar_blade = null;
        public static final Item laser_blade = null;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemLasarBlade().setRegistryName(NAME_ITEM_LASAR_BLADE).setUnlocalizedName(MOD_ID + "." + NAME_ITEM_LASAR_BLADE),
                new ItemLaserBlade().setRegistryName(NAME_ITEM_LASER_BLADE).setUnlocalizedName(MOD_ID + "." + NAME_ITEM_LASER_BLADE)
        );
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        int entityID = 0;

        event.getRegistry().registerAll(
                EntityEntryBuilder.create().entity(LaserTrapEntity.class).id(NAME_ENTITY_LASER_TRAP, entityID++).name(MOD_ID + "." + NAME_ENTITY_LASER_TRAP).tracker(64, 4, false).build()
        );
    }

    @SubscribeEvent
    public static void remapItems(RegistryEvent.MissingMappings<Item> mappings) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : mappings.getAllMappings()) {
            if (!mapping.key.getResourceDomain().equals(MOD_ID)) {
                continue;
            }

            String name = mapping.key.getResourcePath();
            if (name.equals(MOD_ID + "." + NAME_ITEM_LASER_BLADE)) {
                // Replace item ID "tolaserblade:tolaserblade.laser_blade" (-1.11.2) with "tolaserblade:laser_blade" (1.12-)
                mapping.remap(ToLaserBlade.ITEMS.laser_blade);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            // Send packet of server-side settings to logged-in player's client
            ToLaserBladeConfig.ServerConfig serverConfig = new ToLaserBladeConfig.ServerConfig();
            serverConfig.isEnabledBlockingWithLaserBlade = ToLaserBladeConfig.common.isEnabledBlockingWithLaserBlade;
            serverConfig.laserBladeEfficiency = ToLaserBladeConfig.common.laserBladeEfficiency;

            logger.info("Send packet of server-side settings to logged-in player's client.");
            ToLaserBladePacketHandler.INSTANCE.sendTo(new ServerConfigMessage(serverConfig), (EntityPlayerMP) event.player);
        }
    }

    // For damage test
    /*
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        float dmg = CombatRules.getDamageAfterAbsorb(event.getAmount(), (float)event.getEntityLiving().getTotalArmorValue(), (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        String str = event.getSource().getDamageType() + " caused " + dmg + " point damage to " + event.getEntityLiving().getName() + "!";
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(str));
    }
    // */

    // Proxy Classes

    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent event) {
            // Init network (register packet)
            ToLaserBladePacketHandler.init();
            // Init config
            ToLaserBladeConfig.init();
        }

        public void Init(FMLInitializationEvent event) {
            // register item event for LaserBlade dyeing
            MinecraftForge.EVENT_BUS.register(ITEMS.laser_blade);
        }

        public void postInit(FMLPostInitializationEvent event) {

        }
    }

    @SideOnly(Side.SERVER)
    public static class ServerProxy extends CommonProxy {

    }

    @SideOnly(Side.CLIENT)
    public static class ClientProxy extends CommonProxy {
        @Override
        public void preInit(FMLPreInitializationEvent event) {
            super.preInit(event);

            OBJLoader.INSTANCE.addDomain(MOD_ID);
            MinecraftForge.EVENT_BUS.register(this); // register onModelBakeEvent
        }

        @Override
        public void postInit(FMLPostInitializationEvent event) {
            super.postInit(event);

            // Register item color handler
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemLaserBlade.ColorHandler.INSTANCE, ITEMS.laser_blade);
        }

        // ToLaserBladeConfig
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(MOD_ID)) {
                ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);

                if (Minecraft.getMinecraft().isSingleplayer()) {
                    // Update common settings only when the player is playing single-player
                    ToLaserBladeConfig.server.isEnabledBlockingWithLaserBlade = ToLaserBladeConfig.common.isEnabledBlockingWithLaserBlade;
                    ToLaserBladeConfig.server.laserBladeEfficiency = ToLaserBladeConfig.common.laserBladeEfficiency;
                }
            }
        }

        // Model registry and bakery
        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {
            ModelLoader.setCustomModelResourceLocation(ITEMS.lasar_blade, 0, MRL_ITEM_LASAR_BLADE);
            ModelLoader.setCustomModelResourceLocation(ITEMS.laser_blade, 0, MRL_ITEM_LASER_BLADE);

            // Register LaserBlade renderer
            ITEMS.laser_blade.setTileEntityItemStackRenderer(new ItemLaserBladeRenderer());

            // Register laser trap entity renderer
            RenderingRegistry.registerEntityRenderingHandler(LaserTrapEntity.class, LaserTrapEntityRenderer::new);
        }

        @SubscribeEvent
        public void onModelBakeEvent(ModelBakeEvent event) {
            ModelLaserBlade modelLaserBlade = new ModelLaserBlade(bakeModel(RL_OBJ_ITEM_LASER_BLADE), bakeModel(RL_OBJ_ITEM_LASER_BLADE_1), event.getModelRegistry().getObject(MRL_ITEM_LASER_BLADE));
            event.getModelRegistry().putObject(MRL_ITEM_LASER_BLADE, modelLaserBlade);
        }

        public IBakedModel bakeModel(ResourceLocation location) {
            try {
                IModel model = ModelLoaderRegistry.getModel(location);
                // logger.info("Loaded obj model: " + model.hashCode());  // for debug
                return model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @SubscribeEvent
        public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
            // Register texture for obj model
            event.getMap().registerSprite(RL_TEXTURE_ITEM_LASER_BLADE);
        }

        @SubscribeEvent
        public void onConnectedToServer(ClientConnectedToServerEvent event) {
            if (!hasShownUpdate) {
                // Check update and Notify client
                CheckResult result = ForgeVersion.getResult(Loader.instance().activeModContainer());
                Status status = result.status;

                if (status == Status.PENDING) {
                    // Failed to get update information
                    return;
                }

                if (status == Status.OUTDATED || status == Status.BETA_OUTDATED) {
                    ITextComponent modNameHighlighted = new TextComponentString(MOD_NAME);
                    modNameHighlighted.getStyle().setColor(TextFormatting.YELLOW);

                    ITextComponent newVersionHighlighted = new TextComponentString(result.target.toString());
                    newVersionHighlighted.getStyle().setColor(TextFormatting.YELLOW);

                    ITextComponent message = new TextComponentTranslation("tolaserblade.message.update", modNameHighlighted).appendText(": ").appendSibling(newVersionHighlighted);
                    message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, result.url));

                    Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(message);

                    hasShownUpdate = true;
                }
            }
        }
    }
}
