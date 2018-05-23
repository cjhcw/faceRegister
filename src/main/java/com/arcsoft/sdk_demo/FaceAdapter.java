package com.arcsoft.sdk_demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FaceAdapter extends ArrayAdapter<User> {

    private int resourceId;

    public FaceAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);
        View view;
        ViewHolder holder=new ViewHolder();
        //获取要显示的列表项视图
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    resourceId, parent, false);
            //从列表项视图获取相应控件
            holder.name = (TextView) view.findViewById(R.id.userName);
            holder.type = (TextView) view.findViewById(R.id.type);
            holder.addtime = (TextView) view.findViewById(R.id.addtime);
            holder.allowdate= (TextView) view.findViewById(R.id.allowdate);
            view.setTag(holder);
        } else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }

        //给各个控件设置要显示内容
        holder.name.setText(user.getName());
        holder.type.setText(user.getType());
        holder.addtime.setText(user.getAddtime());
        holder.allowdate.setText(user.getAllowdate());
        return view;
    }

    class ViewHolder{
        TextView name;
        TextView type;
        TextView addtime ;
        TextView allowdate ;
    }
}

