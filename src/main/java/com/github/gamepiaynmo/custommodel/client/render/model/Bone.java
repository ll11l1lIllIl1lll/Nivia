package com.github.gamepiaynmo.custommodel.client.render.model;

import com.github.gamepiaynmo.custommodel.client.ModelPack;
import com.github.gamepiaynmo.custommodel.expression.IExpressionBool;
import com.github.gamepiaynmo.custommodel.expression.IExpressionFloat;
import com.github.gamepiaynmo.custommodel.expression.ParseException;
import com.github.gamepiaynmo.custommodel.client.render.CustomJsonModel;
import com.github.gamepiaynmo.custommodel.client.render.PlayerBone;
import com.github.gamepiaynmo.custommodel.client.render.PlayerFeature;
import com.github.gamepiaynmo.custommodel.client.render.RenderContext;
import com.github.gamepiaynmo.custommodel.util.*;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Bone implements IBone {
    public final CustomJsonModel model;

    private String id;
    private IBone parent;

    private Vector3 position = Vector3.Zero.cpy();
    private Vector3 rotation = Vector3.Zero.cpy();
    private Vector3 scale = new Vector3(1, 1, 1);
    private boolean visible;
    private double[] physicsParams;
    private float[] color = new float[] { 1, 1, 1 };
    private float alpha = 1;
    private boolean emissive;

    private IExpressionFloat[] positionExpr;
    private IExpressionFloat[] rotationExpr;
    private IExpressionFloat[] scaleExpr;
    private IExpressionBool visibleExpr;
    private IExpressionFloat[] physicsExpr;
    private IExpressionFloat[] colorExpr;
    private IExpressionFloat alphaExpr;
    private IExpressionBool emissiveExpr;

    public List<PlayerFeature> attachments = Lists.newArrayList();
    private List<Box> boxes = Lists.newArrayList();
    private List<Quad> quads = Lists.newArrayList();
    private List<ParticleEmitter> particles = Lists.newArrayList();
    private List<ItemPart> items = Lists.newArrayList();

    private boolean physicalize = false;
    public Vector3 velocity = Vector3.Zero.cpy();
    private double length;

    private IExpressionFloat texture = null;
    private Vec2d textureSize;

    private boolean compiled = false;
    private int glList;

    public static Bone getBoneFromJson(ModelPack pack, CustomJsonModel model, JsonObject jsonObj, RenderContext context) throws ParseException {
        Bone bone = new Bone(model);

        bone.id = Json.getString(jsonObj, CustomJsonModel.ID);
        if (bone.id == null)
            throw new TranslatableException("error.custommodel.loadmodelpack.noboneid");

        String parentId = Json.getString(jsonObj, CustomJsonModel.PARENT);
        if (parentId == null)
            bone.parent = PlayerBone.NONE.getBone();
        else bone.parent = model.getBone(parentId);
        if (bone.parent == null)
            throw new TranslatableException("error.custommodel.loadmodelpack.noparentid", parentId);

        JsonElement textureId = jsonObj.get(CustomJsonModel.TEXTURE);
        if (textureId != null) {
            bone.texture = Json.getFloatExpression(textureId, 0, model.getParser());
            if (bone.texture == null)
                throw new TranslatableException("error.custommodel.loadmodelpack.notexture", textureId);

            JsonElement texSizeArray = jsonObj.get(CustomJsonModel.TEXTURE_SIZE);
            if (texSizeArray != null) {
                double[] texSize = Json.parseDoubleArray(texSizeArray, 2);
                bone.textureSize = new Vec2d(texSize[0], texSize[1]);
            }
        }

        bone.positionExpr = Json.parseFloatExpressionArray(jsonObj.get(CustomJsonModel.POSITION), 3, new float[] { 0, 0, 0 }, model.getParser());
        bone.rotationExpr = Json.parseFloatExpressionArray(jsonObj.get(CustomJsonModel.ROTATION), 3, new float[] { 0, 0, 0 }, model.getParser());
        bone.scaleExpr = Json.parseFloatExpressionArray(jsonObj.get(CustomJsonModel.SCALE), 3, new float[] { 1, 1, 1 }, model.getParser());
        bone.visibleExpr = Json.getBooleanExpression(jsonObj, CustomJsonModel.VISIBLE, true, model.getParser());
        JsonElement physical = jsonObj.get(CustomJsonModel.PHYSICS);
        if (physical != null) {
            bone.physicalize = true;
            bone.physicsExpr = Json.parseFloatExpressionArray(physical, 5, new float[] { 0, 0, 0, 0, 0 }, model.getParser());
            bone.physicsParams = new double[bone.physicsExpr.length];
        }

        bone.colorExpr = Json.parseFloatExpressionArray(jsonObj.get(CustomJsonModel.COLOR), 3, new float[] { 1, 1, 1 }, model.getParser());
        bone.alphaExpr = Json.getFloatExpression(jsonObj, CustomJsonModel.ALPHA, 1, model.getParser());
        bone.emissiveExpr = Json.getBooleanExpression(jsonObj, CustomJsonModel.EMISSIVE, false, model.getParser());

        Json.parseJsonArray(jsonObj.get(CustomJsonModel.ATTACHED), element -> {
            String id = element.getAsString();
            Collection<PlayerFeature> features = PlayerFeature.getListById(id);
            if (features == null)
                throw new TranslatableException("error.custommodel.loadmodelpack.nohidebone", id);
            for (PlayerFeature feature : features)
                bone.attachments.add(feature);
        });

        Json.parseJsonArray(jsonObj.get(CustomJsonModel.BOXES), element -> {
            bone.boxes.add(Box.getBoxFromJson(bone, element.getAsJsonObject(), context));
        });

        Json.parseJsonArray(jsonObj.get(CustomJsonModel.QUADS), element -> {
            bone.quads.add(Quad.getQuadFromJson(bone, element.getAsJsonObject(), context));
        });

        Json.parseJsonArray(jsonObj.get(CustomJsonModel.PARTICLES), element -> {
            bone.particles.add(ParticleEmitter.getParticleFromJson(bone, element.getAsJsonObject()));
        });

        Json.parseJsonArray(jsonObj.get(CustomJsonModel.ITEMS), element -> {
            bone.items.add(ItemPart.fromJson(bone, element.getAsJsonObject()));
        });

        return bone;
    }

    private Bone(CustomJsonModel model) {
        this.model = model;
    }

    public CustomJsonModel getModel() { return model; }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Vector3 getPosition(RenderContext context) {
        return position.cpy();
    }

    @Override
    public Vector3 getRotation(RenderContext context) {
        return rotation.cpy();
    }

    @Override
    public Vector3 getScale(RenderContext context) {
        return scale.cpy();
    }

    @Override
    public boolean isVisible(RenderContext context) { return visible; }

    @Override
    public Vec2d getTextureSize(RenderContext context) {
        if (textureSize == null) {
            return textureSize = model.pack.getTextureSize(getTexture(context).apply(context));
        }
        return textureSize;
    }

    @Override
    public Function<RenderContext, ResourceLocation> getTexture(RenderContext context) {
        if (texture == null) {
            Function<RenderContext, ResourceLocation> res = parent.getTexture(context);
            if (parent instanceof Bone)
                texture = ((Bone) parent).texture;
            return res;
        }
        return model.pack.getTexture((int) texture.eval(context), context);
    }

    @Override
    public IBone getParent() {
        return parent;
    }

    @Override
    public PlayerBone getPlayerBone() {
        return null;
    }

    public boolean isPhysicalized() { return physicalize; }
    public double[] getPhysicsParams() { return physicsParams; }
    public double getLength() { return length; }
    public boolean isEmissive() { return emissive; }

    public void render(RenderContext context) {
        float scaleFactor = context.currentParameter.scale;
        if (!compiled)
            compile(scaleFactor);

        if (context.renderEmissive == emissive) {
            GlStateManager.color(color[0], color[1], color[2], context.isInvisible ? 0.15f * alpha : alpha);
            GlStateManager.callList(glList);
        }

        if (!context.renderEmissive) {
            for (ItemPart item : items)
                item.render(context);
        }
        GlStateManager.enableRescaleNormal();
    }

    private void compile(float scaleFactor) {
        glList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(glList, GL11.GL_COMPILE);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        for (Box box : boxes)
            box.render(bufferBuilder, scaleFactor);
        for (Quad quad : quads)
            quad.render(bufferBuilder, scaleFactor);

        GlStateManager.glEndList();
        this.compiled = true;
    }

    private static double degToRad = Math.PI / 180;

    public void update(RenderContext context) {
        position.set(positionExpr[0].eval(context), positionExpr[1].eval(context), positionExpr[2].eval(context)).scl(-1, -1, 1);
        rotation.set(rotationExpr[0].eval(context), rotationExpr[1].eval(context), rotationExpr[2].eval(context)).scl(degToRad);
        scale.set(scaleExpr[0].eval(context), scaleExpr[1].eval(context), scaleExpr[2].eval(context));
        visible = parent.isVisible(context) && visibleExpr.eval(context);
        length = position.len() * 0.0625;
        color[0] = colorExpr[0].eval(context);
        color[1] = colorExpr[1].eval(context);
        color[2] = colorExpr[2].eval(context);
        alpha = alphaExpr.eval(context);
        emissive = emissiveExpr.eval(context);
        if (physicalize) {
            for (int i = 0; i < physicsParams.length; i++)
                physicsParams[i] = physicsExpr[i].eval(context);
        }
    }

    public void tick(Matrix4 transform, RenderContext context) {
        for (ParticleEmitter emitter : particles)
            emitter.tick(transform, context);
    }

    public void release() {
        for (ParticleEmitter emitter : particles)
            emitter.release();
    }

    public List<Box> getBoxes() { return boxes; }

    public List<Quad> getQuads() { return quads; }
}
