package com.example.survivalgame.util;

import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;
import android.util.Log;

public class SpotLight extends ShaderProgram {

	private static SpotLight INSTANCE;

	public static final String VERTEXSHADER = "uniform mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" + "attribute vec4 "
			+ ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" + "attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_COLOR + ";\n" + "varying vec4 "
			+ ShaderProgramConstants.VARYING_COLOR + ";\n" + "void main() {\n" + "     gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * "
			+ ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" + " " + ShaderProgramConstants.VARYING_COLOR + " = " + ShaderProgramConstants.ATTRIBUTE_COLOR + ";\n" + "}";

	private static final String UNIFORM_DATA = "u_data";

	public static String FRAGMENTSHADER = "precision highp float;\n" + "varying vec4 " + ShaderProgramConstants.VARYING_COLOR + ";\n" + "uniform vec4 "
			+ SpotLight.UNIFORM_DATA + ";\n" + "void main() {\n" + "vec4 color = " + ShaderProgramConstants.VARYING_COLOR + ";\n" + "vec4 data = " + SpotLight.UNIFORM_DATA + ";\n"
			+ "float xSquared = pow((data.x - gl_FragCoord.x)/300.0, 2.0);\n" + "float ySquared = pow((data.y - gl_FragCoord.y)/300.0, 2.0);\n"
			+ "float distance = sqrt(xSquared + ySquared);\n" + "float radius = data.z;\n"

			+ "\n" + "{ \n" + "float opacity = distance; \n" + "if(opacity >= data.w) \n"
			+ "{opacity = data.w;} \n" + "color.w = opacity;}\n" +

			"       gl_FragColor = color;\n" + "}";

	// ===========================================================
	// Fields
	// ===========================================================

	public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
	public static int sUniformData = ShaderProgramConstants.LOCATION_INVALID;

	// ===========================================================
	// Constructors
	// ===========================================================

	private SpotLight() {
		super(SpotLight.VERTEXSHADER, SpotLight.FRAGMENTSHADER);
	}
	
	public static void reloadFragment(){
		
		Log.v("GAME","TAMANIO COSO "+(300*Util.ratioWidth));
		
		FRAGMENTSHADER = "precision highp float;\n" + "varying vec4 " + ShaderProgramConstants.VARYING_COLOR + ";\n" + "uniform vec4 "
				+ SpotLight.UNIFORM_DATA + ";\n" + "void main() {\n" + "vec4 color = " + ShaderProgramConstants.VARYING_COLOR + ";\n" + "vec4 data = " + SpotLight.UNIFORM_DATA + ";\n"
				+ "float xSquared = pow((data.x - gl_FragCoord.x)/"+133*Util.ratioWidth+", 2.0);\n" + "float ySquared = pow((data.y - gl_FragCoord.y)/"+133*Util.ratioHeight+", 2.0);\n"
				+ "float distance = sqrt(xSquared + ySquared);\n" + "float radius = data.z;\n"

				+ "\n" + "{ \n" + "float opacity = distance; \n" + "if(opacity >= data.w) \n"
				+ "{opacity = data.w;} \n" + "color.w = opacity;}\n" +

				"       gl_FragColor = color;\n" + "}";
	}
	

	public static SpotLight getInstance() {
		if (SpotLight.INSTANCE == null) {
			Log.v("GAME","Width Ratio "+Util.ratioWidth);
			reloadFragment();
			SpotLight.INSTANCE = new SpotLight();
		}
		return SpotLight.INSTANCE;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void link(final GLState pGLState) throws ShaderProgramLinkException {
		GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
		GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION, ShaderProgramConstants.ATTRIBUTE_COLOR);

		super.link(pGLState);

		SpotLight.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		SpotLight.sUniformData = this.getUniformLocation(SpotLight.UNIFORM_DATA);
	}

	@Override
	public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
		GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION);

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(SpotLight.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);

		GLES20.glUniform4f(SpotLight.sUniformData, Util.widthScreen/2, Util.heightScreen/2, 0.2f, Util.oppacityScreen);
	}

	@Override
	public void unbind(final GLState pGLState) {
		GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION);

		super.unbind(pGLState);
	}

}