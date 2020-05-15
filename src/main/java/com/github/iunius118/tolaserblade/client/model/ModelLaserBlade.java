package com.github.iunius118.tolaserblade.client.model;

import com.github.iunius118.tolaserblade.ToLaserBladeConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.*;

public class ModelLaserBlade implements IBakedModel {
    public final IBakedModel bakedJSONModel;

    public ItemStack itemStack;
    public World world;
    public EntityLivingBase entity;

    public TransformType cameraTransformType = TransformType.NONE;

    public IBlockState state;
    public EnumFacing side;
    public long rand;

    public int renderingMode = 0;

    public final Map<String, List<BakedQuad>> mapQuads_0 = new HashMap<>();
    public final Map<String, List<BakedQuad>> mapQuads_1 = new HashMap<>();
    public final String[] partNames = {"Hilt", "Hilt_bright", "Blade_core", "Blade_halo_1", "Blade_halo_2"};

    public ModelLaserBlade(IBakedModel bakedOBJModelIn, IBakedModel bakedJSONModelIn) {
        this(bakedOBJModelIn, bakedJSONModelIn, false);
    }

    public ModelLaserBlade(IBakedModel bakedOBJModelIn, IBakedModel bakedOBJModelSubIn, IBakedModel bakedJSONModelIn) {
        this(bakedOBJModelIn, bakedOBJModelSubIn, bakedJSONModelIn, false);
    }

    public ModelLaserBlade(IBakedModel bakedOBJModelIn, IBakedModel bakedJSONModelIn, boolean isInitialized) {
        bakedJSONModel = bakedJSONModelIn;

        if (!isInitialized) {
            // Separate Quads to each parts by OBJ Group.
            for (String partName : partNames) {
                mapQuads_0.put(partName, getPartQuads(bakedOBJModelIn, ImmutableList.of(partName)));
                mapQuads_1.putAll(mapQuads_0);
            }
        }
    }

    public ModelLaserBlade(IBakedModel bakedOBJModelIn, IBakedModel bakedOBJModelSubIn, IBakedModel bakedJSONModelIn, boolean isInitialized) {
        bakedJSONModel = bakedJSONModelIn;

        if (!isInitialized) {
            // Separate Quads to each parts by OBJ Group.
            for (String partName : partNames) {
                mapQuads_0.put(partName, getPartQuads(bakedOBJModelIn, ImmutableList.of(partName)));
                mapQuads_1.put(partName, getPartQuads(bakedOBJModelSubIn, ImmutableList.of(partName)));
            }
        }
    }

    public List<BakedQuad> getPartQuads(IBakedModel bakedModelIn, final List<String> visibleGroups) {
        List<BakedQuad> quads = Collections.EMPTY_LIST;

        if (bakedModelIn instanceof OBJBakedModel) {
            try {
                OBJModel obj = ((OBJBakedModel) bakedModelIn).getModel();

                // ModelState for handling visibility of each group.
                IModelState modelState = part -> {
                    if (part.isPresent()) {
                        UnmodifiableIterator<String> parts = Models
                                .getParts(part.get());

                        if (parts.hasNext()) {
                            String name = parts.next();

                            if (!parts.hasNext() && visibleGroups.contains(name)) {
                                // Return Absent for NOT invisible group.
                                return Optional.empty();
                            } else {
                                // Return Present for invisible group.
                                return Optional.of(TRSRTransformation.identity());
                            }
                        }
                    }

                    return Optional.empty();
                };

                // Bake model of visible groups.
                IBakedModel bakedModel = obj.bake(modelState, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

                quads = bakedModel.getQuads(null, null, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return quads;
    }

    public void handleItemState(ItemStack itemStackIn, World worldIn, EntityLivingBase entityLivingBaseIn) {
        itemStack = itemStackIn;
        world = worldIn;
        entity = entityLivingBaseIn;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState blockStateIn, EnumFacing enumFacingIn, long longRand) {
        if (enumFacingIn == null) {
            state = blockStateIn;
            side = enumFacingIn;
            rand = longRand;

            if (longRand >= 0 && longRand < partNames.length) {
                return mapQuads_0.get(partNames[(int) longRand]);
            }
        }

        return Collections.emptyList();
    }

    public List<BakedQuad> getQuadsByName(String name) {
        Map<String, List<BakedQuad>> mapQuads;

        if (renderingMode == 1) {
            mapQuads = mapQuads_1;
        } else {
            mapQuads = mapQuads_0;
        }

        if (mapQuads.containsKey(name)) {
            return mapQuads.get(name);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return bakedJSONModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return bakedJSONModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(Collections.EMPTY_LIST) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                // Copy ModelLaserBlade object and handle ItemStack.
                if (originalModel instanceof ModelLaserBlade) {
                    ModelLaserBlade model = (ModelLaserBlade) originalModel;
                    model.handleItemState(stack, world, entity);
                    return model;
                }

                return originalModel;
            }
        };
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType transformTypeIn) {
        Matrix4f matrix;

        // Get transformation matrix from JSON item model.
        matrix = bakedJSONModel.handlePerspective(transformTypeIn).getValue();

        cameraTransformType = transformTypeIn;

        if (ToLaserBladeConfig.client.isEnabledLaserBlade3DModel) {
            return Pair.of(this, matrix);
        } else {
            return Pair.of(this.bakedJSONModel, matrix);
        }
    }
}
