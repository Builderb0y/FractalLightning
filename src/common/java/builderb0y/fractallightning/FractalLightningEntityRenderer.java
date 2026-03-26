package builderb0y.fractallightning;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.Identifier;

                           

import net.minecraft.client.render.entity.state.LightningEntityRenderState;

@Environment(EnvType.CLIENT)
public class FractalLightningEntityRenderer extends EntityRenderer<LightningEntity, LightningEntityRenderState> {

	public FractalLightningEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public LightningEntityRenderState createRenderState() {
		return new LightningEntityRenderState();
	}

	@Override
	public void updateRenderState(LightningEntity entity, LightningEntityRenderState state, float tickDelta) {
		super.updateRenderState(entity, state, tickDelta);
		state.seed = entity.seed;
	}

	                           

		@Override
		public void render(
			LightningEntityRenderState state,
			MatrixStack matrices,
			net.minecraft.client.render.command.OrderedRenderCommandQueue queue,
			net.minecraft.client.render.state.CameraRenderState cameraState
		) {
			queue.submitCustom(
				matrices,
				LightningRenderer.LIGHTNING_LAYER,
				(MatrixStack.Entry matrix, VertexConsumer buffer) -> {
					new LightningRendererImpl(matrix.getPositionMatrix(), buffer, state.age).generatePoints(state.seed);
				}
			);
		}

	     

           
                                                                                                                                 
                                                                                        
                                                         
                                                                                   
   

       

	@Override
	public boolean canBeCulled(LightningEntity entity) {
		return false;
	}
}

     

                            
                                                                                     

                                                                               
                 
  

          
                                                                                                                                                  
                                                                                       
                                                        
                                                                                                
  

          
                                                       
                                                                                       
  
 

      