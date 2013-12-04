package com.example.survivalgame.util;

import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class MyShader extends ShaderProgram {

	private static MyShader INSTANCE;
	
	

	// public static final String VERTEXSHADER = "uniform mat4 "
	// + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n"
	// + "attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION
	// + ";\n" + "attribute vec4 "
	// + ShaderProgramConstants.ATTRIBUTE_COLOR + ";\n" + "varying vec4 "
	// + ShaderProgramConstants.VARYING_COLOR + ";\n" + "void main() {\n"
	// + "	gl_Position = "
	// + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * "
	// + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" + "	"
	// + ShaderProgramConstants.VARYING_COLOR + " = "
	// + ShaderProgramConstants.ATTRIBUTE_COLOR + ";\n" + "}";

	public static final String VERTEXSHADER = "uniform mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" + "attribute vec4 "
			+ ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" + "attribute vec2 " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" + "varying vec2 "
			+ ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" + "void main() {\n" + "       " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = "
			+ ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" + "       gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * "
			+ ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" + "}";

	private static final String UNIFORM_DATA = "u_data";

	public static final String FRAGMENTSHADER = "precision highp float;\n" + "varying vec4 " + ShaderProgramConstants.VARYING_COLOR + ";\n" + "uniform vec3 "
			+ MyShader.UNIFORM_DATA + ";\n" + "void main() {\n" + "vec4 color = " + ShaderProgramConstants.VARYING_COLOR + ";\n" + "vec3 data = " + MyShader.UNIFORM_DATA + ";\n"
			+ "float xSquared = pow((data.x - gl_FragCoord.x)/1024.0, 2.0);\n" + "float ySquared = pow((data.y - gl_FragCoord.y)/1024.0, 2.0);\n"
			+ "float distance = sqrt(xSquared + ySquared);\n" + "float radius = data.z;\n"

			+ "if (distance < radius) \n" + "color.w = 0.0;\n" + "else \n" + "color.w = (distance-radius)/radius;\n" +

			"	gl_FragColor = color;\n" + "}";

	// ===========================================================
	// Fields
	// ===========================================================

	public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
	public static int sUniformData = ShaderProgramConstants.LOCATION_INVALID;

	// ===========================================================
	// Constructors
	// ===========================================================

	private MyShader() {
		super(MyShader.VERTEXSHADER, MyShader.FRAGMENTSHADER);
	}

	public static MyShader getInstance() {
		if (MyShader.INSTANCE == null) {
			MyShader.INSTANCE = new MyShader();
		}
		return MyShader.INSTANCE;
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

		MyShader.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		MyShader.sUniformData = this.getUniformLocation(MyShader.UNIFORM_DATA);
	}

	@Override
	public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
		GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION);

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(MyShader.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
		//
		
//		if(Util.firstItem){
//		GLES20.glUniform3f(MyShader.sUniformData, Util.lightX[0]*Util.ratioWidth, 1080-Util.lightY[0]*Util.ratioHeight, 0.174860644f);
//		Util.firstItem =false;
//		}else{
//		GLES20.glUniform3f(MyShader.sUniformData, Util.lightX[1]*Util.ratioWidth, 1080-Util.lightY[1]*Util.ratioHeight, 0.174860644f);
//		Util.firstItem =true;
//		}
	}

	@Override
	public void unbind(final GLState pGLState) {
		GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION);

		super.unbind(pGLState);
	}

}