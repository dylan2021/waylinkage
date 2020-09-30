package com.android.waylinkage.activity.frag0

import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.android.waylinkage.activity.BaseFgActivity

import com.android.waylinkage.R
import com.android.waylinkage.util.KeyConstant
import com.android.waylinkage.util.ToastUtil
import kotlinx.android.synthetic.main.activity_record.*

/**
 * @author Gool Lee
 * @Date 监理旁站记录
 */
class RecordActivity : BaseFgActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_tv -> doPost(v as TextView);
        }
    }

    private fun doPost(it: TextView?) {
        ToastUtil.show(this, "点击" + it?.text)
    }

    var title: String = ""
    var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBar()
        setContentView(R.layout.activity_record)
        title = intent.getStringExtra(KeyConstant.TITLE)
        id = intent.getIntExtra(KeyConstant.id, 0)
        initTitleBackBt(title)

        //record_tv.setOnClickListener(this)
    }


}
