package com.fixedit.fixitadmin.Utils;

public interface ActionCompletionContract {
    void onViewMoved(int oldPosition, int newPosition);

    void onViewSwiped(int position);
}
