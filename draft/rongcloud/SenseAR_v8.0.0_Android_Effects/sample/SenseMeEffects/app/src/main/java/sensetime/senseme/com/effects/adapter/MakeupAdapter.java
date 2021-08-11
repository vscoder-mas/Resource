package sensetime.senseme.com.effects.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sensetime.senseme.com.effects.R;
import sensetime.senseme.com.effects.view.MakeupItem;
import sensetime.senseme.com.effects.view.RoundImageView;

public class MakeupAdapter extends RecyclerView.Adapter {

    List<MakeupItem> mMakeupList;
    private View.OnClickListener mOnClickMakeupListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public MakeupAdapter(List<MakeupItem> list, Context context) {
        mMakeupList = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.makeup_item, null);
        return new MakeupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MakeupViewHolder viewHolder = (MakeupViewHolder) holder;
        viewHolder.imageView.setNeedBorder(false);
        viewHolder.imageView.setImageBitmap(mMakeupList.get(position).icon);
        viewHolder.textView.setText(mMakeupList.get(position).name);
        viewHolder.textView.setTextColor(Color.parseColor("#ffffff"));

        bindState(getItem(position), viewHolder, position);
        holder.itemView.setSelected(mSelectedPosition == position);

        if(mSelectedPosition == position){
            viewHolder.imageView.setNeedBorder(true);
        }

        if(mOnClickMakeupListener != null) {
            holder.itemView.setTag(position);

            holder.itemView.setOnClickListener(mOnClickMakeupListener);
//            holder.itemView.setSelected(mSelectedPosition == position);
        }
    }

    /**
     * loading 状态绑定
     *
     * @param stickerItem
     * @param holder
     * @param position
     */
    private void bindState(MakeupItem stickerItem, MakeupViewHolder holder, int position) {
        if (stickerItem != null) {
            switch (stickerItem.state) {
                case NORMAL_STATE:
                    //设置为等待下载状态
                    if (holder.normalState.getVisibility() != View.VISIBLE) {
                        Log.i("StickerAdapter", "NORMAL_STATE");
                        holder.normalState.setVisibility(View.VISIBLE);
                        holder.downloadingState.setVisibility((View.INVISIBLE));
                        holder.downloadingState.setActivated(false);
                        holder.loadingStateParent.setVisibility((View.INVISIBLE));
                    }
                    break;
                case LOADING_STATE:
                    //设置为loading 状态
                    if (holder.downloadingState.getVisibility() != View.VISIBLE) {
                        Log.i("StickerAdapter", "LOADING_STATE");
                        holder.normalState.setVisibility(View.INVISIBLE);
                        holder.downloadingState.setActivated(true);
                        holder.downloadingState.setVisibility((View.VISIBLE));
                        holder.loadingStateParent.setVisibility((View.VISIBLE));
                    }

                    break;
                case DONE_STATE:
                    //设置为下载完成状态
                    if (holder.normalState.getVisibility() != View.INVISIBLE || holder.downloadingState.getVisibility() != View.INVISIBLE) {
                        Log.i("StickerAdapter", "DONE_STATE");
                        holder.normalState.setVisibility(View.INVISIBLE);
                        holder.downloadingState.setVisibility((View.INVISIBLE));
                        holder.downloadingState.setActivated(false);
                        holder.loadingStateParent.setVisibility((View.INVISIBLE));
                    }

                    break;
            }
        }
    }

    public void setClickMakeupListener(View.OnClickListener listener) {
        mOnClickMakeupListener = listener;
    }

    public MakeupItem getItem(int position) {
        if (position >= 0 && position < getItemCount()) {
            return mMakeupList.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mMakeupList.size();
    }

    static class MakeupViewHolder extends ViewHolder {

        View view;
        RoundImageView imageView;
        TextView textView;
        ImageView normalState;
        ImageView downloadingState;
        ViewGroup loadingStateParent;

        public MakeupViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (RoundImageView) itemView.findViewById(R.id.iv_makeup_image);
            textView = (TextView) itemView.findViewById(R.id.makeup_text);

            normalState = (ImageView) itemView.findViewById(R.id.normalState);
            downloadingState = (ImageView) itemView.findViewById(R.id.downloadingState);
            loadingStateParent = (ViewGroup) itemView.findViewById(R.id.loadingStateParent);
        }
    }

    public List<MakeupItem> getData() {
        return mMakeupList;
    }

    public void setSelectedPosition(int position){
        mSelectedPosition = position;
    }
}
