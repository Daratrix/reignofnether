package com.solegendary.reignofnether.unit.modelling.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.player.PlayerColors;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class PlayerColorRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    public static <T extends LivingEntity, M extends EntityModel<T>> LivingEntityRenderer<T, M> create(LivingEntityRenderer<T, M> baseRenderer) {
        baseRenderer.addLayer(new PlayerColorLayer(baseRenderer));
        return baseRenderer;
    }

    public static <T extends LivingEntity, M extends EntityModel<T>> EntityRendererProvider<T> layer(EntityRendererProvider<T> entityRendererProvider) {
        return (context) -> create((LivingEntityRenderer<T, M>) entityRendererProvider.create(context));
    }

    public static class PlayerColorLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

        private static HashMap<ResourceLocation, ResourceLocation> cachedColorLayerTexture = new HashMap<>();

        private static ResourceLocation getColorLayerTexture(ResourceLocation baseTexture) {
            var cached = cachedColorLayerTexture.getOrDefault(baseTexture, null);
            if (cached == null) {
                cached = new ResourceLocation(ReignOfNether.MOD_ID, baseTexture.getPath().replace("/entities/", "/entities/color_layers/"));
                cachedColorLayerTexture.put(baseTexture, cached);
            }

            return cached;
        }

        public PlayerColorLayer(RenderLayerParent<T, M> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int pPackedLight, T t, float v, float v1, float v2, float v3, float v4, float v5) {
            try {
                if (!(t instanceof Unit u)) {
                    return; // don't render team color for non-Unit entities
                }

                var texture = getColorLayerTexture(this.getTextureLocation(t));

                var colorHex = PlayerColors.getPlayerDisplayColorHex(u.getOwnerName());

                int r = (colorHex & 0xFF0000) >> 16;
                int g = (colorHex & 0xFF00) >> 8;
                int b = (colorHex & 0xFF);


                renderColoredCutoutModel(this.getParentModel(), texture, poseStack, multiBufferSource, pPackedLight, t, r/255f,g/255f,b/255f);
                //int i = LivingEntityRenderer.getOverlayCoords(t, 0);
                //VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.entityCutout(texture));
                //this.getParentModel().renderToBuffer(poseStack, buffer, pPackedLight, i, r, g, b, 1);
            } catch (Exception e) {
                // not ideal, but no way to detect if a texture exists or not...
            }
        }
    }
}
