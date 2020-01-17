package org.cs.tec.library.Weidge

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View


class CustomRecycleView @JvmOverloads constructor(context:Context,attr:AttributeSet,i:Int) :RecyclerView(context,attr,i){

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
    }

}