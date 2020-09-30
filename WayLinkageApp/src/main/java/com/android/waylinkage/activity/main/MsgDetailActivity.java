package com.android.waylinkage.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.MsgInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.widget.TouchImageView;
import com.google.gson.reflect.TypeToken;

import org.xml.sax.XMLReader;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * 游戏列表
 * Gool
 */
public class MsgDetailActivity extends BaseFgActivity {

    private MsgDetailActivity context;
    private int id;
    private String title;
    private TextView mDescTv;
    private TextView publishTv;
    private DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);

        initStatusBar();
        context = this;

        id = getIntent().getIntExtra(KeyConstant.id, 0);
        title = getIntent().getStringExtra(KeyConstant.TITLE);
        initTitleBackBt(title);

        initView();
        getMsgData();
    }

    private void initView() {
        TextView titleTv = (TextView) findViewById(R.id.msg_detail_title_tv);
        titleTv.setText(title);

        mDescTv = (TextView) findViewById(R.id.msg_detail_content_tv);
        publishTv = (TextView) findViewById(R.id.msg_detail_publish_tv);
    }

    private void getMsgData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert(getString(R.string.loading), true);
        String url = Constant.WEB_SITE + "/biz/contents/" + id;
        Response.Listener<MsgInfo> successListener = new Response
                .Listener<MsgInfo>() {
            @Override
            public void onResponse(MsgInfo result) {
                if (result == null) {
                    if (null != context && !context.isFinishing()) {
                        dialogHelper.hideAlert();
                    }
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                setViewData(result);
            }
        };

        Request<MsgInfo> versionRequest = new
                GsonRequest<MsgInfo>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                    }
                }, new TypeToken<MsgInfo>() {
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

    private void setViewData(MsgInfo result) {
        //发布人,时间
        String time = result.getPublishTime();
        String publisherName = result.getPublisherName();
        publishTv.setText(publisherName == null ? "" : publisherName + "\n" + (time == null ? "" : TextUtil.substringTime(time)));

        final String content = result.getContent();
        if (content == null || !content.contains("<img src=")) {//不包含图片
            if (null != context && !context.isFinishing()) {
                dialogHelper.hideAlert();
            }
        }
        mDescTv.setText(Html.fromHtml(content.replace("<br />", ""), new HtmlImageGetter(), new MyTagHandler()));
        mDescTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class HtmlImageGetter implements Html.ImageGetter {
        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable d = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(
                    R.color.gray_1);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, ImageUtil.getScreenWidth(context),
                    0);
            new LoadImage().execute(source, d);
            return d;
        }

        /**
         * 异步下载图片类
         */
        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

            private LevelListDrawable mDrawable;

            @Override
            protected Bitmap doInBackground(Object... params) {

                String urlBase64 = (String) params[0];
                mDrawable = (LevelListDrawable) params[1];
                return ImageUtil.base64UrlStrToBitmap(urlBase64);
            }


            /**
             * 图片下载完成后执行
             */
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null && context != null) {
                    mDrawable.addLevel(1, 1, new BitmapDrawable(bitmap));
                    /**
                     * 适配图片大小 <br/>
                     * 默认大小：bitmap.getWidth(), bitmap.getHeight()<br/>
                     * 适配屏幕：getDrawableAdapter
                     */
                    mDrawable = getDrawableAdapter(context, mDrawable,
                            bitmap.getWidth(), bitmap.getHeight());

                    // mDrawable.setBounds(0, 0, bitmap.getWidth(),
                    // bitmap.getHeight());

                    mDrawable.setLevel(1);

                    /**
                     * 图片下载完成之后重新赋值textView<br/>
                     * mtvActNewsContent:我项目中使用的textView
                     *
                     */
                    mDescTv.invalidate();
                    CharSequence t = mDescTv.getText();
                    mDescTv.setText(t);
                }
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
            }

            /**
             * 加载网络图片,适配大小
             *
             * @param context
             * @param drawable
             * @param oldWidth
             * @param oldHeight
             * @return
             * @author Ruffian
             * @date 2016年1月15日
             */
            public LevelListDrawable getDrawableAdapter(Activity context,
                                                        LevelListDrawable drawable, int oldWidth,
                                                        int oldHeight) {
                LevelListDrawable newDrawable = drawable;
                newDrawable.setBounds(0, 0, oldWidth, oldHeight);
                return newDrawable;
            }
        }

    }

    public class MyTagHandler implements Html.TagHandler {

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            // 处理标签<img>
            if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                // 获取长度
                int len = output.length();
                // 获取图片地址
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgURL = images[0].getSource();
                // 使图片可点击并监听点击事件

                output.setSpan(new ClickableImage(context, imgURL), len - 1, len,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private class ClickableImage extends ClickableSpan {
            private String url;
            private Context context;

            public ClickableImage(Context context, String url) {
                this.context = context;
                this.url = url;
            }

            @Override
            public void onClick(View widget) {
                // 进行图片点击之后的处理
                Bitmap bitmap = ImageUtil.base64UrlStrToBitmap(url);
                showPicDialog(bitmap);
            }
        }
    }

    private void showPicDialog(Bitmap picUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_fullscree_animation);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_file_detail, null);
        TouchImageView fileDetailSDV = (TouchImageView) v.findViewById(R.id
                .card_detail_file_sdv);
        v.findViewById(R.id.loading_view).setVisibility(View.GONE);
        fileDetailSDV.setImageBitmap(picUrl);

        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);

        fileDetailSDV.setOnImageClickListener(new TouchImageView.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
    }


}