package com.randomringtonemanager;

import android.content.Context;
import android.media.Ringtone;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RingtoneAdaptor extends RecyclerView.Adapter<RingtoneAdaptor.ViewHolder> {
    Context mContext;
    List<Ringtone> ringtonesList;

    public RingtoneAdaptor(Context context, List<Ringtone> ringtones){
        this.mContext = context;
        this.ringtonesList =ringtones;
    }

    @NonNull
    @Override
    public RingtoneAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ringtonelist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RingtoneAdaptor.ViewHolder holder, int position) {
        holder.ringtoneName.setText(ringtonesList.get(position).getTitle(mContext));
    }

    @Override
    public int getItemCount() {
        return ringtonesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ringtoneName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ringtoneName = itemView.findViewById(R.id.ringtoneName);
        }
    }
}
