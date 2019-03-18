package com.yanshao.extractapk.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanshao.extractapk.R;
import com.yanshao.extractapk.bean.AppInfo;

import java.util.List;

public class AppListRecyclerAdapter extends RecyclerView.Adapter<AppListRecyclerAdapter.ViewHolder> {
    private List<AppInfo> appInfos;
    Activity mactivity;
    OnItemCallClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView app_name, app_version, app_packagename;
        ImageView app_icon;
Button bf_btn;
        public ViewHolder(View itemView) {
            super(itemView);
            app_name = (TextView) itemView.findViewById(R.id.app_name);
            app_icon = (ImageView) itemView.findViewById(R.id.app_icon);
            app_version = (TextView) itemView.findViewById(R.id.app_version);
            app_packagename = (TextView) itemView.findViewById(R.id.app_packagename);
            bf_btn = (Button) itemView.findViewById(R.id.bf_btn);
        }
    }

    public AppListRecyclerAdapter(Activity activity, List<AppInfo> appInfoList) {
        this.appInfos = appInfoList;
        mactivity = activity;
    }

    @Override
    public int getItemCount() {

        return appInfos.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.applist_recycler_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        AppInfo appInfo = appInfos.get(position);
        holder.app_icon.setImageDrawable(appInfo.getAppIcon());
        holder.app_name.setText(appInfo.getAppName());
        holder.app_version.setText("版本:"+appInfo.getVersionName());
        holder.app_packagename.setText(appInfo.getPackageName());
        holder. bf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,position);
            }
        });
    }


    public void setOnItemCallClickListener(OnItemCallClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemCallClickListener {

        void onItemClick(View view, int position);

    }


}
