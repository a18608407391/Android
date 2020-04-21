package com.zk.library.Base

import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.trello.rxlifecycle2.components.support.RxFragment
import com.zk.library.Base.Transaction.ExtraTransaction
import com.zk.library.Base.Transaction.ISupportFragment
import com.zk.library.Base.Transaction.SupportFragmentDelegate
import com.zk.library.Base.Transaction.anim.FragmentAnimator
import com.zk.library.Utils.MaterialDialogUtils
import com.zk.library.Utils.BUNDLE
import com.zk.library.Utils.CANONICAL_NAME
import com.zk.library.Utils.CLASS
import org.cs.tec.library.status.StatusLayoutManager
import java.lang.reflect.ParameterizedType
import android.view.animation.Animation
import android.widget.Toast
import com.zk.library.Base.Transaction.SupportHelper


abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel> : RxFragment(), IBaseFragment, ISupportFragment {

    var binding: V? = null
    public var viewModel: VM? = null
    protected var viewModelId: Int = 0
    var dialog: MaterialDialog? = null
    var inputdialog: MaterialDialog? = null
    lateinit var mStatusLayoutManager: StatusLayoutManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var k: Int = initContentView()
        binding = DataBindingUtil.inflate(inflater, k, container, false)
        viewModelId = initVariableId()
        viewModel = initViewModel()
        if (viewModel == null) {
            val modelClass: Class<*>
            val type = javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<*>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java
            }
            viewModel = createViewModel(this, modelClass as Class<VM>)
        }
        binding!!.setVariable(viewModelId, viewModel)
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel!!)
        //注入RxLifecycle生命周期
        viewModel!!.injectLifecycleProvider(this)
//        initStatusLayout()


        return binding!!.root

    }


    open fun initMap(savedInstanceState: Bundle?) {

    }


    open fun initViewStub() {

    }

