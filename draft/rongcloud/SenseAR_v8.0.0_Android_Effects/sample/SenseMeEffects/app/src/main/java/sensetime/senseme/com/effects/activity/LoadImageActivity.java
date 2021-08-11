package sensetime.senseme.com.effects.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import sensetime.senseme.com.effects.R;
import sensetime.senseme.com.effects.adapter.MediaAdapter;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.FileUtils;
import sensetime.senseme.com.effects.utils.MultiLanguageUtils;
import sensetime.senseme.com.effects.utils.STUtils;
import sensetime.senseme.com.effects.view.MediaItem;

public class LoadImageActivity extends Activity {

    private Context mContext;

    private final int REQUEST_PICK_IMAGE_GALLERY = 100;
    private final int REQUEST_PICK_IMAGE_TAKE_PHOTO = 101;
    private final int REQUEST_PICK_VIDEO = 102;

    public static final int MODE_GALLERY_IMAGE = 2;
    public static final int MODE_TAKE_PHOTO = 3;

    private TextView mBackButton;
    private RecyclerView mMediaOptionsRecycleView;
    private final HashMap<String, ArrayList<MediaItem>> mMediaLists = new HashMap<>();
    private final HashMap<String, MediaAdapter> mMediaAdapters = new HashMap<>();

    private static final String PICTURE_PATH = Environment.getExternalStorageDirectory() + "/DCIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Constants.ACTIVITY_MODE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_load_image);

        mContext = this;

        //fix sdk version >23 camera crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        initView();
    }

    private void initView() {
        TextView mSelectionVideo = (TextView) findViewById(R.id.tv_back_to_video);
        mSelectionVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            }
        });

        ConstraintLayout mGalleryButton = (ConstraintLayout) findViewById(R.id.ll_from_gallery);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.ACTIVITY_MODE_FOR_TV) {
                    mMediaOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    mMediaOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
                    mMediaOptionsRecycleView.setVisibility(View.VISIBLE);
                    mMediaOptionsRecycleView.setAdapter(mMediaAdapters.get("image"));
                    if (mMediaAdapters != null && mMediaAdapters.get("image") != null) {
                        mMediaAdapters.get("image").setSelectedPosition(-1);
                        mMediaAdapters.get("image").notifyDataSetChanged();
                    }
                    mBackButton.setVisibility(View.VISIBLE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE_GALLERY);
                }

            }
        });

        ConstraintLayout mTakePictureButton = (ConstraintLayout) findViewById(R.id.ll_from_camera);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = Uri.fromFile(new File(PICTURE_PATH, "sensetime.jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_PICK_IMAGE_TAKE_PHOTO);
            }
        });

        ConstraintLayout mLocalVideoButton = (ConstraintLayout) findViewById(R.id.ll_from_video);
        mLocalVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.ACTIVITY_MODE_FOR_TV) {
                    mMediaOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    mMediaOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
                    mMediaOptionsRecycleView.setVisibility(View.VISIBLE);
                    mMediaOptionsRecycleView.setAdapter(mMediaAdapters.get("video"));

                    if (mMediaAdapters.get("video") != null) {
                        mMediaAdapters.get("video").setSelectedPosition(-1);
                        mMediaAdapters.get("video").notifyDataSetChanged();
                    }

                    mBackButton.setVisibility(View.VISIBLE);
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, REQUEST_PICK_VIDEO);
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "选择视频出错，请重新选择！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        if (Constants.ACTIVITY_MODE_FOR_TV) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initSelectView();
                }
            }).start();

            mTakePictureButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        startImageActivity(MODE_GALLERY_IMAGE, -1, uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_PICK_IMAGE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.fromFile(new File(PICTURE_PATH + "/sensetime.jpg"));
                    startImageActivity(MODE_TAKE_PHOTO, -1, uri);
                }
                break;

            case REQUEST_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        // 视频路径
                        String videoPath = FileUtils.getPath(mContext, uri);
                        openVideoActivity(videoPath);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "选择视频出错，请重新选择！", Toast.LENGTH_SHORT).show();
                    }
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void openVideoActivity(String videoPath) {
        if (videoPath != null) {
            Intent intent = new Intent(this.getApplicationContext(), VideoActivity.class);
            intent.putExtra("VideoPath", videoPath);
            this.startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "选择视频出错，请重新选择！", Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageActivity(int mode, int drawableIndex, Uri uri) {
        Intent intent = new Intent(this.getApplicationContext(), ImageActivity.class);

        Bundle data = new Bundle();
        data.putInt("mode", mode);
        data.putInt("drawableIndex", drawableIndex);

        intent.putExtra("bundle", data);
        intent.putExtra("imageUri", uri);
        this.startActivity(intent);
    }

    // 分隔间距 继承RecyclerView.ItemDecoration
    static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = space;
            }
        }
    }

    private void initSelectView() {
        ArrayList<MediaItem> videoList = new ArrayList<>();
        ArrayList<MediaItem> imageList = new ArrayList<>();

        mMediaOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_media_file_icons);
        mMediaOptionsRecycleView.setVisibility(View.INVISIBLE);

        STUtils.getAllFiles(new File(Environment.getExternalStorageDirectory().getPath()), videoList, imageList);

        mMediaLists.put("video", videoList);
        mMediaLists.put("image", imageList);

        mMediaAdapters.put("video", new MediaAdapter(mMediaLists.get("video"), this));
        mMediaAdapters.put("image", new MediaAdapter(mMediaLists.get("image"), this));


        mBackButton = (TextView) findViewById(R.id.tv_media_cancel);
        mBackButton.setVisibility(View.INVISIBLE);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaOptionsRecycleView.setVisibility(View.INVISIBLE);
                mBackButton.setVisibility(View.INVISIBLE);
            }
        });

        mMediaAdapters.get("video").setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());
                mMediaAdapters.get("video").setSelectedPosition(position);

                mMediaAdapters.get("video").notifyDataSetChanged();

                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra("VideoPath", mMediaLists.get("video").get(position).name);
                mContext.startActivity(intent);
                mMediaOptionsRecycleView.setVisibility(View.INVISIBLE);
                mBackButton.setVisibility(View.INVISIBLE);
            }
        });

        mMediaAdapters.get("image").setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());
                mMediaAdapters.get("image").setSelectedPosition(position);

                mMediaAdapters.get("image").notifyDataSetChanged();

                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("ImagePath", mMediaLists.get("image").get(position).name);
                mContext.startActivity(intent);
                mMediaOptionsRecycleView.setVisibility(View.INVISIBLE);
                mBackButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtils.appContext);
    }
}
