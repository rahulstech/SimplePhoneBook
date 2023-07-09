package rahulstech.android.namewithphotolist.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ItemClickHelper extends RecyclerView.SimpleOnItemTouchListener {

    private RecyclerView mRecyclerView;
    private GestureDetectorCompat mGestureDetector;

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            View child = mRecyclerView.findChildViewUnder(x,y);
            if (null == child) return false;
            int adapterPosition = mRecyclerView.getChildAdapterPosition(child);
            handleItemClick(mRecyclerView,child,adapterPosition);
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    };

    private OnItemClickListener mItemClickListener;

    public ItemClickHelper(@NonNull Context context, @NonNull RecyclerView rv) {
        Objects.requireNonNull(context,"context == null");
        Objects.requireNonNull(rv,"recycler view == null");
        this.mRecyclerView = rv;
        mGestureDetector = new GestureDetectorCompat(context,mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return true;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void handleItemClick(RecyclerView rv, View itemView, int adapterPosition) {
        if (null != mItemClickListener) {
            mItemClickListener.onClickItem(rv,itemView,adapterPosition);
        }
    }
}
