package sensetime.senseme.com.effects.display;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import com.sensetime.stmobile.STCommon;
import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STRotateType;
import com.sensetime.stmobile.model.STAnimalFaceInfo;
import com.sensetime.stmobile.model.STEffectCustomParam;
import com.sensetime.stmobile.model.STEffectRenderInParam;
import com.sensetime.stmobile.model.STEffectRenderOutParam;
import com.sensetime.stmobile.model.STEffectTexture;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STQuaternion;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import sensetime.senseme.com.effects.VideoPlayerActivity;
import sensetime.senseme.com.effects.display.glutils.GlUtil;
import sensetime.senseme.com.effects.display.glutils.OpenGLUtils;
import sensetime.senseme.com.effects.display.glutils.STGLRender;
import sensetime.senseme.com.effects.utils.FileUtils;
import sensetime.senseme.com.effects.utils.LogUtils;

public class VideoPreviewDisplay extends ImageDisplay {
	private String TAG = "VideoPreviewDisplay";
	private boolean mNeedAvatar = true;
	private SurfaceTexture mVideoTexture;
	private MediaPlayer mMediaPlayer;
	private String mVideoPath;
	private boolean mNeedPause = true;
	private Timer mTimer = new Timer();
	private TimerTask mTimerTask;
	private MediaMetadataRetriever mMediaMetadataRetriever;
	private boolean mIsFirstPlaying = true;
	private STGLRender mGLRender;
	private ByteBuffer mRGBABuffer;

	private String mVideoRotation;
	private long mStartTime;
	private boolean mIsPaused = false;
	protected int mHumanActionCreateConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO_SINGLE_THREAD;


	public VideoPreviewDisplay(Context context, GLSurfaceView glSurfaceView, Handler handler, String path){
		super(context, glSurfaceView, handler);

		mVideoPath = path;
		mGLRender = new STGLRender();
	}


	public long getHumanActionDetectConfig(){
    	return mDetectConfig;
	}

	public boolean ifNeedBeauty(){
    	return mNeedBeautify;
	}
	public boolean ifNeedNakeup(){
		return mNeedMakeup;
	}
	public boolean ifNeedSticker(){
		return mNeedSticker;
	}
	public boolean ifNeedFilter(){
		return mNeedFilter;
	}


	public String getCurrentStickerPath(){
    	return mCurrentSticker;
	}

//	public float[] getCurrentBeautyParams() {
//		return mBeautifyParams;
//	}

	public String getCurrentFilterPath(){
		return mCurrentFilterStyle;
	}

	public float getCurrentFilterStrength(){
		return mCurrentFilterStrength;
	}

	public String[] getCurrentMakeupPaths(){
		return mCurrentMakeup;
	}

	public float[] getCurrentMakeupStrengths(){
		return mMakeupStrength;
	}

	@Override
	public void onResume(){
		LogUtils.i(TAG, "onResume");
		mIsPaused = false;
		mNeedPause = true;

		mGLRender = new STGLRender();
		mGlSurfaceView.onResume();
		mGlSurfaceView.forceLayout();
		mGlSurfaceView.requestRender();

		if(mNeedFilter){
			mCurrentFilterStyle = null;
		}
	}

	@Override
	public void onPause() {
		LogUtils.i(TAG, "onPause");
		mIsPaused = true;

		if(mMediaPlayer != null){
			mMediaPlayer.pause();
		}

		mGlSurfaceView.queueEvent(new Runnable() {
			@Override
			public void run() {
				mSTHumanActionNative.reset();
				mSTMobileAvatarNative.destroyInstance();
				deleteTextures();
				mRGBABuffer = null;
				mGLRender.destroyFrameBuffers();
			}
		});

		mGlSurfaceView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(mMediaPlayer != null){
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);

		if (mIsPaused == true) {
			return ;
		}
		adjustViewPort(width, height);

		mGLRender.init(mImageWidth, mImageHeight);
		mStartTime = System.currentTimeMillis();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		setUpVideo();
	}

	private void adjustViewPort(int width, int height) {
		mDisplayHeight = height;
		mDisplayWidth = width;
		GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
		mGLRender.calculateVertexBuffer(mDisplayWidth, mDisplayHeight, mImageWidth, mImageHeight);
	}

