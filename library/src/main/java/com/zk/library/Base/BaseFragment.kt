package com.zk.library.Base

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
import com.zk.library.Utils.MaterialDialogUtils
import com.zk.library.Utils.BUNDLE
import com.zk.library.Utils.CANONICAL_NAME
import com.zk.library.Utils.CLASS
import org.cs.tec.library.status.StatusLayoutManager
import java.lang.reflect.ParameterizedType


abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel> : RxFragment(), IBaseActivity {

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
        Log.e("result", "执行速度")


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
            if(it!=null){
              var t =   it[BUNDLE]
                if(t!=null){
                    startActivity(it[CLASS] as Class<*>,t as Bundle)
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
}