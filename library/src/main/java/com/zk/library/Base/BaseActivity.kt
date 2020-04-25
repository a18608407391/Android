package com.zk.library.Base

import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.zk.library.Base.Transaction.*
import com.zk.library.Base.Transaction.anim.FragmentAnimator
import com.zk.library.Bus.Messenger
import com.zk.library.Bus.event.ActivityDestroyEven
import com.zk.library.R
import com.zk.library.Utils.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.http.NetworkUtil


abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel> : RxAppCompatActivity(), IBaseActivity, ISupportActivity {
    var mViewModel: VM? = null
    //    var mImmersionBar: ImmersionBar? = null
//    var flagImmersionBar: Boolean = true
    lateinit var binding: V
    var viewModelId: Int = 0
    var dialog: MaterialDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //页面接受的参数方法
        mDelegate.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.e("result", "不为空")
        }
        window.setBackgroundDrawable(null)
        initParam()

        ARouter.getInstance().inject(this)
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding(savedInstanceState)
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        //页面数据初始化方法
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        //注册RxBus


//        if (Build.VERSION.SDK_INT == 26) {
//            Log.e("result", "8.0")
//        } else {

//        }
        initData()
        initMap(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        if (flagImmersionBar) {
//            mImmersionBar = ImmersionBar.with(this)
//            mImmersionBar!!.statusBarColor(R.color.white).fitsSystemWindows(true).statusBarDarkFont(true).statusBarColorTransform(R.color.white).init()
//        }else{
//        }
        initViewObservable()
        mViewModel!!.registerRxBus()
    }

    open fun initMap(savedInstanceState: Bundle?) {

    }


    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == 26) {
            return
        } else {
            super.setRequestedOrientation(requestedOrientation)
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_DOWN) {
            if (!NetworkUtil.isNetworkAvailable(context)) {
                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (doTouchEven(ev)) {
            return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
        } else {
            return false
        }
    }


    open fun doTouchEven(ev: MotionEvent?): Boolean {

        return true
    }


    var progressDialog: Dialog? = null
    fun showProgressDialog(msg: String): Dialog? {
        progressDialog = showProgress(this@BaseActivity, msg)
        return progressDialog
    }

    fun showProgress(mActivity: Activity, text: String): Dialog {
        if (progressDialog == null) {
            progressDialog = Dialog(mActivity)
            var inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflate.inflate(R.layout.loading_layout, null, false)
            progressDialog!!.setContentView(view)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        var img = progressDialog!!.findViewById<ImageView>(R.id.progress_animate)
        var title = progressDialog!!.findViewById<TextView>(R.id.progress_title)
        if (!progressDialog!!.isShowing) {
            title!!.text = text
            var d = img.background as AnimationDrawable
            d.start()
            progressDialog!!.show()
        }
        return progressDialog!!
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing!!) {
            var img = progressDialog!!.findViewById<ImageView>(R.id.progress_animate)
            var d = img.background as AnimationDrawable
            d.stop()
            progressDialog!!.dismiss()
        }
    }


    private fun fixOrientation(): Boolean {
        try {
            var field = Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            var o = field?.get(this) as ActivityInfo
            o.screenOrientation = -1
            field.isAccessible = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    open fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            //根据上面设置是否对状态栏单独设置颜色
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            val localLayoutParams = window.attributes
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        }
    }

    open fun doPressBack() {
        mDelegate.onBackPressedSupport()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("result", "onDestroy")
        //接触Messenger注册.  mDelegate.onDestroy();
        mDelegate.onDestroy();

        RxBus.default?.post(ActivityDestroyEven(this.componentName.className))
        Messenger.get()!!.unregister(mViewModel!!)
        lifecycle.removeObserver(mViewModel!!)
        mViewModel!!.removeRxBus()
        mViewModel = null
        binding.unbind()
        if (progressDialog != null && progressDialog?.isShowing!!) {
            progressDialog!!.dismiss()
        }
//        if(mImmersionBar!=null){
//            mImmersionBar!!.removeSupportAllView()
//            mImmersionBar!!.destroy()
//        }
    }

    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState))
        viewModelId = initVariableId()
        mViewModel = initViewModel()
        if (mViewModel == null) {
            var modelClass = GenericsUtils.getSuperClassGenricType(BaseActivity::class.java, 1)
            mViewModel = ViewModelFactory.INSTANCE.create(modelClass as Class<VM>)
        }
        binding.setVariable(viewModelId, mViewModel)
        lifecycle.addObserver(mViewModel!!)
        mViewModel!!.injectLifecycleProvider(this)
        setMap(savedInstanceState)

    }

    open fun setMap(savedInstanceState: Bundle?) {

    }

    fun refreshLayout() {
        if (mViewModel != null) {
            binding.setVariable(viewModelId, mViewModel)
        }
    }

    private fun registorUIChangeLiveDataCallBack() {
        mViewModel!!.getUC().getShowDialogEven().observe(this, Observer {
            showDialog(it!!)
        })
        mViewModel!!.getUC().getDismisDialogEven().observe(this, Observer {
            dismissDialog()
        })
        mViewModel!!.getUC().getStartActivityEven().observe(this, Observer {
            var clz: Class<*> = it!![CLASS] as Class<*>
            var bundle: Bundle = it!![BUNDLE] as Bundle
            startActivity(clz, bundle)
        })
        mViewModel!!.getUC().getStartContainerEvent()!!.observe(this, Observer {
            val canonicalName = it!![CANONICAL_NAME] as String
            val bundle = it!![BUNDLE] as Bundle
            startContainerActivity(canonicalName, bundle)
        })
        mViewModel!!.getUC().getFinishEven().observe(this, Observer { finish() })
        //关闭上一层
        mViewModel!!.getUC().getonBackPressEvent().observe(this, Observer { onBackPressed() })
//        mViewModel!!.getUC().showToastEven().observe(this, Observer {
//            Toast.makeText(this@BaseActivity, it, Toast.LENGTH_SHORT).show()
//        })
    }

    fun showDialog(title: String) {
        if (dialog != null) {
            dialog!!.show()
        } else {
            var build: MaterialDialog.Builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true)
            dialog = build.show()
        }
    }

    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    fun startActivity(clz: Class<*>) {
        startActivity(Intent(this, clz))
    }

    fun startActivity(clz: Class<*>, bundle: Bundle) {
        var intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(Intent(this, clz))
    }

    fun startContainerActivity(canonicalName: String) {
        startContainerActivity(canonicalName, null)
    }

    fun startContainerActivity(canonicalName: String, bundle: Bundle?) {
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }

    override fun initParam() {
    }

    var mDelegate = SupportActivityDelegate(this)

    abstract fun initVariableId(): Int
    abstract fun initContentView(savedInstanceState: Bundle?): Int
    open fun initViewModel(): VM? {
        return null
    }

    override fun initData() {
    }

    override fun initViewObservable() {
    }

    fun <T : ViewModel> createViewModel(fragment: FragmentActivity, cls: Class<T>): T {
        return ViewModelProviders.of(fragment).get(cls)
    }


    fun <T : ISupportFragment> findFragment(fragmentClass: Class<T>): T {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass)
    }

    fun setDefaultFragmentBackground(@DrawableRes backgroundRes: Int) {
        mDelegate.setDefaultFragmentBackground(backgroundRes)
    }

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    fun loadRootFragment(containerId: Int, @NonNull toFragment: ISupportFragment) {
        mDelegate.loadRootFragment(containerId, toFragment)
    }

    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment, addToBackStack: Boolean, allowAnimation: Boolean) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation);
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    fun loadMultipleRootFragment(containerId: Int, showPosition: Int, vararg toFragments: ISupportFragment) {
        mDelegate.loadMultipleRootFragment(containerId, showPosition, *toFragments)
    }

    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     * <p>
     * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
     *
     * @param showFragment 需要show的Fragment
     */
    fun showHideFragment(showFragment: ISupportFragment) {
        mDelegate.showHideFragment(showFragment);
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    fun showHideFragment(showFragment: ISupportFragment, hideFragment: ISupportFragment) {
        mDelegate.showHideFragment(showFragment, hideFragment)
    }

    /**
     * It is recommended to use {@link SupportFragment#start(ISupportFragment)}.
     */
    fun start(toFragment: ISupportFragment) {
        mDelegate.start(toFragment)
    }

    override fun onResume() {
        super.onResume()
//        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
//        MobclickAgent.onPause(this)
    }

    /**
     * It is recommended to use {@link SupportFragment#start(ISupportFragment, int)}.
     *
     * @param launchMode Similar to Activity's LaunchMode.
     */
    fun start(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int) {
        mDelegate.start(toFragment, launchMode);
    }

    /**
     * It is recommended to use {@link SupportFragment#startForResult(ISupportFragment, int)}.
     * Launch an fragment for which you would like a result when it poped.
     */
    fun startForResult(toFragment: ISupportFragment, requestCode: Int) {
        mDelegate.startForResult(toFragment, requestCode)
    }

    /**
     * It is recommended to use {@link SupportFragment#startWithPop(ISupportFragment)}.
     * Start the target Fragment and pop itself
     */
    fun startWithPop(toFragment: ISupportFragment) {
        mDelegate.startWithPop(toFragment)
    }

    /**
     * It is recommended to use {@link SupportFragment#startWithPopTo(ISupportFragment, Class, boolean)}.
     *
     * @see #popTo(Class, boolean)
     * +
     * @see #start(ISupportFragment)
     */
    fun startWithPopTo(toFragment: ISupportFragment, targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#replaceFragment(ISupportFragment, boolean)}.
     */
    fun replaceFragment(toFragment: ISupportFragment, addToBackStack: Boolean) {
        mDelegate.replaceFragment(toFragment, addToBackStack);
    }

    /**
     * Pop the fragment.
     */
    fun pop() {
        mDelegate.pop()
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     * <p>
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable, popAnim: Int) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }


    fun getTopFragment(): ISupportFragment {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    override fun getSupportDelegate(): SupportActivityDelegate {
        return mDelegate
    }

    override fun extraTransaction(): ExtraTransaction {
        return mDelegate.extraTransaction();

    }

    override fun getFragmentAnimator(): FragmentAnimator {
        return mDelegate.getFragmentAnimator()
    }

    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?) {
        mDelegate.setFragmentAnimator(fragmentAnimator)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return mDelegate.onCreateFragmentAnimator()
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDelegate.onPostCreate(savedInstanceState);

    }

    override fun post(runnable: Runnable?) {
        mDelegate.post(runnable)
    }

    override fun onBackPressed() {

        mDelegate.onBackPressed()
    }

    override fun onBackPressedSupport() {
        mDelegate.onBackPressedSupport()
    }
}