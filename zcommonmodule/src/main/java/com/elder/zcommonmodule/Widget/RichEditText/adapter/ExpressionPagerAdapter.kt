/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elder.zcommonmodule.Widget.RichEditText.adapter

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup

open class ExpressionPagerAdapter(private val views: List<View>) : PagerAdapter() {

    override fun getCount(): Int = views.size

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean = arg0 === arg1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        (container as ViewPager).addView(views[position])
        return  views[position]
    }
//    override fun instantiateItem(arg0: View?, arg1: Int): Any {
//        (arg0 as ViewPager).addView(views[arg1])
//        return views[arg1]
//    }
//
//    override fun destroyItem(arg0: View?, arg1: Int, arg2: Any?) {
//        (arg0 as ViewPager).removeView(views[arg1])
//    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(views[position])
    }
}
