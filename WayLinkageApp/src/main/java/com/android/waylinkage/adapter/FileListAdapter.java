/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.waylinkage.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.android.waylinkage.widget.TouchImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Gool
 */
public class FileListAdapter extends BaseAdapter {

    private List<FileListInfo> fileList;
    private FragmentActivity context;
    private boolean allowDelete = false;
    private ImageView loadView;

    public FileListAdapter(FragmentActivity c, List<FileListInfo> mFileListData) {
        context = c;
        fileList = mFileListData;
    }

    public void setDate(List<FileListInfo> gameInfoList) {
        this.fileList = gameInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (fileList != null) {
            return fileList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (fileList != null) {
            return fileList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public interface DataRemoveCallBack {
        void finish(List<FileListInfo> data);
    }

    DataRemoveCallBack calback;

    public void setCallBack(DataRemoveCallBack c) {
        calback = c;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (fileList == null || fileList.size() == 0) {
            return null;
        }
        final FileListInfo fileInfo = fileList.get(position);

        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout
                            .item_card_detail_file_list, parent,
                    false);
            holder.filePicIv = (SimpleDraweeView) convertView.findViewById(R.id
                    .card_detail_file_list_item_iv);
            holder.removeBt = (Button) convertView.findViewById(R.id.dialog_btn_sure);
            holder.centerTitleTv = (TextView) convertView.findViewById(R.id.dialog_center_title_tv);
            holder.fileSizeTv = (TextView) convertView.findViewById(R.id.dialog_center_size_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("", "附件:" + fileInfo);
        if (fileInfo != null) {
            //只是查看,不显示删除按钮
            if (fileInfo.fileUrl == Constant.TYPE_SEE) {
                holder.removeBt.setVisibility(View.GONE);
                holder.centerTitleTv.setPadding(0, 35, 0, 0);
            }
            if (allowDelete) {
                holder.removeBt.setVisibility(View.VISIBLE);
            }
            final String fileUrl = fileInfo.filePath;
            Log.d("", "附件:" + fileUrl);
            if (ImageUtil.isImageSuffix(fileUrl)) {
                final String uriString = UrlConstant.URL_FILE_HEAD_IMG + fileUrl;
                holder.filePicIv.setImageURI(uriString);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPicDialog(uriString);
                    }
                });
            } else {
                int resId = null == fileUrl ? R.drawable.ic_file_other : fileUrl.endsWith(".txt") ? R.drawable.ic_txt : fileUrl.endsWith(".pdf") ? R.drawable.ic_pdf :
                        fileUrl.contains(".doc") ? R.drawable.ic_word : R.drawable.ic_file_other;
                holder.filePicIv.setImageResource(resId);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //预览
                        //showWebView(context, UrlConstant.URL_FILE_HEAD_DOC + fileUrl);
                        //调用浏览器打开
                        Utils.openBrowser(context, UrlConstant.URL_FILE_HEAD_DOC + fileUrl);
                    }
                });
            }
            String fileName = fileInfo.fileName == null ? "未知" : fileInfo.fileName;
            holder.centerTitleTv.setText(fileName);
            holder.fileSizeTv.setText(fileInfo.fileSize == 0 ? "" : Formatter.formatFileSize(context, fileInfo.fileSize));

            holder.removeBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileList.remove(fileInfo);
                    calback.finish(fileList);
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    private void showPicDialog(String picUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_fullscree_animation);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.layout_dialog_file_detail, null);
        final TouchImageView fileDetailSDV = (TouchImageView) v.findViewById(R.id
                .card_detail_file_sdv);
        final ImageView loadView = (ImageView) v.findViewById(R.id.loading_view);
        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        loadView.startAnimation(operatingAnim);
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        Picasso.with(context).load(picUrl)
                .error(R.drawable.ic_def_logo_720_288)
                .into(fileDetailSDV, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadView.clearAnimation();
                        loadView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        loadView.clearAnimation();
                        loadView.setVisibility(View.GONE);
                        ToastUtil.show(context, "图片加载失败");
                    }
                });

        fileDetailSDV.setOnImageClickListener(new TouchImageView.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
    }

    public class ViewHolder {
        private SimpleDraweeView filePicIv;
        public Button removeBt;
        public TextView centerTitleTv;
        public TextView fileSizeTv;
    }

}














