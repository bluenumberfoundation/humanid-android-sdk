package com.nbs.humanidui.presentation.welcome

import android.app.Activity
import android.content.Intent
import com.nbs.humanidui.R
import com.nbs.humanidui.base.BaseActivity
import com.nbs.humanidui.event.CloseAllActivityEvent
import com.nbs.humanidui.presentation.phonenumber.PhoneNumberActivity
import com.nbs.humanidui.util.BundleKeys
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HumanIDActivity : BaseActivity(), WelcomeFragment.OnWelcomeButtonListener {
    companion object{
        @JvmStatic
        fun start(activity: Activity){
            val intent = Intent(activity, HumanIDActivity::class.java)
            activity.startActivityForResult(intent, 200)
        }
    }

    override val layoutResource: Int
        get() = R.layout.activity_humanid

    override fun initLib() {
        EventBus.getDefault().register(this)
    }

    override fun initIntent() {
    }

    override fun initUI() {
    }

    override fun initAction() {
    }

    override fun initProcess() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerWelcome, WelcomeFragment.newInstance(this))
                .commitAllowingStateLoss()
    }

    override fun onButtonContinueClicked() {
        PhoneNumberActivity.start(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseActivityEventReceived(closeAllActivityEvent: CloseAllActivityEvent){
        val intent = Intent()
        closeAllActivityEvent.exchangeToken?.let {
            intent.putExtra(BundleKeys.KEY_USER_HASH, it)
        }

        closeAllActivityEvent.errorMessage?.let {
            intent.putExtra(BundleKeys.KEY_LOGIN_ERROR, it)
        }

        intent.putExtra(BundleKeys.KEY_LOGIN_CANCEL, (closeAllActivityEvent.isCancel))

        setResult(0x300, intent)
        finish()
    }
}
