package com.android.waylinkage.activity.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.ChooseProjectActivity;
import com.android.waylinkage.activity.frag0.EdmListActivity;
import com.android.waylinkage.activity.frag0.QualityDetailActivity;
import com.android.waylinkage.activity.frag0.QualityOverActivity;
import com.android.waylinkage.activity.frag0.RecordActivity;
import com.android.waylinkage.activity.frag0.SafyListActivity;
import com.android.waylinkage.base.fragment.BaseSearchFragment;
import com.android.waylinkage.bean.BuildSiteInfo;
import com.android.waylinkage.bean.ChooseDataInfo;
import com.android.waylinkage.bean.Contact;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.bean.CounterInfo;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.ProgressOverdueInfo;
import com.android.waylinkage.bean.ProgressOverdueObj;
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.bean.SafyInfo;
import com.android.waylinkage.bean.StatData;
import com.android.waylinkage.bean.StatInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.AuthsConstant;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ExRadioGroup;
import com.android.waylinkage.view.RoundProgressBar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * Gool Lee
 */
@SuppressLint({"WrongConstant", "ValidFragment"})
public class BuildSiteFragment extends BaseSearchFragment {
    public String TAG = BuildSiteFragment.class.getSimpleName();
    private MainActivity context;
    private String chooseId = "";
    private TextView topProjectNameTv;
    private RoundProgressBar pb0, pb;
    private TextView pb0Tv, pbTv;
    private TextView belowContractNameTv, buildSiteNameTv;
    private TextView constructUnitNameTv;
    private SimpleDraweeView buildSitePicIv;
    private TextView counterNumberTv, planDesignInfoTv;
    private String buildSiteName;
    private TextView totalProgress0, qualityTotalTv, buttomTv0_2, buttomTv1, buttomTv1_2,
            buttomTv2, buttomTv2_2, qualityBt, progressBt;
    private String progressStatus;
    private int leftPeriodDay;
    private double processProgress = 0;
    private TextView thingsNumTv, flagTv, materailTv;
    private int tabPosition = 0;
    private int totalQuality;
    private int confirmedQuality;
    private int unreformedQuality;
    private double passingRate;
    private int trainNum, accidentNum;
    private ExRadioGroup overdueItemRGProgress, overdueItemRG;
    private String type = "0";
    private SharedPreferences sp;
    private String PROJECT_IMG_URL;
    private TextView leftPeriodTv;
    private TextView onGoingProcessorCntTv;
    private LinearLayout.LayoutParams tvParams;
    private ImageView qualityOverOpen, progressOverOpen;
    private int px43;
    private String url_counter;
    int belowContractProjectId = 0;
    String urlBuildSiteContract = "";
    String urlOverQualityType = "", urlOverProgressType = "", urlInfoType = "";
    private Button backTopBt;
    private View contractLayout, view, qualityOverLayout;
    private SharedPreferences.Editor spEdit;
    private List<Contact> contractBulidSitesList;
    private int pendingApproval;

    public BuildSiteFragment(int choosedId) {
        chooseId = choosedId + "";
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_build_site;
    }

    private String[] tabArr = {"工程进度", "工程质量", "工程安全"};

    @Override
    protected void initViewsAndEvents(View v) {
        context = (MainActivity) getActivity();
        sp = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        spEdit = sp.edit();
        view = v;
        initView();
        initMenuBt();
        initGuildTipDialog();
    }

    private void setTvName(int id, String text) {
        TextView tv1 = view.findViewById(id);
        tv1.setText((App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? "工地" :
                App.CHOOSE_AUTH_TYPE == Constant.CONTRACT ? "标段" : "项目") + text
        );
    }

