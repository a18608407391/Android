package com.elder.zcommonmodule.Utils

import android.support.v4.app.ActivityOptionsCompat
import android.view.View


fun getScaleUpAnimation(root_layout: View): ActivityOptionsCompat {
   return ActivityOptionsCompat.makeScaleUpAnimation(root_layout, root_layout.getWidth() / 2, root_layout.getHeight() / 2, 0, 0)
}