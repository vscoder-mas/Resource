//package sensetime.senseme.com.effects.activity;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.media.MediaMetadataRetriever;
//import android.media.MediaPlayer;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
//import android.opengl.GLSurfaceView;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.IOException;
//
//import sensetime.senseme.com.effects.R;
//import sensetime.senseme.com.effects.display.VideoPreviewDisplay;
//import sensetime.senseme.com.effects.encoder.mediacodec.VideoProcessor;
//import sensetime.senseme.com.effects.utils.FileUtils;
//import sensetime.senseme.com.effects.utils.MultiLanguageUtils;
//
//public class VideoActivity extends ImageActivity{
//    private final static String TAG = "VideoActivity";
//    private String mVideoPath = null;
//    private String mOutputPath;
//
//    private TextView mVideoPlayerBtn, mVideoProcessBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected void initDisplay() {
//        initVideoView();
//
//        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.id_gl_sv);
//        mImageDisplay = new VideoPreviewDisplay(getApplicationContext(), glSurfaceView, mHandler, mVideoPath);
//
//        mImageBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_face);
//        mImageDisplay.setImageBitmap(mImageBitmap);
//    }
//
//    private boolean mIsProcessing = false;
//    private void initVideoView(){
////        findViewById(R.id.tv_play_video).setVisibility(View.VISIBLE);
//        findViewById(R.id.tv_process_video).setVisibility(View.VISIBLE);
//        findViewById(R.id.tv_capture).setVisibility(View.INVISIBLE);
//
//        mVideoPath = this.getIntent().getStringExtra("VideoPath");
//
//        if(mVideoPath == null || !mVideoPath.endsWith(".mp4")){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "请选择mp4格式视频文件！", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            finish();
//        }
//
//        mOutputPath = mVideoPath;
//        mVideoPlayerBtn = (TextView) findViewById(R.id.tv_play_video);
//        mVideoPlayerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(mContext, VideoPlayActivity.class);
//
//                intent.putExtra("VideoPath", mOutputPath);
//                mContext.startActivity(intent);
//            }
//        });
//
//        mVideoProcessBtn = (TextView) findViewById(R.id.tv_process_video);
//        mVideoProcessBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(mContext,MultiLanguageUtils.getStr(R.string.toast_start_video), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                mIsProcessing = true;
//                mVideoProcessBtn.setTextColor(Color.parseColor("#6f6f6f"));
//                mVideoProcessBtn.setClickable(false);
//                processVideoAndSave();
//            }
//        });
//
//        mStartVideoPlayer = (TextView)findViewById(R.id.tv_video_player_start);
//        mStartVideoPlayer.setVisibility(View.VISIBLE);
//        mStartVideoPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!mIsPreviewing){
//                    ((VideoPreviewDisplay)mImageDisplay).StartPlayVideo();
//                    mStartVideoPlayer.setText(MultiLanguageUtils.getStr(R.string.video_pause));
//                    mIsPreviewing = true;
//                }else {
//                    ((VideoPreviewDisplay)mImageDisplay).pauseVideo();
//                    mStartVideoPlayer.setText(MultiLanguageUtils.getStr(R.string.video_preview));
//                    mIsPreviewing = false;
//                }
//            }
//        });
//    }
//
//
//    private boolean mIsPreviewing = false;
//
//    private TextView mStartVideoPlayer;
//    private MediaPlayer mMediaPlayer;
//    private VideoProcessor mVideoProcessor;
//    private void processVideoAndSave(){
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setLooping(true);
//        try {
//            mMediaPlayer.setDataSource(mVideoPath);
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        mMediaPlayer.prepareAsync();
//
//        try {
//            Thread.sleep(100);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //int videoDuration = mMediaPlayer.getDuration();
//
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(mVideoPath);
//        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        if(duration == null){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(mContext,"读取视频信息失败，请重新选择！", Toast.LENGTH_SHORT).show();
//                    mIsPreviewing = false;
//                    mVideoProcessBtn.setTextColor(Color.parseColor("#000000"));
//                    mVideoProcessBtn.setClickable(true);
//                }
//            });
//            return;
//        }
//
//        int videoDuration = Integer.parseInt(duration);
//        if(videoDuration < 1 * 1000){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mIsPreviewing = false;
//                    mVideoProcessBtn.setTextColor(Color.parseColor("#000000"));
//                    Toast.makeText(mContext, MultiLanguageUtils.getStr(R.string.toast_short_video), Toast.LENGTH_SHORT).show();
//                    mVideoProcessBtn.setClickable(true);
//                }
//            });
//
//            return;
//        }else if(videoDuration > 180 * 1000){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(mContext,"视频太长，只处理前180秒！", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            videoDuration = 180 * 1000;
//        }
//
//        videoDuration = videoDuration - 100;
//
//        mVideoProcessor = new VideoProcessor(mContext);
////        mVideoProcessor.setBodyModelName(mBodyModelName);
//        mVideoProcessor.setInputVideoPath(mVideoPath);
////        mVideoProcessor.set3DMeshStrenth(m3dMeshStrenth);
//        mOutputPath = FileUtils.getPath(mContext, "/Camera/", System.currentTimeMillis() + ".mp4");
//        mVideoProcessor.setOutputVideoPath(mOutputPath);
//        mVideoProcessor.setDetectConfig(((VideoPreviewDisplay)mImageDisplay).getHumanActionDetectConfig());
//        mVideoProcessor.setRenderOptions(((VideoPreviewDisplay)mImageDisplay).ifNeedBeauty(), ((VideoPreviewDisplay)mImageDisplay).ifNeedNakeup(),
//                ((VideoPreviewDisplay)mImageDisplay).ifNeedSticker(), ((VideoPreviewDisplay)mImageDisplay).ifNeedFilter());
//        mVideoProcessor.setCurrentBeautyParams(((VideoPreviewDisplay)mImageDisplay).getBeautifyParamsTypeBase(), ((VideoPreviewDisplay)mImageDisplay).getBeautifyParamsTypeProfessional(), ((VideoPreviewDisplay)mImageDisplay).getBeautifyParamsTypeMicro(), ((VideoPreviewDisplay)mImageDisplay).getBeautifyParamsTypeAdjust());
//        mVideoProcessor.setCurrentMakeups(((VideoPreviewDisplay)mImageDisplay).getCurrentMakeupPaths(), ((VideoPreviewDisplay)mImageDisplay).getCurrentMakeupStrengths());
//        mVideoProcessor.setCurrentSticker(((VideoPreviewDisplay)mImageDisplay).getCurrentStickerPath());
//        mVideoProcessor.setCurrentFilter(((VideoPreviewDisplay)mImageDisplay).getCurrentFilterPath(), ((VideoPreviewDisplay)mImageDisplay).getCurrentFilterStrength());
//
////        mIsProcessingTV.setText("正在初始化编解码器...");
////        mIsProcessingVideo = true;
//        mVideoProcessor.setOnVideoCutFinishListener(new VideoProcessor.OnVideoProcessListener() {
//            @Override
//            public void onFinish() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mIsPreviewing = false;
//                        mVideoProcessBtn.setTextColor(Color.parseColor("#000000"));
//                        mVideoProcessBtn.setClickable(true);
//
//                        Toast.makeText(mContext,MultiLanguageUtils.getStr(R.string.toast_video_done), Toast.LENGTH_SHORT).show();
////                        mIsProcessingVideo = false;
//
//                        if(mOutputPath != null){
//                            File mediaFile = new File(mOutputPath);
//                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                            Uri contentUri = Uri.fromFile(mediaFile);
//                            mediaScanIntent.setData(contentUri);
//                            mContext.sendBroadcast(mediaScanIntent);
//
//                            if (Build.VERSION.SDK_INT >= 19) {
//                                MediaScannerConnection.scanFile(mContext, new String[]{mOutputPath}, null, null);
//                            }
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailed() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mIsPreviewing = false;
//                        mVideoProcessBtn.setTextColor(Color.parseColor("#000000"));
//                        mVideoProcessBtn.setClickable(true);
//
//                        mVideoPlayerBtn.setTextColor(Color.parseColor("#ffffff"));
//
//                        Toast.makeText(mContext,"视频编解码失败！", Toast.LENGTH_SHORT).show();
//
////                        mIsProcessingVideo = false;
//                    }
//                });
//            }
//
//            @Override
//            public void onCanceled() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mIsPreviewing = false;
//                        mVideoProcessBtn.setTextColor(Color.parseColor("#000000"));
//                        mVideoProcessBtn.setClickable(true);
//
//                        Toast.makeText(mContext,MultiLanguageUtils.getStr(R.string.toast_video_error), Toast.LENGTH_SHORT).show();
//
////                        mIsProcessingVideo = false;
//                    }
//                });
//            }
//        });
//
//        mVideoProcessor.setOnFrameCallbackListener(new VideoProcessor.FrameCallbackListener() {
//            @Override
//            public void onFrameProgress(final float progress) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        float prog = Math.round(progress*100)/100;
//                        if(prog > 100){
//                            prog = 100;
//                        }
////                        mIsProcessingTV.setText("正在处理视频："+ prog +"%");
//                    }
//                });
//            }
//        });
//
//        try {
//            mVideoProcessor.processVideo(0,videoDuration * 1000);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