    private void initGuildTipDialog() {

        boolean isFirstLuncher = sp.getBoolean(KeyConstant.IS_FIRST_SHOW_GUILD, true);
        if (isFirstLuncher) {
            int picId = R.drawable.bg_project_switch;
            int pic2Id = R.drawable.bg_project_search;
            if (App.AUTH_TYPE == Constant.CONTRACT) {
                picId = R.drawable.bg_contract_switch;
                pic2Id = R.drawable.bg_contract_search;
            } else if (App.AUTH_TYPE == Constant.BUILDSITE) {
                picId = R.drawable.bg_building_sites_switch;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                    .dialog_fullscreen);

            final ImageView guildView = new ImageView(context);
            guildView.setImageResource(picId);
            guildView.setScaleType(ImageView.ScaleType.FIT_XY);
            final Dialog dialog = builder.create();
            dialog.show();

            final int finalPic2Id = pic2Id;
            guildView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.AUTH_TYPE == Constant.BUILDSITE) {
                        dialog.dismiss();
                    } else {
                        guildView.setImageResource(finalPic2Id);
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (App.AUTH_TYPE == Constant.BUILDSITE) {
                            return false;
                        } else {
                            guildView.setImageResource(finalPic2Id);
                            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    return false;
                                }
                            });
                            return true;
                        }
                    }
                    return false;
                }
            });
            dialog.getWindow().setContentView(guildView);
            spEdit.putBoolean(KeyConstant.IS_FIRST_SHOW_GUILD, false).commit();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
        setTvName(R.id.design_info_tv, "概况");
        setTvName(R.id.employee_manager_bt, "人员");
        setTvName(R.id.device_manager_bt, "设备");
        setTvName(R.id.materail_manager_bt, "材料");
        setTvName(R.id.data_manager_bt, "资料");

        setTvName(R.id.progress_tv, "进度");
        setTvName(R.id.quality_tv, "质量");
        setTvName(R.id.safy_tv, "安全");
    }

    private void getData() {
        getInfo();
        getCounterNumber();
        getProgressData();//进度
        getQualityThingsNumsData();//质量
        getSecurityData();//安全

        //逾期项(进度,质量)
        getOverdueDataProgress();
        if (App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE) {
            getOverdueBuidSiteQuality();
        } else {
            getOverdueContratsQuality();
        }

        getAuthData();
    }

    //获取数据
    private void getInfo() {
        if (App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE) {
            contractLayout.setVisibility(View.GONE);
            if (App.AUTH_TYPE == Constant.BUILDSITE) {
                backTopBt.setVisibility(View.GONE);
            } else {
                backTopBt.setVisibility(View.VISIBLE);
            }
            urlInfoType = UrlConstant.url_biz_buildSites;
            urlOverProgressType = "/biz/bi/buildSite/progress?id=";
            urlOverQualityType = "buildSiteId=";
            urlBuildSiteContract = UrlConstant.url_biz_buildSites + "/all?contractId=";
            getBuildSiteInfo();
        } else if (App.CHOOSE_AUTH_TYPE == Constant.CONTRACT) {
            contractLayout.setVisibility(View.VISIBLE);
            buildSiteNameTv.setVisibility(View.GONE);
            if (App.AUTH_TYPE == Constant.CONTRACT) {
                backTopBt.setVisibility(View.GONE);
            } else {
                backTopBt.setText("返回项目");
                backTopBt.setVisibility(View.VISIBLE);
            }
            urlInfoType = UrlConstant.url_biz_contracts;
            urlOverProgressType = "/biz/bi/contract/progress?id=";
            urlOverQualityType = "contractId=";
            urlBuildSiteContract = UrlConstant.url_biz_contracts + "/all?projectId=";
            url_counter = Constant.WEB_SITE + UrlConstant.url_biz_buildSites + "/all?contractId=" + chooseId;
            getProjectContractInfo();
        } else {
            buildSiteNameTv.setVisibility(View.GONE);
            backTopBt.setVisibility(View.GONE);
            contractLayout.setVisibility(View.VISIBLE);
            counterNumberTv.setPadding(20, 0, 0, 0);
            ((TextView) view.findViewById(R.id.contract_title_tv)).setText("标段");
            urlInfoType = UrlConstant.url_biz_projects;
            urlOverProgressType = "/biz/bi/project/progress?id=";
            urlOverQualityType = "projectId=";
            url_counter = Constant.WEB_SITE + UrlConstant.url_biz_contracts + "/all?projectId=" + chooseId;
            getProjectContractInfo();
        }
    }

    private void getContractBulidSitesList() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + urlBuildSiteContract + belowContractProjectId;
        Response.Listener<List<Contact>> successListener = new Response
                .Listener<List<Contact>>() {
            @Override
            public void onResponse(List<Contact> result) {
                contractBulidSitesList = result;
            }
        };

        Request<List<Contact>> versionRequest = new
                GsonRequest<List<Contact>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<List<Contact>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void getCounterNumber() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        Response.Listener<List<Contact>> successListener = new Response
                .Listener<List<Contact>>() {
            @Override
            public void onResponse(List<Contact> result) {
                if (result == null) {
                    return;
                }
                counterNumberTv.setText(result.size() + "个");
            }
        };

        Request<List<Contact>> versionRequest = new
                GsonRequest<List<Contact>>(
                        Request.Method.GET, url_counter,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<List<Contact>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    //获取标段
    private void getProjectContractInfo() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + urlInfoType + "/" + chooseId;
        Response.Listener<ContractInfo> successListener = new Response
                .Listener<ContractInfo>() {
            @Override
            public void onResponse(ContractInfo result) {
                if (result == null) {
                    return;
                }
                if (result.getBizProject() != null) {
                    belowContractProjectId = result.getBizProject().getId();
                }
                buildSiteName = result.getName();
                type = App.CHOOSE_AUTH_TYPE + "";

                double invest = result.getInvest();
                String unitNameCn = "";
                if (result.getContractCorporation() != null) {
                    unitNameCn = result.getContractCorporation().getNameCn();
                } else if (result.getConstructionUnit() != null) {
                    unitNameCn = result.getConstructionUnit().getNameCn();
                }
                String planBeginDate = TextUtil.substringTime(result.getPlanBeginDate());
                String planEndDate = result.getPlanEndDate();
                setTopInfoView(result.getPic(), invest, unitNameCn, planBeginDate, planEndDate);
            }
        };

        Request<ContractInfo> versionRequest = new
                GsonRequest<ContractInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<ContractInfo>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void initMenuBt() {
        View.OnClickListener onMenuClickLister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, EdmListActivity.class);
                intent1.putExtra(KeyConstant.project_plan_title, buildSiteName);
                intent1.putExtra(KeyConstant.id, chooseId);
                intent1.putExtra(KeyConstant.project_plan_info_type, Integer.valueOf(type));

                switch (v.getId()) {
                    case R.id.confirm_things_bt:
                        Intent intent = new Intent(context, ReportMenuActivity.class);
                        intent.putExtra(KeyConstant.id, chooseId);
                        context.startActivity(intent);
                        break;
                    case R.id.employee_manager_bt:
                        intent1.putExtra(KeyConstant.type, 3);//人员管理
                        context.startActivity(intent1);

                        break;
                    case R.id.device_manager_bt:
                        intent1.putExtra(KeyConstant.type, 4);//设备管理
                        context.startActivity(intent1);
                        break;
                    case R.id.materail_manager_bt:
                        intent1.putExtra(KeyConstant.type, 5);//材料管理
                        context.startActivity(intent1);
                        break;
                    case R.id.plans_bt:
                        Intent i = new Intent(context, ProcessorListActivity.class);
                        i.putExtra(KeyConstant.id, chooseId);
                        i.putExtra(KeyConstant.TITLE, buildSiteName);
                        context.startActivity(i);
                        break;
                    //日志管理
                    case R.id.note_manager_bt:
                        Intent i2 = new Intent(context, RecordActivity.class);
                        i2.putExtra(KeyConstant.id, chooseId);
                        i2.putExtra(KeyConstant.TITLE, context.getString(R.string.note_manager));
                        context.startActivity(i2);
                        break;
                    //资料管理
                    case R.id.data_manager_bt:
                        Intent i1 = new Intent(context, RecordActivity.class);
                        i1.putExtra(KeyConstant.id, chooseId);
                        i1.putExtra(KeyConstant.TITLE, context.getString(R.string.data_manager));
                        context.startActivity(i1);
                        break;
                    //更多
                    case R.id.more_bt:
                        Intent i3 = new Intent(context, MoreActivity.class);
                        i3.putExtra(KeyConstant.id, chooseId);
                        i3.putExtra(KeyConstant.project_plan_title, buildSiteName);
                        i3.putExtra(KeyConstant.type, Integer.valueOf(type));
                        i3.putExtra(KeyConstant.numbers, pendingApproval);
                        i3.putExtra(KeyConstant.TITLE, context.getString(R.string.more));
                        context.startActivity(i3);
                        break;
                    //标段/工地列表
                    case R.id.contract_layout:
                        Intent i4 = new Intent(context, BuildSiteListActivity.class);
                        i4.putExtra(KeyConstant.id, chooseId);
                        i4.putExtra(KeyConstant.TITLE, buildSiteName);
                        context.startActivity(i4);
                        context.finish();
                        break;
                }
            }
        };
        View bt1 = view.findViewById(R.id.confirm_things_bt);
        View bt2 = view.findViewById(R.id.employee_manager_bt);
        View bt3 = view.findViewById(R.id.device_manager_bt);
        View bt4 = view.findViewById(R.id.materail_manager_bt);


        View planBt = view.findViewById(R.id.plans_bt);
        View dataBt = view.findViewById(R.id.data_manager_bt);
        View noteBt = view.findViewById(R.id.note_manager_bt);
        View moreBt = view.findViewById(R.id.more_bt);

        View emptyBt = view.findViewById(R.id.empty_bt);
        if (App.CHOOSE_AUTH_TYPE != Constant.BUILDSITE) {
            if (App.CHOOSE_AUTH_TYPE == Constant.PROJECT) {//项目
                emptyBt.setVisibility(View.VISIBLE);
                bt1.setVisibility(View.VISIBLE);
                dataBt.setVisibility(View.VISIBLE);
            } else {//合同段
                bt2.setVisibility(View.VISIBLE);
                bt3.setVisibility(View.VISIBLE);
                bt4.setVisibility(View.VISIBLE);
            }
        } else {//标段
            planBt.setVisibility(View.VISIBLE);
            noteBt.setVisibility(View.VISIBLE);
            dataBt.setVisibility(View.VISIBLE);
            moreBt.setVisibility(View.VISIBLE);
        }

        planBt.setOnClickListener(onMenuClickLister);
        bt1.setOnClickListener(onMenuClickLister);
        bt2.setOnClickListener(onMenuClickLister);
        bt3.setOnClickListener(onMenuClickLister);
        bt4.setOnClickListener(onMenuClickLister);
        dataBt.setOnClickListener(onMenuClickLister);
        noteBt.setOnClickListener(onMenuClickLister);
        moreBt.setOnClickListener(onMenuClickLister);
        contractLayout.setOnClickListener(onMenuClickLister);
    }

    private void initView() {
        //获取工地数据
        topProjectNameTv = (TextView) view.findViewById(R.id.top_project_name_tv);
        buildSiteNameTv = (TextView) view.findViewById(R.id.fragment0_name_tv);
        buildSiteName = sp.getString(KeyConstant.SP_MAIN_NAME, "");
        App.AUTH_TYPE = sp.getInt(AuthsConstant.AUTH_TYPE, 0);
        topProjectNameTv.setText(buildSiteName);
        buildSiteNameTv.setText(buildSiteName);

        buildSitePicIv = (SimpleDraweeView) view.findViewById(R.id.pic_sdv);
        PROJECT_IMG_URL = sp.getString(KeyConstant.SP_PROJECT_IMG_URL, "");
        buildSitePicIv.setImageURI(PROJECT_IMG_URL);

        belowContractNameTv = (TextView) view.findViewById(R.id.fragment0_below_contract_tv);
        constructUnitNameTv = (TextView) view.findViewById(R.id.fragment0_construction_unit_tv);

        planDesignInfoTv = (TextView) view.findViewById(R.id.design_info_tv);
        counterNumberTv = (TextView) view.findViewById(R.id.buildsite_number_tv);

        qualityOverLayout = view.findViewById(R.id.quality_over_layout);
        contractLayout = view.findViewById(R.id.contract_layout);
        backTopBt = view.findViewById(R.id.map_bt);
        backTopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back2Top();
            }
        });

        thingsNumTv = (TextView) view.findViewById(R.id.confirm_things_num_red_tv);
        pb = (RoundProgressBar) view.findViewById(R.id.progressbar);
        pb0 = (RoundProgressBar) view.findViewById(R.id.progressbar0);
        pbTv = (TextView) view.findViewById(R.id.pb_tv);
        pb0Tv = (TextView) view.findViewById(R.id.pb0_tv);

        //进度
        progressBt = (TextView) view.findViewById(R.id.progress_tv);
        totalProgress0 = (TextView) view.findViewById(R.id.total_progress_tv);
        leftPeriodTv = (TextView) view.findViewById(R.id.left_priod_tv);
        onGoingProcessorCntTv = (TextView) view.findViewById(R.id.working_numbers_tv);

        //质量
        qualityBt = (TextView) view.findViewById(R.id.quality_tv);
        qualityTotalTv = (TextView) view.findViewById(R.id.bottom_tv0);
        buttomTv1 = (TextView) view.findViewById(R.id.bottom_tv1);
        buttomTv2 = (TextView) view.findViewById(R.id.bottom_tv2);
        progressOverOpen = (ImageView) view.findViewById(R.id.progress_see_more);
        qualityOverOpen = (ImageView) view.findViewById(R.id.quality_see_more);

        buttomTv0_2 = (TextView) view.findViewById(R.id.bottom_tv0_2);
        buttomTv1_2 = (TextView) view.findViewById(R.id.bottom_tv1_2);
        buttomTv2_2 = (TextView) view.findViewById(R.id.bottom_tv2_2);
        //进度
        progressBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ProcessorProgressListActivity.class);
                intent1.putExtra(KeyConstant.id, chooseId);
                intent1.putExtra(KeyConstant.TITLE, buildSiteName == null ? "" : buildSiteName);
                context.startActivity(intent1);
            }
        });

        //质量
        qualityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportReformListActivity.class);
                intent.putExtra(KeyConstant.id, chooseId);
                intent.putExtra(KeyConstant.isProgress, false);//进度
                intent.putExtra(KeyConstant.isOverdue, true);//进度
                intent.putExtra(KeyConstant.reportIdicateType, KeyConstant.TYPE_REPORT);
                context.startActivity(intent);
            }
        });

        //安全
        materailTv = (TextView) view.findViewById(R.id.materail_tv);
        flagTv = (TextView) view.findViewById(R.id.flag_tv);
        materailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafyListActivity.class);
                intent.putExtra(KeyConstant.id, chooseId);
                intent.putExtra(KeyConstant.TITLE, "安全防护用品");
                intent.putExtra(KeyConstant.tab_position, 2);
                context.startActivity(intent);
            }
        });
        flagTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafyListActivity.class);
                intent.putExtra(KeyConstant.id, chooseId);
                intent.putExtra(KeyConstant.TITLE, "警示标志");
                intent.putExtra(KeyConstant.tab_position, 3);
                context.startActivity(intent);
            }
        });

        topProjectNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.AUTH_TYPE == App.CHOOSE_AUTH_TYPE) {
                } else {
                    showTopDialog(contractBulidSitesList);
                }

            }
        });

        overdueItemRGProgress = (ExRadioGroup) view.findViewById(R.id.overdue_item_radio_group0);
        overdueItemRG = (ExRadioGroup) view.findViewById(R.id.overdue_item_radio_group);

        planDesignInfoTv.setOnClickListener(infoListener);

        tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.setMargins(35, 0, 0, 0);
        tvParams.width = context.getResources().getDimensionPixelOffset(R.dimen.dm120);
        px43 = context.getResources().getDimensionPixelOffset(R.dimen.dm043);
        tvParams.height = px43;

    }

    //获取权限数据列表
    private void getAuthData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_data;
        Response.Listener<ChooseDataInfo> successListener = new Response
                .Listener<ChooseDataInfo>() {
            @Override
            public void onResponse(ChooseDataInfo result) {
                if (result == null || result.getData() == null) {
                    return;
                }
                if (result.getData().size() == 1) {
                    topProjectNameTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                } else {
                    Drawable icNext = context.getResources().getDrawable(R.drawable.ic_next_down);
                    topProjectNameTv.setCompoundDrawablesWithIntrinsicBounds(null, null, icNext, null);
                    topProjectNameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (App.AUTH_TYPE == App.CHOOSE_AUTH_TYPE) {
                                Intent intent = new Intent(context, ChooseProjectActivity.class);
                                context.startActivity(intent);
                                context.finish();
                            } else {
                                showTopDialog(contractBulidSitesList);
                            }

                        }
                    });
                }


            }
        };

        Request<ChooseDataInfo> versionRequest = new
                GsonRequest<ChooseDataInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<ChooseDataInfo>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void back2Top() {
        spEdit.putInt(KeyConstant.SP_CHOOSE_ID, belowContractProjectId);
        spEdit.putInt(AuthsConstant.CHOOSE_AUTH_TYPE, App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? Constant.CONTRACT : App.CHOOSE_AUTH_TYPE == Constant.CONTRACT ? Constant.PROJECT : Constant.PROJECT);
        spEdit.putString(KeyConstant.SP_PROJECT_IMG_URL, "");
        spEdit.putBoolean(KeyConstant.IS_FIRST_LUNCHER_SP, false);
        spEdit.commit();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //工程进度
    private void getProgressData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        String url = Constant.WEB_SITE + urlInfoType + "/" + chooseId + "/progress";
        Log.d(TAG, "统计:" + url);
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (result == null) {
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            //进度
                            processProgress = jsonObject.getDouble(KeyConstant.completedPercentage);
                            pb0Tv.setText(String.format("%.1f", processProgress) + "%");
                            pb0.setProgress((int) processProgress);


                            leftPeriodDay = (int) jsonObject.getDouble(KeyConstant.restPeriod);
                            progressStatus = jsonObject.getString(KeyConstant.progress);

                            String str0 = "整体进度：" + (progressStatus == null ? "未知" : progressStatus);
                            String str1 = "剩余工期：" + leftPeriodDay + "天";
                            totalProgress0.setText(str0);
                            leftPeriodTv.setText(str1);
                            int ongoingProcessorCnt = jsonObject.getInt(KeyConstant.ongoingProcessorCnt);
                            onGoingProcessorCntTv.setText("施工中工序：" + ongoingProcessorCnt + "个");
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                return params;
            }
        };

        App.requestQueue.add(jsonObjRequest);
    }

    private void setContratsQualityItems(final List<StatInfo> result) {
        for (final StatInfo qualityBean : result) {
            if (qualityBean.getTotal() > 0) {
                final TextView tv = new TextView(context);
                final int qualityBeanId = qualityBean.getId();
                final String name = qualityBean.getName();
                String pocessorName = "";
                if (name.length() > 4) {
                    pocessorName = name.substring(0, 4) + "...";
                } else {
                    pocessorName = name;
                }
                tv.setText(pocessorName);
                tv.setLayoutParams(tvParams);
                tv.setSingleLine();
                tv.setIncludeFontPadding(false);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(14f);
                tv.setTextColor(context.getResources().getColor(R.color.white));
                tv.setBackgroundResource(R.drawable.shape_yellow_5px);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, QualityOverActivity.class);
                        intent.putExtra(KeyConstant.id, qualityBeanId + "");
                        intent.putExtra(KeyConstant.name, name);
                        context.startActivity(intent);

                    }
                });
                overdueItemRG.addView(tv);
            }
        }
        final ViewGroup.LayoutParams pp = overdueItemRG.getLayoutParams();
        pp.height = px43;
        overdueItemRG.setLayoutParams(pp);
        qualityOverOpen.setSelected(false);
        qualityOverOpen.setVisibility(overdueItemRG.getChildCount() > 3 ? View.VISIBLE : View.GONE);
        qualityOverLayout.setVisibility(overdueItemRG.getChildCount() > 0 ? View.VISIBLE : View.GONE);
        qualityOverOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示多行
                pp.height = v.isSelected() ? px43 : ViewGroup.LayoutParams.WRAP_CONTENT;
                overdueItemRG.setLayoutParams(pp);
                v.setSelected(!v.isSelected());
            }
        });
    }

    private void setQualityItemsData(final List<QualityReportBean.ContentBean> result) {
        for (final QualityReportBean.ContentBean qualityBean : result) {
            final TextView tv = new TextView(context);
            final int pocessorId = qualityBean.getId();
            final QualityReportBean.ContentBean.BizProcessorBean.BizProcessorConfigBean bizProcessorConfig = qualityBean.getBizProcessor().getBizProcessorConfig();
            final String name = bizProcessorConfig.getName();
            String pocessorName = "";
            if (name.length() > 4) {
                pocessorName = name.substring(0, 4) + "...";
            } else {
                pocessorName = name;
            }
            tv.setText(pocessorName);
            tv.setLayoutParams(tvParams);
            tv.setSingleLine();
            tv.setIncludeFontPadding(false);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(14f);
            tv.setTextColor(context.getResources().getColor(R.color.white));
            tv.setBackgroundResource(R.drawable.shape_yellow_5px);
            final String checkor = "检查人：" + qualityBean.getChecker() + "\b\b\b" + "检查时间：" + TextUtil.substringTime(qualityBean.getCheckTime());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QualityDetailActivity.class);
                    intent.putExtra(KeyConstant.id, pocessorId);
                    intent.putExtra(KeyConstant.type, 0);
                    intent.putExtra(KeyConstant.isOverdue, true);
                    intent.putExtra(KeyConstant.processorConfigId, bizProcessorConfig.getId());
                    intent.putExtra(KeyConstant.name, name);
                    intent.putExtra(KeyConstant.time_name, checkor);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) qualityBean);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });
            overdueItemRG.addView(tv);
        }
        final ViewGroup.LayoutParams pp = overdueItemRG.getLayoutParams();
        pp.height = px43;
        overdueItemRG.setLayoutParams(pp);
        qualityOverOpen.setSelected(false);
        qualityOverOpen.setVisibility(result.size() > 3 ? View.VISIBLE : View.GONE);
        qualityOverOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示多行
                pp.height = v.isSelected() ? px43 : ViewGroup.LayoutParams.WRAP_CONTENT;
                overdueItemRG.setLayoutParams(pp);
                v.setSelected(!v.isSelected());
            }
        });
    }

    private void getOverdueContratsQuality() {
        String url = Constant.WEB_SITE + urlInfoType + "/" + chooseId + "/quality/stat";//2待整改
        Response.Listener<StatData> successListener =
                new Response.Listener<StatData>() {
                    @Override
                    public void onResponse(StatData result) {
                        overdueItemRG.removeAllViews();
                        List<StatInfo> data = result.getData();
                        if (data == null || data.size() == 0) {
                            qualityOverLayout.setVisibility(View.GONE);
                            return;
                        }
                        //汇报数据
                        setContratsQualityItems(data);
                    }
                };

        Request<StatData> request = new GsonRequest<StatData>(Request.Method.GET,
                url, successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.d(TAG, "返回数据"+volleyError.getCause());
            }
        }, new TypeToken<StatData>() {
        }.getType()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                return params;
            }
        };
        App.requestQueue.add(request);
    }

    private void getOverdueBuidSiteQuality() {
        String url = Constant.WEB_SITE + "/biz/qualities/all?" + urlOverQualityType + chooseId + "&status=" + 2;//2待整改
        Response.Listener<List<QualityReportBean.ContentBean>> successListener =
                new Response.Listener<List<QualityReportBean.ContentBean>>() {
                    @Override
                    public void onResponse(List<QualityReportBean.ContentBean> data) {
                        overdueItemRG.removeAllViews();
                        if (data == null || data.size() == 0) {
                            qualityOverLayout.setVisibility(View.GONE);
                            return;
                        }
                        qualityOverLayout.setVisibility(View.VISIBLE);

                        //汇报数据
                        setQualityItemsData(data);
                    }
                };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };

        Request<List<QualityReportBean.ContentBean>> request =
                new GsonRequest<List<QualityReportBean.ContentBean>>(Request.Method.GET,
                        url,
                        successListener, errorListener, new TypeToken<List<QualityReportBean.ContentBean>>() {
                }.getType()) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(request);
    }

    private void getOverdueDataProgress() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE
                + urlOverProgressType + chooseId + "&progressType=" + "overdue";//"&end=" + endDate
        Response.Listener<ProgressOverdueObj> successListener = new Response
                .Listener<ProgressOverdueObj>() {
            @Override
            public void onResponse(ProgressOverdueObj result) {
                overdueItemRGProgress.removeAllViews();
                View progressOverLayout = view.findViewById(R.id.progress_over_layout);
                if (result == null || result.getData().size() == 0) {
                    progressOverLayout.setVisibility(View.GONE);
                    return;
                }
                progressOverLayout.setVisibility(View.VISIBLE);
                List<ProgressOverdueInfo> data = result.getData();

                setProgressItemsData(data);
            }
        };

        Request<ProgressOverdueObj> versionRequest = new
                GsonRequest<ProgressOverdueObj>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<ProgressOverdueObj>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void getSecurityData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + urlInfoType + "/" + chooseId + "/security";
        Response.Listener<List<SafyInfo>> successListener = new Response
                .Listener<List<SafyInfo>>() {
            @Override
            public void onResponse(List<SafyInfo> result) {
                if (result == null || result.size() == 0 || null == result.get(0)) {
                    ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                    return;
                }
                setSafyData(result);
            }
        };

        Request<List<SafyInfo>> versionRequest = new
                GsonRequest<List<SafyInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                    }
                }, new TypeToken<List<SafyInfo>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);

    }

    private void setProgressItemsData(List<ProgressOverdueInfo> result) {
        for (ProgressOverdueInfo itemInfo : result) {
            final TextView tv = new TextView(context);
            final int pocessorId = itemInfo.getId();
            String pocessorName = "";
            final String name = itemInfo.getName();
            if (name.length() > 4) {
                pocessorName = name.substring(0, 4) + "...";
            } else {
                pocessorName = name;
            }
            tv.setText(pocessorName);
            tv.setLayoutParams(tvParams);
            tv.setGravity(Gravity.CENTER);
            tv.setSingleLine();
            tv.setTextSize(14f);
            tv.setIncludeFontPadding(false);
            tv.setTextColor(context.getResources().getColor(R.color.white));
            tv.setBackgroundResource(R.drawable.shape_yellow_5px);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProcessorProgressDetailActivity.class);
                    intent.putExtra(KeyConstant.TITLE, name);
                    intent.putExtra(KeyConstant.id, pocessorId);
                    context.startActivity(intent);
                }
            });
            overdueItemRGProgress.addView(tv);
        }

        final ViewGroup.LayoutParams pp = overdueItemRGProgress.getLayoutParams();
        pp.height = px43;
        overdueItemRGProgress.setLayoutParams(pp);
        progressOverOpen.setSelected(false);
        progressOverOpen.setVisibility(result.size() > 3 ? View.VISIBLE : View.GONE);
        progressOverOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = v.isSelected();
                pp.height = selected ? px43 : ViewGroup.LayoutParams.WRAP_CONTENT;
                overdueItemRGProgress.setLayoutParams(pp);
                v.setSelected(!selected);
            }
        });
    }

    //安全
    private void setSafyData(List<SafyInfo> result) {
        ImageView materailTagIv = (ImageView) view.findViewById(R.id.materail_tv_tag);
        ImageView flagTagIv = (ImageView) view.findViewById(R.id.flag_tag_iv);
        ImageView checkTagIv = (ImageView) view.findViewById(R.id.safy_check_tag);
        for (SafyInfo safyInfo : result) {
            String type = safyInfo.getType();
            String value = safyInfo.getValue();
            if (Constant.train.equals(type)) {//安全教育
                trainNum = value == null ? 0 : Integer.valueOf(value);
            } else if (Constant.accident.equals(type)) {//事故
                accidentNum = value == null ? 0 : Integer.valueOf(value);

            } else if (Constant.check.equals(type)) {//排查
                if (value == null) {
                    checkTagIv.setImageResource(R.drawable.ic_monitor_safety_nonchecked);
                } else {
                    checkTagIv.setSelected("2".equals(value));
                }
            } else if (Constant.material.equals(type)) {//防护用品
                if (value == null) {
                    materailTagIv.setImageResource(R.drawable.ic_monitor_safety_nonchecked);
                } else {
                    materailTagIv.setSelected("2".equals(value));

                }
            } else if (Constant.flag.equals(type)) {//标志
                if (value == null) {
                    flagTagIv.setImageResource(R.drawable.ic_monitor_safety_nonchecked);
                } else {
                    flagTagIv.setSelected("2".equals(value));
                }
            }
        }
        ((TextView) view.findViewById(R.id.safy_accident_numb_tv)).setText(accidentNum + "次");//事故
        ((TextView) view.findViewById(R.id.safy_train_numb_tv)).setText("本月" + trainNum + "次");//教育
        setSafyTv(buttomTv1_2, "安全事故", true);
        setSafyTv(buttomTv0_2, "安全教育", true);
        setSafyTv(buttomTv2_2, "安全隐患排查", true);
    }

    private void setSafyTv(TextView textView, final String title, boolean clickAble) {
        if (clickAble) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = v == buttomTv0_2 ? 0 : v == buttomTv1_2 ? 1 : 4;
                    Intent intent = new Intent(context, SafyListActivity.class);
                    intent.putExtra(KeyConstant.id, chooseId);
                    intent.putExtra(KeyConstant.TITLE, title);
                    intent.putExtra(KeyConstant.tab_position, position);//教育,事故,检查
                    context.startActivity(intent);
                }
            });
        }
    }


    //获取质量
    private void getQualityThingsNumsData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + urlInfoType + "/" + chooseId + "/counter";

        Response.Listener<CounterInfo> successListener = new Response
                .Listener<CounterInfo>() {
            @Override
            public void onResponse(CounterInfo result) {

                if (result == null) {
                    return;
                }
                setQualityInfo(result);
            }
        };

        Request<CounterInfo> versionRequest = new GsonRequest<CounterInfo>(
                Request.Method.GET, url,
                successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //获取质量统计个数失败
            }
        }, new TypeToken<CounterInfo>() {
        }.getType()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }

    //质量
    private void setQualityInfo(CounterInfo result) {
        //待审核
        pendingApproval = result.getPendingApproval();
        if (pendingApproval > 0) {
            thingsNumTv.setVisibility(View.VISIBLE);
            thingsNumTv.setText(pendingApproval + "");
        }
        //质检总数
        totalQuality = result.getTotalQuality();
        //待整改
        unreformedQuality = result.getUnreformedQuality();
        //通过数量
        confirmedQuality = result.getConfirmedQuality();
        passingRate = ((confirmedQuality / (double) totalQuality) * 100);
        qualityTotalTv.setText("质检总数量：" + totalQuality + "个");
        buttomTv1.setText("待整改数量：" + unreformedQuality + "个");
        buttomTv2.setText("质量通过数量：" + confirmedQuality + "个");
        pb.setProgress((int) passingRate);
        pbTv.setText(Double.isNaN(passingRate) ? "0.0%" : String.format("%.1f", passingRate) + "%");
    }

    //获取工地列表
    private void getBuildSiteInfo() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + urlInfoType + "/" + chooseId;

        Response.Listener<BuildSiteInfo> successListener = new Response
                .Listener<BuildSiteInfo>() {
            @Override
            public void onResponse(BuildSiteInfo result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null) {
                    return;
                }
                //设置数据
                buildSiteName = result.getName();
                type = result.getType();

                belowContractProjectId = result.getBizContract().getId();
                double invest = result.getInvest();
                String unitNameCn = result.getConstructionUnit().getNameCn();
                String planBeginDate = TextUtil.substringTime(result.getPlanBeginDate());
                String planEndDate = result.getPlanEndDate();
                setTopInfoView(result.getPic(), invest, unitNameCn, planBeginDate, planEndDate);
            }
        };

        Request<BuildSiteInfo> versionRequest = new
                GsonRequest<BuildSiteInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();

                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<BuildSiteInfo>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);

    }

    //设置顶部数据
    private void setTopInfoView(List<FileInfo> pic, double invest, String unitNameCn, String
            planBeginDate, String planEndDate) {
        sp.edit().putString(KeyConstant.SP_MAIN_NAME, buildSiteName).commit();
        //图片
        if (pic != null && pic.size() > 0) {
            PROJECT_IMG_URL = UrlConstant.URL_FILE_HEAD_IMG + pic.get(0).url;
            sp.edit().putString(KeyConstant.SP_PROJECT_IMG_URL, PROJECT_IMG_URL).commit();
            buildSitePicIv.setImageURI(PROJECT_IMG_URL);
        } else {
            buildSitePicIv.setImageURI("");
        }

        int period = 0;
        try {
            Date startTime = DateUtil.getFormat().parse(planBeginDate);
            Date endTime = DateUtil.getFormat().parse(planEndDate);
            period = TextUtil.differentDaysByMillisecond2(endTime, startTime);
        } catch (ParseException e) {
        }

        buildSiteNameTv.setText(buildSiteName);
        topProjectNameTv.setText(buildSiteName);
        belowContractNameTv.setText(unitNameCn);
        constructUnitNameTv.setText("总投资" + invest + "万元\n" + planEndDate + "完工   总工期" + period + "天");

        //获取标段/工地列表
        getContractBulidSitesList();
    }

    private View.OnClickListener infoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PlanDesignActivity.class);
            intent.putExtra(KeyConstant.project_plan_title, buildSiteName);
            intent.putExtra(KeyConstant.id, chooseId);
            intent.putExtra(KeyConstant.project_plan_info_type, Integer.valueOf(type));
            context.startActivity(intent);
        }
    };

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

    private void showTopDialog(List<Contact> contractBulidSitesList) {
        if (contractBulidSitesList == null) {
            ToastUtil.show(context, "列表数据为空");
            return;
        }
        final Dialog dialog = new Dialog(context, R.style.dialog_top_to_bottom);
        //填充对话框的布局

        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_road_map_top, null);
        ExRadioGroup topLayout = (ExRadioGroup) inflate.findViewById(R.id.road_map_layout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 0, 0, 0);
        for (Contact buildSiteInfo : contractBulidSitesList) {
            final String name = buildSiteInfo.getName();
            final int infoId = buildSiteInfo.getId();
            TextView itemTv = new TextView(context);
            itemTv.setGravity(Gravity.CENTER_VERTICAL);
            itemTv.setPadding(35, 8, 35, 10);
            itemTv.setTextSize(15.5f);
            itemTv.setText(name);
            itemTv.setSingleLine();
            itemTv.setTextColor(context.getResources().getColor(R.color.color666666));
            itemTv.setBackgroundResource(R.drawable.selector_dialog_tab_bg);
            itemTv.setLayoutParams(lp);
            topLayout.addView(itemTv);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseId = infoId + "";
                    buildSiteName = name;
                    spEdit.putInt(KeyConstant.SP_CHOOSE_ID, infoId).commit();
                    getData();
                    dialog.dismiss();
                }
            });
        }

        dialog.setContentView(inflate);//将布局设置给Dialog

        DialogUtils.setDialogWindow(context, dialog, Gravity.TOP);
    }

    private boolean isExit = false;

    public void exitBy2Click() {
        if (App.AUTH_TYPE == App.CHOOSE_AUTH_TYPE
                || App.CHOOSE_AUTH_TYPE == Constant.PROJECT) {
            if (!isExit) {
                isExit = true;
                ToastUtil.show(context, "再点一次退出");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                context.finish();
            }
        } else {
            back2Top();
        }

    }
}
