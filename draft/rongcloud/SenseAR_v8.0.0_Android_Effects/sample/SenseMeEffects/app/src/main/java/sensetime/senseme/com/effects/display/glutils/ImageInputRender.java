package sensetime.senseme.com.effects.display.glutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import sensetime.senseme.com.effects.utils.LogUtils;

import android.opengl.GLES20;
import android.util.Log;

public class ImageInputRender {
    private final static String TAG = "ImageInputRender";
	protected boolean DEBUG = true;
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    public static final String DRAW_POINTS_VERTEX_SHADER = "" +
            "attribute vec4 aPosition;\n" +
            "void main() {\n" +
            "  gl_PointSize = 2.0;" +
            "  gl_Position = aPosition;\n" +
            "}";

    public static final String DRAW_POINTS_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "uniform vec4 uColor;\n" +
            "void main() {\n" +
            "  gl_FragColor = uColor;\n" +
            "}";

    private final static String DRAW_POINTS_PROGRAM = "mPointProgram";
    private final static String DRAW_POINTS_COLOR = "uColor";
    private final static String DRAW_POINTS_POSITION = "aPosition";
    private int mDrawPointsProgram = 0;
    private int mColor = -1;
    private int mPosition = -1;
    private int[] mPointsFrameBuffers;

    private final LinkedList<Runnable> mRunOnDraw;
    private final String mVertexShader;
    private final String mFragmentShader;
    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    protected int mOutputWidth;
    protected int mOutputHeight;
    protected boolean mIsInitialized;
    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;
    protected int mSurfaceWidth, mSurfaceHeight;
    protected int mTableTextureID = OpenGLUtils.NO_TEXTURE;

    private final float vertexPoint[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private final float texturePoint[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

	public ImageInputRender(){
	    mRunOnDraw = new LinkedList<Runnable>();
	    mVertexShader = NO_FILTER_VERTEX_SHADER;
	    mFragmentShader = NO_FILTER_FRAGMENT_SHADER;

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(0, false, true)).position(0);
	}

    public void init() {
        onInit();
        mIsInitialized = true;
    }

    protected void onInit() {
        mGLProgId = OpenGLUtils.loadProgram(mVertexShader, mFragmentShader);
        Log.d("fenghx", "The program ID for image is "+mGLProgId);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId,
                "inputTextureCoordinate");
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    protected void runPendingOnDrawTasks() {
    	synchronized (mRunOnDraw) {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    	}
    }

    public final void destroy() {
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgId);
    }

    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    public void onDisplaySizeChanged(final int width, final int height) {
    	mSurfaceWidth = width;
    	mSurfaceHeight = height;
    }

    public int onDrawFrame(final int textureId, final FloatBuffer cubeBuffer,
            final FloatBuffer textureBuffer) {
		GLES20.glUseProgram(mGLProgId);
		runPendingOnDrawTasks();
		if (!mIsInitialized) {
		 return OpenGLUtils.NOT_INIT;
		}

		cubeBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribPosition);
		textureBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
		     textureBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
		if (textureId != OpenGLUtils.NO_TEXTURE) {
		 GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		 GLES20.glUniform1i(mGLUniformTexture, 0);
		}

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		return OpenGLUtils.ON_DRAWN;
	}

    public int onDrawFrame(final int textureId) {
		GLES20.glUseProgram(mGLProgId);
		runPendingOnDrawTasks();
		if (!mIsInitialized)
			return OpenGLUtils.NOT_INIT;

		mGLCubeBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribPosition);
		mGLTextureBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
		     mGLTextureBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
		if (textureId != OpenGLUtils.NO_TEXTURE) {
		 GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		 GLES20.glUniform1i(mGLUniformTexture, 0);
		}

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		return OpenGLUtils.ON_DRAWN;
	}

    public void initDrawPoints() {
        mDrawPointsProgram = OpenGLUtils.loadProgram(DRAW_POINTS_VERTEX_SHADER, DRAW_POINTS_FRAGMENT_SHADER);
        mColor = GLES20.glGetAttribLocation(mDrawPointsProgram, DRAW_POINTS_POSITION);
        mPosition = GLES20.glGetUniformLocation(mDrawPointsProgram, DRAW_POINTS_COLOR);

        if (mPointsFrameBuffers == null) {
            mPointsFrameBuffers = new int[1];

            GLES20.glGenFramebuffers(1, mPointsFrameBuffers, 0);
        }
    }

    public void onDrawPoints(int textureId, float[] points) {

        if (mDrawPointsProgram == 0) {
            initDrawPoints();
        }

        GLES20.glUseProgram(mDrawPointsProgram);
        GLES20.glUniform4f(mColor, 0.0f, 1.0f, 0.0f, 1.0f);

        FloatBuffer buff = null;

        buff = ByteBuffer.allocateDirect(points.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        buff.clear();
        buff.put(points).position(0);

        GLES20.glVertexAttribPointer(mPosition, 2, GLES20.GL_FLOAT, false, 0, buff);
        GLES20.glEnableVertexAttribArray(mPosition);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mPointsFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);

        GlUtil.checkGlError("glBindFramebuffer");

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, points.length/2);

        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        int glError = GLES20.glGetError();
        if(glError != 0)
            LogUtils.d(TAG, "CatchGLError : " + glError);
    }

}
