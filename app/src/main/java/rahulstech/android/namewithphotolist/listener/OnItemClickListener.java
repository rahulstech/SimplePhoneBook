package rahulstech.android.namewithphotolist.listener;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener {

    void onClickItem(@NonNull RecyclerView rv, @NonNull View itemView, int adapterPosition);
}
