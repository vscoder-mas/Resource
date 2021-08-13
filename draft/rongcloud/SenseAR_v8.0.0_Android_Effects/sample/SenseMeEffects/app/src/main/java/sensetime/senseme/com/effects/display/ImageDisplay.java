//package sensetime.senseme.com.effects.display;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.hardware.SensorEvent;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLSurfaceView.Renderer;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.sensetime.stmobile.STCommon;
//import com.sensetime.stmobile.STEffectBeautyType;
//import com.sensetime.stmobile.STMobileAvatarNative;
//import com.sensetime.stmobile.STMobileEffectNative;
//import com.sensetime.stmobile.STRotateType;
//import com.sensetime.stmobile.model.STAnimalFace;
//import com.sensetime.stmobile.model.STAnimalFaceInfo;
//import com.sensetime.stmobile.model.STEffectBeautyInfo;
//import com.sensetime.stmobile.model.STEffectCustomParam;
//import com.sensetime.stmobile.model.STEffectRenderInParam;
//import com.sensetime.stmobile.model.STEffectRenderOutParam;
//import com.sensetime.stmobile.model.STEffectTexture;
//import com.sensetime.stmobile.model.STFaceAttribute;
//import com.sensetime.stmobile.model.STHumanAction;
//import com.sensetime.stmobile.STMobileHumanActionNative;
//import com.sensetime.stmobile.model.STMobile106;
//import com.sensetime.stmobile.model.STQuaternion;
//import com.sensetime.stmobile.sticker_module_types.STCustomEvent;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.TreeMap;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//import sensetime.senseme.com.effects.activity.CameraActivity;
////import sensetime.senseme.com.effects.activity.ImageActivity;
//import sensetime.senseme.com.effects.SenseMeApplication;
//import sensetime.senseme.com.effects.display.glutils.ImageInputRender;
//import sensetime.senseme.com.effects.encoder.mediacodec.utils.CollectionUtils;
//import sensetime.senseme.com.effects.utils.Constants;
//import sensetime.senseme.com.effects.utils.EffectInfoDataHelper;
//import sensetime.senseme.com.effects.utils.FileUtils;
//import sensetime.senseme.com.effects.utils.LogUtils;
//import sensetime.senseme.com.effects.display.glutils.TextureRotationUtil;
//import sensetime.senseme.com.effects.display.glutils.OpenGLUtils;
//import sensetime.senseme.com.effects.display.glutils.GlUtil;
//import sensetime.senseme.com.effects.utils.STUtils;
//
//import static sensetime.senseme.com.effects.display.BaseCameraDisplay.MESSAGE_NEED_ADD_STICKER;
//import static sensetime.senseme.com.effects.utils.STUtils.convertMakeupTypeToNewType;
//
//public class ImageDisplay extends BaseDisplay implements Renderer{
//
//    protected Bitmap mOriginBitmap;
//	protected byte[] mTmpBuffer = null;
//    private String TAG = "ImageDisplay";
//
//	private boolean mNeedAvatar = true;
//	protected boolean mNeedAvatarExpression = false;
//	protected float[] mAvatarExpression = new float[54];
//	protected STMobileAvatarNative mSTMobileAvatarNative = new STMobileAvatarNative();
//
//	protected int mImageWidth;
//	protected int mImageHeight;
//	protected int mDisplayWidth;
//	protected int mDisplayHeight;
//
//	protected int mLastBeautyOverlapCount = -1;
//
//	protected Context mContext;
//	protected final FloatBuffer mVertexBuffer;
//	protected final FloatBuffer mTextureBuffer;
//	protected ImageInputRender mImageInputRender;
//	protected boolean mInitialized = false;
//	protected long mFrameCostTime = 0;
//	protected Bitmap mProcessedImage;
//	protected boolean mNeedSave = false;
//	protected Handler mHandler;
//
//	protected CostChangeListener mCostListener;
//
//	protected String mCurrentSticker;
//	protected String mCurrentFilterStyle;
//	protected float mCurrentFilterStrength;
//	protected String mFilterStyle;
////	protected float[] mBeautifyParams = new float[ImageActivity.DEFAULT_BEAUTIFY_PARAMS.length];
//
//	protected boolean mNeedBeautify = false;
//	protected boolean mNeedFaceAttribute = true;
//	protected boolean mNeedSticker = true;
//	protected boolean mNeedFilter = true;
//	protected boolean mNeedMakeup = false;
//	protected String mFaceAttribute = " ";
//	protected int[] mBeautifyTextureId, mMakeupTextureId;
//	protected int[] mTextureOutId;
//	protected int[] mFilterTextureOutId;
//
//	protected boolean mShowOriginal = false;
//
//	protected int mHumanActionCreateConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE;
//	protected long mHumanActionDetectConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_DETECT;
//
//	protected static final int MESSAGE_NEED_CHANGE_STICKER = 1001;
//	protected static final int MESSAGE_NEED_REMOVE_STICKER = 1004;
//	protected static final int MESSAGE_NEED_REMOVEALL_STICKERS = 1005;
//
//	protected HandlerThread mChangeStickerManagerThread;
//	protected Handler mChangeStickerManagerHandler;
//	public TreeMap<Integer, String> mCurrentStickerMaps = new TreeMap<>();
//
//	protected int mCustomEvent = 0;
//	protected SensorEvent mSensorEvent;
//
//	protected boolean mNeedFaceExtraInfo = true;
//	protected STAnimalFace[] mAnimalFace;
//	protected int animalFaceLlength = 0;
//	protected boolean needAnimalDetect;
//
//	protected int[] mMakeupPackageId = new int[Constants.MAKEUP_TYPE_COUNT];
//	protected String[] mCurrentMakeup = new String[Constants.MAKEUP_TYPE_COUNT];
//	protected float[] mMakeupStrength = new float[Constants.MAKEUP_TYPE_COUNT];
//	protected boolean DEBUG = CameraActivity.DEBUG;
////	private boolean mNeed240 = false;
//
//	/**
//	 * SurfaceTexureid
//	 */
//	protected int mTextureId = OpenGLUtils.NO_TEXTURE;
//
//    public ImageDisplay(Context context, GLSurfaceView glSurfaceView, Handler handler){
//    	super(glSurfaceView);
//    	mImageInputRender = new ImageInputRender();
//
//    	glSurfaceView.setEGLContextClientVersion(2);
//		glSurfaceView.setEGLContextFactory(this);
//		glSurfaceView.setRenderer(this);
//		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//
//    	mContext = context;
//    	mHandler = handler;
//
//		mVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        mVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
//
//        mTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        mTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
//
//		initHumanAction();
//		initFaceAttribute();
//		initCatFace();
//
//		if(mNeedAvatar)initAvatar();
//
////		for(int i = 0; i < ImageActivity.DEFAULT_BEAUTIFY_PARAMS.length; i++){
////			mBeautifyParams[i] = ImageActivity.DEFAULT_BEAUTIFY_PARAMS[i];
////		}
//
//		initHandlerManager();
//		initEffect();
//	}
//
//	protected void initHandlerManager(){
//		mChangeStickerManagerThread = new HandlerThread("ChangeStickerManagerThread");
//		mChangeStickerManagerThread.start();
//		mChangeStickerManagerHandler = new Handler(mChangeStickerManagerThread.getLooper()) {
//			@Override
//			public void handleMessage(Message msg) {
//				switch (msg.what){
//					case MESSAGE_NEED_CHANGE_STICKER:
//						String sticker = (String) msg.obj;
//						mCurrentSticker = sticker;
//						int packageId1 = mSTMobileEffectNative.changePackage(mCurrentSticker);
//						if (packageId1 > 0) {
//							mLastBeautyOverlapCount = -1;
//							CollectionUtils.removeByValue(mCurrentStickerMaps, sticker);
//							mCurrentStickerMaps.put(packageId1, sticker);
//						}
//						updateHumanActionDetectConfig();
//						updateAnimalDetectConfig();
//						refreshDisplay();
//						break;
//					case MESSAGE_NEED_REMOVE_STICKER:
//						int packageId = (int) msg.obj;
//						int result = mSTMobileEffectNative.removeEffect(packageId);
//
//						if (mCurrentStickerMaps != null && result == 0) {
//							mCurrentStickerMaps.remove(packageId);
//						}
//						updateHumanActionDetectConfig();
//						break;
//					case MESSAGE_NEED_REMOVEALL_STICKERS:
//						if (mCurrentStickerMaps != null) {
//							mCurrentStickerMaps.clear();
//						}
//						mSTMobileEffectNative.clear();
////						if(mCurrentStickerMaps != null){
////							mCurrentStickerMaps.clear();
////						}
//						updateHumanActionDetectConfig();
//						refreshDisplay();
//						break;
//					case MESSAGE_NEED_ADD_STICKER:
//						String addSticker = (String) msg.obj;
//						mCurrentSticker = addSticker;
//						int stickerId = mSTMobileEffectNative.addPackage(mCurrentSticker);
//						if (stickerId > 0) {
//							if(mCurrentStickerMaps != null){
//								mCurrentStickerMaps.put(stickerId, mCurrentSticker);
//							}
//						} else {
//							Toast.makeText(SenseMeApplication.getContext(), "添加太多贴纸了", Toast.LENGTH_SHORT).show();
//						}
//						updateHumanActionDetectConfig();
//						updateAnimalDetectConfig();
//						refreshDisplay();
//						break;
//
//					default:
//						break;
//				}
//			}
//		};
//	}
//
////	public void updateBeautyParamsUI() {
////		int beautyOverlapCount = mSTMobileEffectNative.getOverlappedBeautyCount();
////		Log.d(TAG, "updateBeautyParamsUI: beautyOverlapCount:" + beautyOverlapCount);
////		mLastBeautyOverlapCount = beautyOverlapCount;
////		if(beautyOverlapCount > 0){
////			STEffectBeautyInfo[] beautyInfos = mSTMobileEffectNative.getOverlappedBeauty(beautyOverlapCount);
////			Log.d(TAG, "run: " + Arrays.toString(beautyInfos));
////			Log.d(TAG, "size: " +beautyInfos.length);
////			setBeautyParamsFromPackage(beautyInfos);
////
////			Message message1 = mHandler.obtainMessage(ImageActivity.MSG_NEED_UPDATE_BEAUTY_PARAMS);
////			mHandler.sendMessage(message1);
////		}  else {
////			mHandler.sendEmptyMessage(ImageActivity.MSG_NEED_RECOVERUI);
////		}
////	}
//
//	protected void initFaceAttribute() {
//		int result = mSTFaceAttributeNative.createInstance(FileUtils.getFaceAttributeModelPath(mContext));
//		LogUtils.i(TAG, "the result for createInstance for faceAttribute is %d", result);
//	}
//
//	protected void initCatFace(){
//		int result = mStAnimalNative.createInstance(FileUtils.getFilePath(mContext, FileUtils.MODEL_NAME_CATFACE_CORE), STCommon.ST_MOBILE_TRACKING_MULTI_THREAD);
//		LogUtils.i(TAG, "create animal handle result: %d", result);
//	}
//
//	protected void initAvatar(){
//		int result = mSTMobileAvatarNative.createInstanceFromAssetFile(FileUtils.MODEL_NAME_AVATAR_CORE, mContext.getAssets());
//		LogUtils.i(TAG, "create avatar handle result: %d", result);
//	}
//
//	protected void initHumanAction() {
//		//从sd读取model路径，创建handle
//		//int result = mSTHumanActionNative.createInstance(FileUtils.getTrackModelPath(mContext), mHumanActionCreateConfig);
//
//		//从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
//		int result = mSTHumanActionNative.createInstanceFromAssetFile(FileUtils.getActionModelName(), mHumanActionCreateConfig, mContext.getAssets());
//		LogUtils.i(TAG, "the result for createInstance for human action is %d", result);
//
//		if(result == 0){
//            result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAND, mContext.getAssets());
//            LogUtils.i(TAG, "add hand model result: %d", result);
//            result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_SEGMENT, mContext.getAssets());
//            LogUtils.i(TAG, "add figure segment model result: %d", result);
//
//			result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.getFaceExtraModelName(), mContext.getAssets());
//			LogUtils.i(TAG, "add face extra model result %d", result);
//
//			result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_EYEBALL_CONTOUR, mContext.getAssets());
//			LogUtils.i(TAG, "add eyeball contour model result: %d", result);
//
//			result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAIR, mContext.getAssets());
//			LogUtils.i(TAG,"add hair model result: %d", result );
//
//			result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_LIPS_PARSING, mContext.getAssets());
//			LogUtils.i(TAG,"add lips parsing model result: %d", result );
//
//			result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.HEAD_SEGMENT_MODEL_NAME, mContext.getAssets());
//			LogUtils.i(TAG,"add head segment model result: %d", result );
//
//			//for test avatar
//			if(mNeedAvatar){
//				int ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_HELP, mContext.getAssets());
//				LogUtils.i(TAG, "add avatar help model result: %d", ret);
//
////				ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_TONGUE, mContext.getAssets());
////				LogUtils.i(TAG,"add tongue model result: %d", ret );
//			}
//		}
//	}
//
//
//	protected void initEffect(){
//		int ret = mSTMobileEffectNative.createInstance(null, STMobileEffectNative.EFFECT_CONFIG_IMAGE_MODE);
//
//		for(int i = 0; i < mBeautifyParamsTypeBase.length; i++){
//			mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, (mBeautifyParamsTypeBase[i]));
//		}
//
//		for(int i = 0; i < mBeautifyParamsTypeProfessional.length; i++){
//			mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, (mBeautifyParamsTypeProfessional[i]));
//		}
//
//		for(int i = 0; i < mBeautifyParamsTypeMicro.length; i++){
//			mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, (mBeautifyParamsTypeMicro[i]));
//		}
//
//		for(int i = 0; i < mBeautifyParamsTypeAdjust.length; i++){
//			mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, (mBeautifyParamsTypeAdjust[i]));
//		}
//
//		for(int i= 0; i < Constants.MAKEUP_TYPE_COUNT; i++){
//			if(mCurrentMakeup[i] != null){
//				setMakeupForType(i, mCurrentMakeup[i]);
//				setStrengthForType(i, mMakeupStrength[i]);
//			}
//		}
//
//		updateHumanActionDetectConfig();
//	}
//
//	public void enableBeautify(boolean needBeautify) {
//		mNeedBeautify = needBeautify;
//	}
//
//	public void enableFaceAttribute(boolean needFaceAttribute) {
//		mNeedFaceAttribute = needFaceAttribute;
//		refreshDisplay();
//	}
//
//	protected String genFaceAttributeString(STFaceAttribute arrayFaceAttribute){
//		String attribute = null;
//		String gender = arrayFaceAttribute.getArrayAttribute()[2].getLabel();
//		if(gender.equals("male")){
//			gender = "男";
//		}else{
//			gender = "女";
//		}
//		attribute = "颜值:" + arrayFaceAttribute.getArrayAttribute()[1].getLabel() + " "
//				+ "性别:" + gender + " "
//				+ "年龄:"+arrayFaceAttribute.getArrayAttribute()[0].getLabel() + " ";
//		return attribute;
//	}
//
//	public void enableSticker(boolean needSticker){
//		mNeedSticker = needSticker;
//		if(!needSticker){
//			refreshDisplay();
//		}
//	}
//
//	public void enableFilter(boolean needFilter){
//		mNeedFilter = needFilter;
//		if(!needFilter){
//			refreshDisplay();
//		}
//	}
//
//	public void enableMakeUp(boolean needMakeup){
//		mNeedMakeup = needMakeup;
//		updateHumanActionDetectConfig();
//
//		if(needMakeup){
//			refreshDisplay();
//		}
//	}
//
//	public long getCostTime(){
//		return mFrameCostTime;
//	}
//
//	public String getFaceAttributeString() {
//		return mFaceAttribute;
//	}
//
//	public void changeSticker(String sticker) {
//		mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_CHANGE_STICKER);
//		Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_CHANGE_STICKER);
//		msg.obj = sticker;
//
//		mChangeStickerManagerHandler.sendMessage(msg);
//	}
//
//	public void addSticker(String addSticker) {
//		mCurrentSticker = addSticker;
//		//int stickerId = mSTMobileEffectNative.addPackage(mCurrentSticker);
//		Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_ADD_STICKER);
//		msg.obj = addSticker;
//		mChangeStickerManagerHandler.sendMessage(msg);
////		mCurrentSticker = addSticker;
////		int stickerId = mStStickerNative.addSticker(mCurrentSticker);
////
////		if(stickerId > 0){
////			if(mCurrentStickerMaps != null){
////				mCurrentStickerMaps.put(stickerId, mCurrentSticker);
////			}
////
////			refreshDisplay();
////
////			return stickerId;
////		}else {
////			Message message = mHandler.obtainMessage(ImageActivity.MSG_NEED_SHOW_TOO_MUCH_STICKER_TIPS);
////			mHandler.sendMessage(message);
////			return -1;
//		//}
//	}
//
//	public void removeSticker(String path) {
//		removeSticker(CollectionUtils.getKey(mCurrentStickerMaps, path));
//	}
//
//	public void removeSticker(int packageId) {
//		mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_REMOVE_STICKER);
//		Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_REMOVE_STICKER);
//		msg.obj = packageId;
//
//		mChangeStickerManagerHandler.sendMessage(msg);
////		int result = mStStickerNative.removeSticker(packageId);
////
////		if(mCurrentStickerMaps != null && result == 0){
////			mCurrentStickerMaps.remove(packageId);
////		}
//		refreshDisplay();
//	}
//
//	public void removeAllStickers() {
//		mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_REMOVEALL_STICKERS);
//		Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_REMOVEALL_STICKERS);
//
//		mChangeStickerManagerHandler.sendMessage(msg);
//	}
//
//	public void setFilterStyle(String filterType, String modelPath, String filterName) {
//		mFilterStyle = modelPath;
//
//		EffectInfoDataHelper.getInstance().filterStyle = modelPath;
//		EffectInfoDataHelper.getInstance().filterType = filterType;
//		EffectInfoDataHelper.getInstance().setFilterName(filterName);
//		mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, modelPath);
//		refreshDisplay();
//	}
//
//	public void setFilterStrength(float strength){
//		mFilterStrength = strength;
//		refreshDisplay();
//		EffectInfoDataHelper.getInstance().filterStrength = strength;
//	}
//
//	public void setBeautyParam(int index, float value) {
//		super.setBeautyParam(index, value);
//		refreshDisplay();
//	}
//
////	public float[] getBeautyParams(){
////		float[] values = new float[6];
////		for(int i = 0; i< mBeautifyParams.length; i++){
////			values[i] = mBeautifyParams[i];
////		}
////
////		return values;
////	}
//
//	public void enableSave(boolean save){
//		mNeedSave = save;
//		refreshDisplay();
//	}
//
//	public void setHandler(Handler handler){
//		mHandler = handler;
//	}
//
//	@Override
//	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//		GLES20.glDisable(GL10.GL_DITHER);
//        GLES20.glClearColor(0,0,0,0);
//        GLES20.glDisable(GL10.GL_CULL_FACE);
//
//        mImageInputRender.init();
//
//		if(mNeedSticker && mCurrentStickerMaps.size() == 0){
//			mSTMobileEffectNative.changePackage(mCurrentSticker);
//		}
//
//		if(mNeedSticker && mCurrentStickerMaps != null){
//			TreeMap<Integer, String> currentStickerMap = new TreeMap<>();
//
//			for (Integer index : mCurrentStickerMaps.keySet()) {
//				String sticker = mCurrentStickerMaps.get(index);//得到每个key多对用value的值
//
//				int packageId = mSTMobileEffectNative.addPackage(sticker);
//				currentStickerMap.put(packageId, sticker);
//
////				Message messageReplace = mHandler.obtainMessage(ImageActivity.MSG_NEED_REPLACE_STICKER_MAP);
////				messageReplace.arg1 = index;
////				messageReplace.arg2 = packageId;
////				mHandler.sendMessage(messageReplace);
//			}
//
//			mCurrentStickerMaps.clear();
//
//			Iterator<Integer> iter =  currentStickerMap.keySet().iterator();
//			while (iter.hasNext()) {
//				int key = iter.next();
//				mCurrentStickerMaps.put(key, currentStickerMap.get(key));
//			}
//		}
//
//		updateHumanActionDetectConfig();
//	}
//
//	@Override
//	public void onSurfaceChanged(GL10 gl, int width, int height) {
//		GLES20.glViewport(0, 0, width, height);
//		mDisplayWidth = width;
//		mDisplayHeight = height;
//		adjustImageDisplaySize();
//		mInitialized = true;
//	}
//
//	@Override
//	public void onDrawFrame(GL10 gl) {
//		long frameStartTime = System.currentTimeMillis();
//		if(!mInitialized)
//			return;
//		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//		int textureId = OpenGLUtils.NO_TEXTURE;
//
//		if(mOriginBitmap != null && mTextureId == OpenGLUtils.NO_TEXTURE){
//			mTextureId = OpenGLUtils.loadTexture(mOriginBitmap, OpenGLUtils.NO_TEXTURE);
//			textureId = mTextureId;
//		}else if(mTextureId != OpenGLUtils.NO_TEXTURE){
//			textureId = mTextureId;
//		}else{
//			return;
//		}
//
//		if (mBeautifyTextureId == null) {
//			mBeautifyTextureId = new int[1];
//			GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
//		}
//
//		if (mMakeupTextureId == null) {
//			mMakeupTextureId = new int[1];
//			GlUtil.initEffectTexture(mImageWidth, mImageHeight, mMakeupTextureId, GLES20.GL_TEXTURE_2D);
//		}
//
//		if (mTextureOutId == null) {
//			mTextureOutId = new int[1];
//			GlUtil.initEffectTexture(mImageWidth, mImageHeight, mTextureOutId, GLES20.GL_TEXTURE_2D);
//		}
//
//		STHumanAction humanAction = null;
//		if(mOriginBitmap != null) {
//			if(mTmpBuffer == null){
//				mTmpBuffer = STUtils.getBGRFromBitmap(mOriginBitmap);
//			}
//
//			if(!mShowOriginal && mSTMobileEffectNative != null){
//
//				STMobile106[] arrayFaces = null, arrayOutFaces = null;
//				int orientation = 0;
//
//				long humanActionCostTime = System.currentTimeMillis();
//				humanAction = mSTHumanActionNative.humanActionDetect(mTmpBuffer, STCommon.ST_PIX_FMT_BGR888,
//						mDetectConfig, orientation,
//						mImageWidth, mImageHeight);
//				LogUtils.i(TAG, "human action cost time: %d", System.currentTimeMillis() - humanActionCostTime);
//
//
//				if (mNeedAvatarExpression) {
//					if (humanAction != null && humanAction.getFaceInfos() != null) {
//						mSTMobileAvatarNative.avatarExpressionDetect(orientation, mImageWidth, mImageHeight, humanAction.getFaceInfos()[0], mAvatarExpression);
//						Log.d("avatarExpressionResult:", Arrays.toString(mAvatarExpression));
//					}
//				}
//
//				long catDetectStartTime = System.currentTimeMillis();
//				mAnimalFace = mStAnimalNative.animalDetect(mTmpBuffer, STCommon.ST_PIX_FMT_BGR888, orientation, mImageWidth, mImageHeight);
//				LogUtils.i(TAG, "cat face detect cost time: %d", System.currentTimeMillis() - catDetectStartTime);
//				animalFaceLlength = mAnimalFace == null ? 0 : mAnimalFace.length;
//
//				if (mNeedBeautify || mNeedFaceAttribute) {
//					if (humanAction != null) {
//						arrayFaces = humanAction.getMobileFaces();
//						if (arrayFaces != null && arrayFaces.length > 0) {
//							arrayOutFaces = new STMobile106[arrayFaces.length];
//						}
//					}
//				}
//				if (arrayFaces != null && arrayFaces.length != 0) {
//					if (mNeedFaceAttribute && arrayFaces != null && arrayFaces.length != 0) { // face attribute
//						STFaceAttribute[] arrayFaceAttribute = new STFaceAttribute[arrayFaces.length];
//						long attributeCostTime = System.currentTimeMillis();
//						int result = mSTFaceAttributeNative.detect(mTmpBuffer, STCommon.ST_PIX_FMT_BGR888, mImageWidth, mImageHeight, arrayFaces, arrayFaceAttribute);
//						LogUtils.i(TAG, "attribute cost time: %d", System.currentTimeMillis() - attributeCostTime);
//						if (result == 0) {
//							if (arrayFaceAttribute[0].getAttributeCount() > 0) {
//								mFaceAttribute = genFaceAttributeString(arrayFaceAttribute[0]);
//								mNeedFaceAttribute = false;
//							} else {
//								mFaceAttribute = "null";
//							}
//						}
//					}
//				}
//
//				if(mCurrentFilterStyle != mFilterStyle){
//					mCurrentFilterStyle = mFilterStyle;
//					mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStyle);
//				}
//				if(mCurrentFilterStrength != mFilterStrength){
//					mCurrentFilterStrength = mFilterStrength;
//					mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStrength);
//				}
//
//				STEffectTexture stEffectTexture = new STEffectTexture(textureId, mImageWidth, mImageHeight, 0);
//				STEffectTexture stEffectTextureOut = new STEffectTexture(mBeautifyTextureId[0], mImageWidth, mImageHeight, 0);
//
//				int event = mCustomEvent;
//				STEffectCustomParam customParam;
//				if(mSensorEvent != null && mSensorEvent.values != null && mSensorEvent.values.length > 0){
//					customParam = new STEffectCustomParam(new STQuaternion(mSensorEvent.values), false, event);
//				} else {
//					customParam = new STEffectCustomParam(new STQuaternion(0f,0f,0f,1f), false, event);
//				}
//
//				STAnimalFaceInfo animalFaceInfo = null;
//				if(mAnimalFace != null && mAnimalFace.length >0){
//					animalFaceInfo = new STAnimalFaceInfo(mAnimalFace, mAnimalFace.length);
//				}
//
//				STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(humanAction, animalFaceInfo, orientation, STRotateType.ST_CLOCKWISE_ROTATE_0, false, customParam, stEffectTexture, null);
//				STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, humanAction);
//				int result = mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);
//
//				if(stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null){
//					textureId = stEffectRenderOutParam.getTexture().getId();
//				}
//
//				if(event == mCustomEvent){
//					mCustomEvent = 0;
//				}
//
//				GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
//
//				mImageInputRender.onDrawFrame(textureId,mVertexBuffer,mTextureBuffer);
//			} else {
//				mImageInputRender.onDisplaySizeChanged(mDisplayWidth,mDisplayHeight);
//				mImageInputRender.onDrawFrame(mTextureId,mVertexBuffer,mTextureBuffer);
//			}
//			GLES20.glFinish();
//		}
//
//		mFrameCostTime = System.currentTimeMillis() - frameStartTime;
//		LogUtils.i(TAG, "image onDrawFrame, the time for frame process is " + (System.currentTimeMillis() - frameStartTime));
//
//		if (mCostListener != null) {
//			mCostListener.onCostChanged((int)mFrameCostTime);
//		}
//		if(mNeedSave){
//			textureToBitmap(textureId);
//			mNeedSave =false;
//		}
//	}
//
//	public void setImageBitmap(Bitmap bitmap) {
//		if (bitmap == null || bitmap.isRecycled())
//			return;
//		mImageWidth = bitmap.getWidth();
//		mImageHeight = bitmap.getHeight();
//		mOriginBitmap = bitmap;
//		adjustImageDisplaySize();
//		refreshDisplay();
//	}
//
//	public void setShowOriginal(boolean isShow)
//	{
//		mShowOriginal = isShow;
//		refreshDisplay();
//	}
//
//	protected void refreshDisplay(){
//		//deleteTextures();
//		mGlSurfaceView.requestRender();
//		updateHumanActionDetectConfig();
//	}
//
//	public void onResume(){
//		mGlSurfaceView.onResume();
//
////		if(mNeedSticker || mNeedFilter){
////			mStStickerNative.changeSticker(mCurrentSticker);
////			mCurrentFilterStyle = null;
////		}
//
//		if(mNeedFilter){
//			mCurrentFilterStyle = null;
//		}
//	}
//
//	public void onPause(){
//		//mCurrentSticker = null;
//
//		mGlSurfaceView.queueEvent(new Runnable() {
//			@Override
//			public void run() {
//				deleteTextures();
//			}
//		});
//
//		mGlSurfaceView.onPause();
//	}
//
//	public void onDestroy(){
//		mSTHumanActionNative.destroyInstance();
//		mSTFaceAttributeNative.destroyInstance();
//		if(mEGLContextHelper != null){
//			mEGLContextHelper.eglMakeCurrent();
//			mSTMobileEffectNative.destroyInstance();
//			mEGLContextHelper.eglMakeNoCurrent();
//
//			mEGLContextHelper.release();
//			mEGLContextHelper = null;
//		}
//		mStAnimalNative.destroyInstance();
//		mChangeStickerManagerThread.quitSafely();
//	}
//
//	protected void adjustImageDisplaySize() {
//		float ratio1 = (float)mDisplayWidth / mImageWidth;
//        float ratio2 = (float)mDisplayHeight / mImageHeight;
//        float ratioMax = Math.max(ratio1, ratio2);
//        int imageWidthNew = Math.round(mImageWidth * ratioMax);
//        int imageHeightNew = Math.round(mImageHeight * ratioMax);
//
//        float ratioWidth = imageWidthNew / (float)mDisplayWidth;
//        float ratioHeight = imageHeightNew / (float)mDisplayHeight;
//
//        float[] cube = new float[]{
//        		TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
//        		TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
//        		TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
//        		TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
//        };
//        mVertexBuffer.clear();
//        mVertexBuffer.put(cube).position(0);
//    }
//
//	protected void textureToBitmap(int textureId){
//		ByteBuffer mTmpBuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
//
//		int[] mFrameBuffers = new int[1];
//		if(textureId != OpenGLUtils.NO_TEXTURE) {
//			GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
//			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
//			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,textureId, 0);
//		}
//		GLES20.glReadPixels(0, 0, mImageWidth, mImageHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mTmpBuffer);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//		mProcessedImage = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
//		mProcessedImage.copyPixelsFromBuffer(mTmpBuffer);
//
////		mProcessedImage = STUtils.getBitmapFromRGBA(mTmpBuffer.array(),mImageWidth,mImageHeight);
//
////		Message msg = Message.obtain(mHandler);
////		msg.what = ImageActivity.MSG_SAVING_IMG;
////		msg.sendToTarget();
//	}
//
//	public Bitmap getBitmap(){
//		return mProcessedImage;
//	}
//
//	protected void deleteTextures() {
//		if(mTextureId != OpenGLUtils.NO_TEXTURE)
//			mGlSurfaceView.queueEvent(new Runnable() {
//
//				@Override
//				public void run() {
//	                GLES20.glDeleteTextures(1, new int[]{
//	                        mTextureId
//	                }, 0);
//	                mTextureId = OpenGLUtils.NO_TEXTURE;
//
//					if (mBeautifyTextureId != null) {
//						GLES20.glDeleteTextures(1, mBeautifyTextureId, 0);
//						mBeautifyTextureId = null;
//					}
//
//					if (mMakeupTextureId != null) {
//						GLES20.glDeleteTextures(1, mMakeupTextureId, 0);
//						mMakeupTextureId = null;
//					}
//
//					if (mTextureOutId != null) {
//						GLES20.glDeleteTextures(1, mTextureOutId, 0);
//						mTextureOutId = null;
//					}
//
//					if(mFilterTextureOutId != null){
//						GLES20.glDeleteTextures(1, mFilterTextureOutId, 0);
//						mFilterTextureOutId = null;
//					}
//	            }
//	        });
//    }
//
////	public void setNeed240(boolean need240){
////		this.mNeed240 = need240;
////	}
//
//    public interface CostChangeListener {
//		void onCostChanged(int value);
//	}
//
//	public void setCostChangeListener(CostChangeListener listener) {
//		mCostListener = listener;
//	}
//
//	public void changeCustomEvent(){
//		mCustomEvent = STCustomEvent.ST_CUSTOM_EVENT_1 | STCustomEvent.ST_CUSTOM_EVENT_2;
//	}
//
//	public void setSensorEvent(SensorEvent event){
//		mSensorEvent =  event;
//	}
//
//	public void setMakeupForType(int type, String typePath){
//		mCurrentMakeup[type] = typePath;
//		EffectInfoDataHelper.getInstance().setCurrentMakeup(mCurrentMakeup);
//		mSTMobileEffectNative.setBeauty(convertMakeupTypeToNewType(type), typePath);
//
//		updateHumanActionDetectConfig();
//		refreshDisplay();
//
//	}
//
//	public String mMakeupPath;
//
//	public void setMakeupForTypeFromAssets(int type, String typePath){
//		mMakeupPath = typePath;
//		int ret = mSTMobileEffectNative.setBeautyFromAssetsFile(402, typePath, mContext.getAssets());
//		updateHumanActionDetectConfig();
//		refreshDisplay();
//	}
//
//	public void removeMakeupByType(int type){
//		int ret = mSTMobileEffectNative.setBeauty(convertMakeupTypeToNewType(type), null);
//
//		if(ret == 0){
//			mCurrentMakeup[type] = null;
//			refreshDisplay();
//		}
//	}
//
//	public void setStrengthForType(int type, float strength){
//
//		if (convertMakeupTypeToNewType(type) == STEffectBeautyType.EFFECT_BEAUTY_HAIR_DYE) {
//			strength = strength * MAKEUP_HAIRDYE_STRENGTH_RATIO;
//		}
//		int ret = mSTMobileEffectNative.setBeautyStrength(convertMakeupTypeToNewType(type), strength);
//		mMakeupStrength[type] = strength;
//		EffectInfoDataHelper.getInstance().currentMakeupStrength = mMakeupStrength;
//		refreshDisplay();
//	}
//
//	public void setBeautyParamsFromPackage(STEffectBeautyInfo[] beautyInfos){
//		boolean needResetBeautyParamsBase = false;
//		boolean needResetBeautyParamsProfessional = false;
//		boolean needResetBeautyParamsMicro = false;
//		boolean needResetBeautyParamsAdjust = false;
//
//		boolean needResetMakeup = false;
//		boolean needResetFilter = false;
//
//		for (int i = 0; i < beautyInfos.length; i++) {
//			if (beautyInfos[i] == null) continue;
//			if ((beautyInfos[i].getType() / 100) == 1) {
//				needResetBeautyParamsBase = false;
//			} else if ((beautyInfos[i].getType() / 100) == 2) {
//				needResetBeautyParamsProfessional = false;
//			} else if ((beautyInfos[i].getType() / 100) == 3) {
//				needResetBeautyParamsMicro = false;
//			} else if ((beautyInfos[i].getType() / 100) == 6) {
//				needResetBeautyParamsAdjust = false;
//			} else if ((beautyInfos[i].getType()/* / 100*/) == 410) {
//				needResetMakeup = true;
//				mHandler.sendEmptyMessage(CameraActivity.MSG_STICKER_HAS_MAKEUP);
//			} else if ((int) (beautyInfos[i].getType() / 100) == 5) {
//				needResetFilter = true;
//			}
//		}
//
//		mNeedClearMakeupView = needResetMakeup;
//		mNeedClearFilterView = needResetFilter;
//
//		if(needResetBeautyParamsBase){
//			mBeautifyParamsTypeBase = new float[mBeautifyParamsTypeBase.length];
//		}
//		if(needResetBeautyParamsProfessional){
//			mBeautifyParamsTypeProfessional = new float[mBeautifyParamsTypeProfessional.length];
//		}
//		if(needResetBeautyParamsMicro){
//			mBeautifyParamsTypeMicro = new float[mBeautifyParamsTypeMicro.length];
//		}
//		if(needResetBeautyParamsAdjust){
//			mBeautifyParamsTypeAdjust = new float[mBeautifyParamsTypeAdjust.length];
//		}
//
//		for (STEffectBeautyInfo beautyInfo : beautyInfos) {
//			if ((beautyInfo.getType() / 100) == 1) {
//				//mBeautifyParamsTypeBase[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
//			} else if ((beautyInfo.getType() / 100) == 2) {
//				mBeautifyParamsTypeProfessional[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
//			} else if ((beautyInfo.getType() / 100) == 3) {
//				mBeautifyParamsTypeMicro[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
//			} else if ((beautyInfo.getType() / 100) == 6) {
//				mBeautifyParamsTypeAdjust[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
//			}
//		}
//
//		if (STUtils.containsBaseParams(beautyInfos)) {
//			mBeautifyParamsTypeBase = STUtils.getBeautifyParamsTypeBase(mBeautifyParamsTypeBase, beautyInfos);
//		}
//	}
//
//	public boolean overlappedBeautyCountChanged() {
//		int overlappedBeautyCount = mSTMobileEffectNative.getOverlappedBeautyCount();
//		return mLastBeautyOverlapCount != overlappedBeautyCount;
//	}
//
//	public boolean stickerMapIsEmpty() {
//		boolean b = mCurrentStickerMaps == null || mCurrentStickerMaps.size() == 0;
//		if (b) mLastBeautyOverlapCount = -1;
//		return b;
//	}
//}
