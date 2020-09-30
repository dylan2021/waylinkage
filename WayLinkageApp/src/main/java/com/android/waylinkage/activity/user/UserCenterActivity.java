package com.android.waylinkage.activity.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.activity.LoginActivity;
import com.android.waylinkage.util.CommonUtil;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.FileUtil;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.Utils;
import com.android.waylinkage.widget.dialogfragment.OneBtDialogFragment;
import com.android.waylinkage.exception.NoSDCardException;
import com.android.waylinkage.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户中心界面
 * Gool  Lee
 */
public class UserCenterActivity extends BaseFgActivity {

    public String TAG = UserCenterActivity.class.getSimpleName();
    private UserCenterActivity context;
    private String pwd;
    private SimpleDraweeView img_photo;
    private TextView mAddressTv, mConnectPhoneTv;
    private EditText mPhoneTv;
    private String nickName, phone;
    private SharedPreferences preferences;
    private int REQUEST_CODE_CAPTURE_CAMERA = 1458;
    private String mCurrentPhotoPath;
    private File mTempDir;
    private String imgStrPost = "";
    private String avatarUrl;
    private SharedPreferences.Editor editor;
    private Dialog defAvatarDialog;
    private FragmentManager fm;
    private Uri fileUri;
    private RelativeLayout imgPhotoLayout;
    private Button titleRightBt;
    private JSONObject jsonObject;
    private EditText mNameTv;
    private String userId = "";
    private Button commitBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        this.setContentView(R.layout.activity_user_center);
        context = this;
        fm = getSupportFragmentManager();
        initTitleBackBt(getString(R.string.me_profile));

        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        editor = preferences.edit();


        try {
            mTempDir = new File(CommonUtil.getImageBasePath());
        } catch (NoSDCardException e) {
            e.printStackTrace();
        }
        if (mTempDir != null && !mTempDir.exists()) {
            mTempDir.mkdirs();
        }
        img_photo = (SimpleDraweeView) findViewById(R.id.img_photo);
        commitBt =  findViewById(R.id.commit_bt);
        imgPhotoLayout = (RelativeLayout) findViewById(R.id.img_photo_layout);

        setData();

        imgPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改头像
                ToastUtil.show(context, "暂不支持修改头像");
                //showChangeAvatarDialog();
            }
        });

        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = mPhoneTv.getText().toString();
                nickName = mNameTv.getText().toString();
                if (TextUtil.isEmpty(nickName)) {
                    ToastUtil.show(context, "昵称不能为空");
                    return;
                }
                if (TextUtil.isEmpty(phone)) {
                    ToastUtil.show(context, "手机号不能为空");
                    return;
                }
                if (!TextUtil.isMobile(phone)) {
                    ToastUtil.show(context, "请输入正确的手机号");
                    return;
                }
                changeData();
            }
        });
        titleRightBt = (Button) findViewById(R.id.title_right_bt);
        titleRightBt.setText("编辑");
        titleRightBt.setVisibility(View.VISIBLE);
        titleRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                commitBt.setVisibility(View.VISIBLE);
                mPhoneTv.setFocusable(true);
                mPhoneTv.setFocusableInTouchMode(true);

                mNameTv.setFocusable(true);
                mNameTv.setFocusableInTouchMode(true);
            }
        });

        //默认头像地址
        for (int i = 1; i < 21; i++) {
            if (i < 10) {
                mUrlList.add(UrlConstant.RECOMMED_URL_START + "0" + i + ".png");
            } else {
                mUrlList.add(UrlConstant.RECOMMED_URL_START + i + ".png");
            }
        }
        defAvatarDialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_recommend_avatar,
                null);
        GridView gridView = (GridView) inflate.findViewById(R.id.recommend_grid_view);
        gridView.setAdapter(new AvatarAdapter());
        defAvatarDialog.setContentView(inflate);//将布局设置给Dialog
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imgStrPost = mUrlList.get(position);
                img_photo.setImageURI(imgStrPost);
                defAvatarDialog.dismiss();
            }
        });
    }

    private void setData() {
        mPhoneTv = (EditText) findViewById(R.id.tv_phone);
        mNameTv = (EditText) findViewById(R.id.tv_nickname);
        mAddressTv = (TextView) findViewById(R.id.profile_email_tv);
        mConnectPhoneTv = (TextView) findViewById(R.id.connect_phone_tv);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra(KeyConstant.employee));
            userId = jsonObject.getString(KeyConstant.id);
            mNameTv.setText(jsonObject.getString(KeyConstant.employeeName));
            String phoneStr = jsonObject.getString(KeyConstant.employeePhone);
            mPhoneTv.setText("null".equals(phoneStr) ? "-" : phoneStr);
            mAddressTv.setText(jsonObject.getString(KeyConstant.regionCode));
            //mConnectPhoneTv.setText(jsonObject.getString(KeyConstant.employeeMobile));
        } catch (JSONException e) {
            ToastUtil.show(context, getString(R.string.request_failed_retry_later));
        }

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            avatarUrl = uri.toString();
            img_photo.setImageURI(avatarUrl);

            String path = uri.getPath();
            //File file = new File(path);
            imgStrPost = ImageUtil.getImageStr(path);
            android.util.Log.d(TAG, path + "修改参数:图片地址:" + imgStrPost);
        } else if (resultCode == Crop.RESULT_ERROR) {
            //Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //修改头像
    public void showChangeAvatarDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_change_avatar,
                null);

        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                int id = v.getId();
                if (id == R.id.choose_local_tv) {//本地相册
                    Crop.pickImage(context);
                } else if (id == R.id.choose_camera_tv) {//相机
                    getImageFromCamera();
                } else if (id == R.id.choose_recomend_tv) {
                    //选择推荐头像
                    setDialogWindow(defAvatarDialog);
                }
            }
        };
        inflate.findViewById(R.id.choose_local_tv).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.choose_recomend_tv).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.choose_camera_tv).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.choose_cancel_tv).setOnClickListener(mDialogClickLstener);

        dialog.setContentView(inflate);//将布局设置给Dialog
        setDialogWindow(dialog);
    }

    private List<String> mUrlList = new ArrayList<>();

    public void onProfilePhoneBtClick(View view) {
        Intent intent = new Intent(context, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.PHONE);
        startActivity(intent);
    }


    //默认头像适配器
    public class AvatarAdapter extends BaseAdapter {
        public AvatarAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return mUrlList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUrlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            AvatarAdapter.ViewHolder holder;
            if (convertView == null) {
                holder = new AvatarAdapter.ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout
                        .layout_avatar_item, null);
                holder.mIconIv = (SimpleDraweeView) convertView.findViewById(R.id
                        .recommend_icon_gv_iv);
                convertView.setTag(holder);
            } else {
                holder = (AvatarAdapter.ViewHolder) convertView.getTag();
            }
            final String uriString = mUrlList.get(position);
            holder.mIconIv.setImageURI(uriString);
            return convertView;
        }

        class ViewHolder {
            private SimpleDraweeView mIconIv;
        }
    }

    private void setDialogWindow(Dialog dialog) {
        Window dialogWindow = dialog.getWindow(); //获取当前Activity所在的窗体
        dialogWindow.setGravity(Gravity.BOTTOM);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams params = dialogWindow.getAttributes();   //获得窗体的属性
        //params.y = 20;  Dialog距离底部的距离
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置Dialog距离底部的距离
        dialogWindow.setAttributes(params); //将属性设置给窗体
        dialog.show();//显示对话框
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            } else if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
                if (fileUri != null) {
                    beginCrop(fileUri);
                }
            }
        }
    }

    private void beginCrop(Uri source) {
        String fileName = "Temp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File cropFile = new File(mTempDir, fileName);
        Uri outputUri = Uri.fromFile(cropFile);
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    protected void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "Temp_camera" + String.valueOf(System.currentTimeMillis());
        File cropFile = new File(mTempDir, fileName);
        fileUri = FileUtil.getUriForFile(context, cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

        mCurrentPhotoPath = fileUri.getPath();
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
    }


    private void changeData() {
        editor.putBoolean(KeyConstant.AVATAR_HAS_CHANGED, true).apply();

        JSONObject j = new JSONObject();
        try {
            j.put(KeyConstant.id, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.dept, j);
        map.put(KeyConstant.id, userId);
        map.put(KeyConstant.employeeName, nickName);
        map.put(KeyConstant.employeePhone, phone);
        map.put(KeyConstant.employeeMobile, phone);
        map.put(KeyConstant.regionCode, mAddressTv.getText().toString());

        try {
            map.put(KeyConstant.employeeBirthday, jsonObject.getString(KeyConstant.employeeBirthday));
            map.put(KeyConstant.employeeEmail, jsonObject.getString(KeyConstant.employeeEmail));
            map.put(KeyConstant.employeeSex, jsonObject.getString(KeyConstant.employeeSex));
            map.put(KeyConstant.employeeStatus, jsonObject.getString(KeyConstant.employeeStatus));
            map.put(KeyConstant.employeeOrder, jsonObject.getString(KeyConstant.employeeOrder));
            map.put(KeyConstant.employeeType, jsonObject.getString(KeyConstant.employeeType));
        } catch (Exception e) {
        }


        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + Constant.url_system_employees;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result == null) {
                    ToastUtil.show(context, "修改失败");
                    return;
                }
                DialogHelper.hideWaiting(fm);
                context.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "修改失败" + error);
                DialogHelper.hideWaiting(fm);
                ToastUtil.show(context, getString(R.string.server_exception));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Content_Type, Constant.application_json);
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);

    }

    /**
     * 显示结果对话框
     */
    private void showReLoginDialog() {
        final OneBtDialogFragment dialogFragment = new OneBtDialogFragment();
        dialogFragment.setTitle(R.string.reLogin);
        dialogFragment.setDialogWidth(context.getResources().getDimensionPixelSize(R.dimen
                .unlogin_dialog_width));
        dialogFragment.setNegativeButton(R.string.login_now, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
                context.startActivity(new Intent(context, LoginActivity.class));
                context.finish();
            }
        });
        dialogFragment.show(fm, "successDialog");
    }


}
