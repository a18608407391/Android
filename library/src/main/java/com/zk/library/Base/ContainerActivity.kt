package com.zk.library.Base
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View.generateViewId
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import android.databinding.ViewDataBinding


class ContainerActivity : RxAppCompatActivity() {
    lateinit var mFragment: WeakReference<Fragment>
    lateinit var mContainer: ViewGroup

    companion object {
        var FRAGMENT: String = "fragment"
        var BUNDLE: String = "bundle"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        mContainer = LinearLayout(this)
        mContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        mContainer.id = generateViewId()
        setContentView(mContainer)
        var fragment: Fragment = supportFragmentManager.findFragmentById(mContainer.id)!!
        if (fragment == null) {
            initFragment(intent)
        }
    }

    fun initFragment(intent: Intent?) {
        if (intent == null) {
            throw  RuntimeException("you must provide a page info to display")
        }
        try {
            val fragmentName = intent.getStringExtra(FRAGMENT)
            if (fragmentName == null || "" == fragmentName) {
                throw IllegalArgumentException("can not find page fragmentName")
            }
            val fragmentClass = Class.forName(fragmentName)
            var fragment = fragmentClass.newInstance() as BaseFragment<ViewDataBinding, BaseViewModel>
            val args = intent.getBundleExtra(BUNDLE)
            if (args != null) {
                fragment.arguments = args
            }
            val trans = supportFragmentManager
                    .beginTransaction()
            trans.replace(mContainer.getId(), fragment)
            trans.commitAllowingStateLoss()
            mFragment = WeakReference(fragment)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }


    }
}