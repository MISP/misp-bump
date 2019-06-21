package lu.circl.mispbump.interfaces;

import android.view.View;

public interface OnRecyclerItemClickListener<T> {
    void onClick(View v, T item);
}