//    private fun initStatusLayout() {
//        mStatusLayoutManager = StatusLayoutManager.Builder(activity as Context)
//                .setContentLayout(binding!!.root)
//                .setEmptyLayout(R.layout.layout_status_layout_manager_empty)
//                .setErrorLayout(R.layout.layout_status_layout_manager_error)
//                .setLoadingLayout(R.layout.layout_status_layout_manager_loading)
//                .setErrorLayoutClickId(R.id.error_click)
//                .newBuilder()
//        mStatusLayoutManager.showLoading()
//    }


    fun showProgressDialog(msg: String): Dialog? {
        var dialog = _mActivity?.showProgressDialog(msg)
        return dialog
    }

    fun dismissProgressDialog() {
        _mActivity!!.dismissProgressDialog()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        //页面数据初始化方法
        initData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
        //注册RxBus
        viewModel!!.registerRxBus()
        initMap(savedInstanceState)
    }

    open fun getLayoutId(): Int {
        return 0
    }

    private fun registorUIChangeLiveDataCallBack() {
        viewModel!!.getUC().getShowDialogEven().observe(this, Observer {
            showDialog(it!!)
        })
        viewModel!!.getUC().getDismisDialogEven().observe(this, Observer {
            dismissDialog()
        })
        viewModel!!.getUC().getStartActivityEven().observe(this, Observer {
            if (it != null) {
                var t = it[BUNDLE]
                if (t != null) {
                    startActivity(it[CLASS] as Class<*>, t as Bundle)
                }
            }

//            var clz: Class<*> = it!![CLASS] as Class<*>
//            var bundle: Bundle = it!![BUNDLE] as Bundle
//            if(bundle!=null){
//                startActivity(clz, bundle)
//            }
        })
        viewModel!!.getUC().getStartContainerEvent()!!.observe(this, Observer {
            val canonicalName = it!![CANONICAL_NAME] as String
            val bundle = it!![BUNDLE] as Bundle
            startContainerActivity(canonicalName, bundle)
        })
        viewModel!!.getUC().getFinishEven().observe(this, Observer { activity!!.finish() })
        //关闭上一层
        viewModel!!.getUC().getonBackPressEvent().observe(this, Observer { activity!!.onBackPressed() })
        viewModel!!.getUC().showToastEven().observe(this, Observer {
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    abstract fun initContentView(): Int
    fun showDialog(title: String) {
        if (dialog != null) {
            dialog?.setTitle(title)
            dialog!!.show()
        } else {
            val builder = MaterialDialogUtils.showIndeterminateProgressDialog(activity!!, title, true)
            dialog = builder.show()
        }
    }

    fun showEnterDialog(): MaterialDialog.Builder? {
        if (inputdialog != null) {
            inputdialog!!.show()
        } else {
            var bundle = MaterialDialogUtils.showInputDialog(activity!!, "用户校验", "非本人谢绝使用")
            inputdialog = bundle.show()
            inputdialog!!.builder.autoDismiss(false)
        }

        return inputdialog!!.builder
    }

    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>) {
        startActivity(Intent(context, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(context, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    fun startContainerActivity(canonicalName: String) {
        startContainerActivity(canonicalName, null)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    fun startContainerActivity(canonicalName: String, bundle: Bundle?) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }

    /**
     * =====================================================================
     */

    //刷新布局
    fun refreshLayout() {
        if (viewModel != null) {
            binding!!.setVariable(viewModelId, viewModel)
        }
    }

    override fun initParam() {

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    fun initViewModel(): VM? {
        return null
    }

    override fun initData() {

    }

    override fun initViewObservable() {

    }

    fun isBackPressed(): Boolean {
        return false
    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    fun <T : ViewModel> createViewModel(fragment: Fragment, cls: Class<T>): T {
        return ViewModelProviders.of(fragment).get(cls)
    }

    //判断用户是否可见，在第一次onResume时是不会调用该方法的
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mDelegate.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {//用户可见
            if (isFirstVisible) {
                isFirstVisible = false
                onFirstUserVisible()
            } else {
                onUserVisible()
            }
        } else {//用户不可见
            if (isFirstInvisible) {
                isFirstInvisible = false
                onFirstUserInvisible()
            } else {
                onUserInvisible()
            }

        }
    }

    private var isFirstVisible: Boolean = true
    private var isFirstInvisible: Boolean = true
    //第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
    private var isFirstResume: Boolean = true

    /**
     * 用户第一次可见
     */
    open fun onFirstUserVisible() {

    }

    /**
     * 用户第一次不可见
     */
    open fun onFirstUserInvisible() {

    }

    /**
     * 用户不可见
     */
    open fun onUserInvisible() {

    }

    /**
     * 用户可见时调用
     */
    open fun onUserVisible() {

    }


    var mDelegate = SupportFragmentDelegate(this)
    var _mActivity: BaseActivity<ViewDataBinding, BaseViewModel>? = null


    override fun getSupportDelegate(): SupportFragmentDelegate {
        return mDelegate
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mDelegate.onAttach(activity)
        _mActivity = mDelegate.activity as BaseActivity<ViewDataBinding, BaseViewModel>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState);

    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (mDelegate == null) {

        } else {

        }

        return mDelegate.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDelegate.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mDelegate.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mDelegate.onPause()
    }

    override fun onDestroyView() {
        mDelegate.onDestroyView()
        super.onDestroyView()
        viewModel?.removeRxBus()
    }

    override fun onDestroy() {
        mDelegate.onDestroy()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mDelegate.onHiddenChanged(hidden)
    }

    override fun extraTransaction(): ExtraTransaction {
        return mDelegate.extraTransaction()
    }

    override fun enqueueAction(runnable: Runnable?) {
        mDelegate.enqueueAction(runnable)
    }

    override fun post(runnable: Runnable?) {
        mDelegate.post(runnable)
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        mDelegate.onEnterAnimationEnd(savedInstanceState)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        mDelegate.onLazyInitView(savedInstanceState)
    }

    override fun onSupportVisible() {
        mDelegate.onSupportVisible()
    }

    override fun onSupportInvisible() {
        mDelegate.onSupportInvisible()
    }

    override fun isSupportVisible(): Boolean {
        return mDelegate.isSupportVisible()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return mDelegate.onCreateFragmentAnimator()
    }

    override fun getFragmentAnimator(): FragmentAnimator {
        return mDelegate.getFragmentAnimator()
    }

    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?) {
        mDelegate.setFragmentAnimator(fragmentAnimator)
    }

    override fun setFragmentResult(resultCode: Int, bundle: Bundle?) {
        mDelegate.setFragmentResult(resultCode, bundle)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        mDelegate.onFragmentResult(requestCode, resultCode, data)
    }

    override fun onNewBundle(args: Bundle?) {
        mDelegate.onNewBundle(args)
    }

    override fun putNewBundle(newBundle: Bundle?) {
        mDelegate.putNewBundle(newBundle)
    }

    override fun onBackPressedSupport(): Boolean {
        Log.e("result","onBackPressedSupport" + this::class.java.canonicalName)
        return mDelegate.onBackPressedSupport()
    }


    open fun hideSoftInput() {
        mDelegate.hideSoftInput()
    }

    /**
     * 显示软键盘,调用该方法后,会在onPause时自动隐藏软键盘
     */
    open fun showSoftInput(view: View) {
        mDelegate.showSoftInput(view);
    }

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    open fun loadRootFragment(containerId: Int, toFragment: ISupportFragment) {
        mDelegate.loadRootFragment(containerId, toFragment);
    }

    open fun loadRootFragment(containerId: Int, toFragment: ISupportFragment, addToBackStack: Boolean, allowAnim: Boolean) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnim);
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    open fun loadMultipleRootFragment(containerId: Int, showPosition: Int, vararg toFragments: ISupportFragment) {
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
    open fun showHideFragment(showFragment: ISupportFragment) {
        mDelegate.showHideFragment(showFragment)
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    open fun showHideFragment(showFragment: ISupportFragment, hideFragment: ISupportFragment) {
        mDelegate.showHideFragment(showFragment, hideFragment);
    }

    open fun start(toFragment: ISupportFragment) {
        mDelegate.start(toFragment);
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    open fun start(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int) {
        mDelegate.start(toFragment, launchMode);
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    open fun startForResult(toFragment: ISupportFragment, requestCode: Int) {
        mDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * Start the target Fragment and pop itself
     */
    open fun startWithPop(toFragment: ISupportFragment) {
        mDelegate.startWithPop(toFragment);
    }

    /**
     * @see #popTo(Class, boolean)
     * +
     * @see #start(ISupportFragment)
     */
    open fun startWithPopTo(toFragment: ISupportFragment, targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }


    open fun replaceFragment(toFragment: ISupportFragment, addToBackStack: Boolean) {
        mDelegate.replaceFragment(toFragment, addToBackStack);
    }

    open fun pop() {
        mDelegate.pop();
    }

    /**
     * Pop the child fragment.
     */
    open fun popChild() {
        mDelegate.popChild();
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
    open fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    open fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    open fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable, popAnim: Int) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    open fun popToChild(targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.popToChild(targetFragmentClass, includeTargetFragment);
    }

    open fun popToChild(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable) {
        mDelegate.popToChild(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    open fun popToChild(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable, popAnim: Int) {
        mDelegate.popToChild(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * 得到位于栈顶Fragment
     */
    open fun getTopFragment(): ISupportFragment {
        return SupportHelper.getTopFragment(getFragmentManager());
    }

    open fun getTopChildFragment(): ISupportFragment {
        return SupportHelper.getTopFragment(getChildFragmentManager());
    }

    /**
     * @return 位于当前Fragment的前一个Fragment
     */
    open fun getPreFragment(): ISupportFragment {
        return SupportHelper.getPreFragment(this);
    }


    /**
     * 获取栈内的fragment对象
     */
    open fun <T : ISupportFragment> findFragment(fragmentClass: Class<T>): T {
        return SupportHelper.findFragment(getFragmentManager(), fragmentClass);
    }

    /**
     * 获取栈内的fragment对象
     */
    open fun <T : ISupportFragment> findChildFragment(fragmentClass: Class<T>): T {
        return SupportHelper.findFragment(getChildFragmentManager(), fragmentClass);
    }

}