	@Override
	protected void initHumanAction() {
		{
			//从sd读取model路径，创建handle
			//int result = mSTHumanActionNative.createInstance(FileUtils.getTrackModelPath(mContext), mHumanActionCreateConfig);

			//从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
			int result = mSTHumanActionNative.createInstanceFromAssetFile(FileUtils.getActionModelName(), mHumanActionCreateConfig, mContext.getAssets());
			LogUtils.i(TAG, "the result for createInstance for human action is %d", result);

			if(result == 0){
				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAND, mContext.getAssets());
				LogUtils.i(TAG, "add hand model result: %d", result);
				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_SEGMENT, mContext.getAssets());
				LogUtils.i(TAG, "add figure segment model result: %d", result);

				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.getFaceExtraModelName(), mContext.getAssets());
				LogUtils.i(TAG, "add face extra model result %d", result);

				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_EYEBALL_CONTOUR, mContext.getAssets());
				LogUtils.i(TAG, "add eyeball contour model result: %d", result);

				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAIR, mContext.getAssets());
				LogUtils.i(TAG,"add hair model result: %d", result );

				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_LIPS_PARSING, mContext.getAssets());
				LogUtils.i(TAG,"add lips parsing model result: %d", result );

				result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.HEAD_SEGMENT_MODEL_NAME, mContext.getAssets());
				LogUtils.i(TAG,"add head segment model result: %d", result );

				//for test avatar
				//if(mNeedAvatar){
					int ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_HELP, mContext.getAssets());
					LogUtils.i(TAG, "add avatar help model result: %d", ret);

//				ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_TONGUE, mContext.getAssets());
//				LogUtils.i(TAG,"add tongue model result: %d", ret );
				//}
			}
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		LogUtils.i(TAG, "onDrawFrame");
		if (mRGBABuffer == null) {
			mRGBABuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
		}

		if (mBeautifyTextureId == null) {
			mBeautifyTextureId = new int[1];
			GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
		}

		if (mMakeupTextureId == null) {
			mMakeupTextureId = new int[1];
			GlUtil.initEffectTexture(mImageWidth, mImageHeight, mMakeupTextureId, GLES20.GL_TEXTURE_2D);
		}

		if (mTextureOutId == null) {
			mTextureOutId = new int[1];
			GlUtil.initEffectTexture(mImageWidth, mImageHeight, mTextureOutId, GLES20.GL_TEXTURE_2D);
		}

		if(mVideoTexture != null && !mIsPaused){
			mVideoTexture.updateTexImage();
		}else{
			return;
		}

		mStartTime = System.currentTimeMillis();
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		mRGBABuffer.rewind();

		long preProcessCostTime = System.currentTimeMillis();
		int textureId = mGLRender.preProcess(mTextureId, mRGBABuffer);
		int originalTextureId = textureId;
		LogUtils.i(TAG, "preprocess cost time: %d", System.currentTimeMillis() - preProcessCostTime);

		int result = -1;

		if(!mShowOriginal && mSTMobileEffectNative != null){

			long startHumanAction = System.currentTimeMillis();
			int orientation = STRotateType.ST_CLOCKWISE_ROTATE_0;
			STHumanAction humanAction = mSTHumanActionNative.humanActionDetect(mRGBABuffer.array(), STCommon.ST_PIX_FMT_RGBA8888,
					mDetectConfig, orientation, mImageWidth, mImageHeight);
			LogUtils.i(TAG, "human action cost time: %d", System.currentTimeMillis() - startHumanAction);

			long catDetectStartTime = System.currentTimeMillis();
			mAnimalFace = mStAnimalNative.animalDetect(mRGBABuffer.array(),  STCommon.ST_PIX_FMT_RGBA8888, orientation, mImageWidth, mImageHeight);
			LogUtils.i(TAG, "cat face detect cost time: %d", System.currentTimeMillis() - catDetectStartTime);
			animalFaceLlength = mAnimalFace == null ? 0 : mAnimalFace.length;

			if(mCurrentFilterStyle != mFilterStyle){
				mCurrentFilterStyle = mFilterStyle;
				mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStyle);
			}
			if(mCurrentFilterStrength != mFilterStrength){
				mCurrentFilterStrength = mFilterStrength;
				mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStrength);
			}

			STEffectTexture stEffectTexture = new STEffectTexture(textureId, mImageWidth, mImageHeight, 0);
			STEffectTexture stEffectTextureOut = new STEffectTexture(mBeautifyTextureId[0], mImageWidth, mImageHeight, 0);

			int event = mCustomEvent;
			STEffectCustomParam customParam;
			if(mSensorEvent != null && mSensorEvent.values != null && mSensorEvent.values.length > 0){
				customParam = new STEffectCustomParam(new STQuaternion(mSensorEvent.values), false, event);
			} else {
				customParam = new STEffectCustomParam(new STQuaternion(0f,0f,0f,1f), false, event);
			}

			STAnimalFaceInfo animalFaceInfo = null;
			if(mAnimalFace != null && mAnimalFace.length >0){
				animalFaceInfo = new STAnimalFaceInfo(mAnimalFace, mAnimalFace.length);
			}

			STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(humanAction, animalFaceInfo, orientation, STRotateType.ST_CLOCKWISE_ROTATE_0, false, customParam, stEffectTexture, null);
			STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, humanAction);
			result = mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);

			if(stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null){
				textureId = stEffectRenderOutParam.getTexture().getId();
			}

			if(event == mCustomEvent){
				mCustomEvent = 0;
			}

			GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
			mGLRender.onDrawFrame(textureId);
		} else {
			GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
			mGLRender.onDrawFrame(originalTextureId);
		}
	}

	public void prepareVideoAndStart() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();

