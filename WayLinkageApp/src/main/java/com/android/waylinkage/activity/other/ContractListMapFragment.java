package com.android.waylinkage.activity.other;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.base.fragment.BaseSearchFragment;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ExRadioGroup;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */
@SuppressLint({"WrongConstant", "ValidFragment"})
public class ContractListMapFragment extends BaseSearchFragment {
    public String TAG = ContractListMapFragment.class.getSimpleName();
    private FragmentActivity context;
    private String projectId = "";
    private String projectName;
    private List<ContractInfo> contractsInfoList = new ArrayList<>();
    private View view;

    public ContractListMapFragment(String projectId) {
        this.projectId = projectId;
    }

    public void setData(String projectId, List<ContractInfo> result) {
        this.projectId = projectId;
        if (result == null) {
            contractsInfoList.clear();
        } else {
            contractsInfoList = result;
        }
        initContractRoadBt(view);
    }

    //点击 地图中的路标按钮
    private void initContractRoadBt(View view) {
        int length = mBtId.length;
        for (int i = 0; i < length; i++) {
            ImageButton flagBt = (ImageButton) view.findViewById(mBtId[i]);
            flagBt.setImageResource(R.drawable.ic_home_flag_disabled);
            if (i < contractsInfoList.size()) {
                final ContractInfo contractInfo = contractsInfoList.get(i);
                flagBt.setImageResource(R.drawable.ic_home_flag);
                flagBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPupWindow(view, contractInfo);
                    }
                });
            } else {
                flagBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtil.show(context, "暂无权限查看该标段信息");
                    }
                });
            }
        }

        view.findViewById(R.id.contracts_list_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTopDialog();
            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_contrats_map;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        context = getActivity();
        this.view = view;
        //获取标段列表
        initContractRoadBt(view);

    }

    private void showTopDialog() {
        final Dialog dialog = new Dialog(context, R.style.dialog_top_to_bottom);
        //填充对话框的布局

        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_road_map_top, null);
        ExRadioGroup topLayout = (ExRadioGroup) inflate.findViewById(R.id.road_map_layout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(55, 0, 0, 0);
        for (ContractInfo contractInfo : contractsInfoList) {
            final String name = contractInfo.getName();
            ContractInfo.ContractCorporationBean contractCorporation = contractInfo.getContractCorporation();
            String corporationNameEn = "";
            if (contractCorporation != null) {
                corporationNameEn = contractCorporation.getNameCn();
            }
            final String unitName = corporationNameEn;
            final String id = contractInfo.getId() + "";

            TextView itemTv = new TextView(context);
            itemTv.setGravity(Gravity.CENTER_VERTICAL);
            itemTv.setPadding(35, 8, 35, 10);
            itemTv.setTextSize(15.5f);
            itemTv.setText(name);
            itemTv.setSingleLine();
            itemTv.setTextColor(context.getResources().getColor(R.color.color666666));
            itemTv.setBackgroundResource(R.drawable.selector_fragment_2_dialog_tab);
            itemTv.setLayoutParams(lp);
            topLayout.addView(itemTv);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BuildSiteListMapActivity.class);
                    intent.putExtra(KeyConstant.id, id);
                    intent.putExtra(KeyConstant.name, name);
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            });
        }

        dialog.setContentView(inflate);//将布局设置给Dialog
        DialogUtils.setDialogWindow(context, dialog, Gravity.TOP);
    }

    int[] mBtId = new int[]{R.id.main_fragment0_laod_tag_bt_0, R.id.main_fragment0_laod_tag_bt_1
            , R.id.main_fragment0_laod_tag_bt_2, R.id.main_fragment0_laod_tag_bt_3, R.id.main_fragment0_laod_tag_bt_4
            , R.id.main_fragment0_laod_tag_bt_5, R.id.main_fragment0_laod_tag_bt_6
    };

    String corporationNameEn = "";

    private void showPupWindow(View v, final ContractInfo contractsInfo) {
        BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(context).
                inflate(R.layout.layout_frgment_0_road_tag_popup_view, null);
        final PopupWindow popupWindow = BubblePopupHelper.create(context, bubbleLayout);
        TextView tvContent = (TextView) bubbleLayout.findViewById(R.id.frgmnet0_load_tag_popupview_tv);

        ContractInfo.ContractCorporationBean contractCorporation = contractsInfo.getContractCorporation();
        if (contractCorporation != null) {
            corporationNameEn = contractCorporation.getNameCn();
        }
        tvContent.setText(contractsInfo.getName() + "：" + corporationNameEn);//业主单位

        int[] location = new int[2];
        v.getLocationInWindow(location);
        popupWindow.showAsDropDown(v);

        bubbleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                //工地
                Intent intent = new Intent(context, BuildSiteListMapActivity.class);
                intent.putExtra(KeyConstant.id, contractsInfo.getId() + "");
                intent.putExtra(KeyConstant.name, contractsInfo.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadView(View view) {
        return null;
    }

}