//				if(mMediaPlayer != null){
//					Message msg = mHandler.obtainMessage(VideoPlayActivity.MSG_MEDIA_PREPARE_PLAY);
//					msg.arg1 = mMediaPlayer.getDuration();
//					mHandler.sendMessage(msg);
//				}
			}
		});
		Surface surface = new Surface(mVideoTexture);
		mMediaPlayer.setSurface(surface);
		surface.release();
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(mVideoPath);
			Thread.sleep(10);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(mNeedPause){
					return ;
				}
				// 在播放完毕被回调
				if(mMediaPlayer != null){
					mMediaPlayer.seekTo(0);
				}
				if(mTimerTask != null){
					if(mMediaPlayer != null && !mMediaPlayer.isPlaying()){
						int position = mMediaPlayer.getCurrentPosition();
//						Message msg = mHandler.obtainMessage(VideoPlayerActivity.MSG_MEDIA_PROGRESS_UPDTAE);
//						msg.arg1 = position;
//						mHandler.sendMessage(msg);
					}
					mTimerTask.cancel();
				}
				if(mIsFirstPlaying){
//					Message msg = mHandler.obtainMessage(VideoPlayerActivity.MSG_STOP_RECORDING);
//					mHandler.sendMessage(msg);
//					mIsFirstPlaying = false;
					StartPlayVideo();
				}else{
					StartPlayVideo();
				}

			}
		});
	}

	private void confirmWidthAndHeight(String rotation){
		try {
			if(rotation == null){
				mImageHeight = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
				mImageWidth = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
				mGLRender.adjustVideoTextureBuffer(180, true, false);

				return;
			}
			switch (Integer.valueOf(rotation)){
				case 0:
					mImageHeight = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
					mImageWidth = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
					mGLRender.adjustVideoTextureBuffer(180, true, false);
					break;
				case 90:
					mImageWidth = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
					mImageHeight = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
					mGLRender.adjustVideoTextureBuffer(90, true,false);
					break;
				case 180:
					mImageHeight = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
					mImageWidth = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
					mGLRender.adjustVideoTextureBuffer(0, true,false);
					break;
				case 270:
					mImageWidth = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
					mImageHeight = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
					mGLRender.adjustVideoTextureBuffer(270, true,false);
					break;
				default:
					break;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

		@Override
		public void onFrameAvailable(SurfaceTexture surfaceTexture) {
			mGlSurfaceView.requestRender();
			if(mNeedPause && mMediaPlayer != null){
				mMediaPlayer.pause();
			}
		}
	};

	private void setUpVideo() {
		// 初始化Camera设备预览需要的显示区域(mSurfaceTexture)
		if (mTextureId == OpenGLUtils.NO_TEXTURE) {
			mTextureId = OpenGLUtils.getExternalOESTextureID();

			mVideoTexture = new SurfaceTexture(mTextureId);
			mVideoTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
		}

		try{
			mMediaMetadataRetriever = new MediaMetadataRetriever();
			mMediaMetadataRetriever.setDataSource(mVideoPath);
		}catch (Exception e){
			Log.e(TAG, "setUpVideo: " + e.getMessage() );

			return;
		}

		mVideoRotation = mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
		confirmWidthAndHeight(mVideoRotation);
		//开始播放
		prepareVideoAndStart();
	}

	public void StartPlayVideo(){
		if(mMediaPlayer != null){
			mNeedPause = false;

			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					int position = 0;
					if(mMediaPlayer != null){
						position = mMediaPlayer.getCurrentPosition();
					}

					int duration = 0;
					if(mMediaPlayer != null){
						duration = mMediaPlayer.getDuration();
					}
					if (duration > 0) {
//						Message msg = mHandler.obtainMessage(VideoPlayerActivity.MSG_MEDIA_PROGRESS_UPDTAE);
//						msg.arg1 = position;
//						mHandler.sendMessage(msg);
					}
				}
			};
			mMediaPlayer.start();
			mTimer.schedule(mTimerTask,0,500);
		}
	}

	public void StopPlayViedo(){
		if(mMediaPlayer != null){
			try {
				mMediaPlayer.pause();
				mMediaPlayer.seekTo(0);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void pauseVideo(){
		if(mMediaPlayer != null){
			try {
				mMediaPlayer.pause();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

}
