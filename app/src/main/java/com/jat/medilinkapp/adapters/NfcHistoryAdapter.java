package com.jat.medilinkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jat.medilinkapp.MyDialogHistory;
import com.jat.medilinkapp.R;
import com.jat.medilinkapp.model.entity.NfcData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NfcHistoryAdapter extends RecyclerView.Adapter<NfcHistoryAdapter.ViewHolderNfcData> {

    ArrayList<NfcData> list;
    MyDialogHistory.DialogListener dialogListener;

    public NfcHistoryAdapter(ArrayList<NfcData> list, MyDialogHistory.DialogListener dialogListener) {
        this.list = list;
        this.dialogListener = dialogListener;
    }

    public void setList(ArrayList<NfcData> tasks) {
        this.list = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderNfcData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.nfc_history_item_layout, parent, false);
        return new ViewHolderNfcData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNfcData holder, int position) {
        NfcData nfcData = list.get(position);

        TextView tvNfcTimeStamp = holder.tvNfcTimeStamp;
        tvNfcTimeStamp.setText(nfcData.getCreateDate());

        ImageView imgStatus = holder.imgSendStatus;

        if(nfcData.isSend()){
            imgStatus.setImageResource(R.drawable.ic_check_circle_nfc);
        }else{
            imgStatus.setImageResource(R.drawable.ic_error_red_42);
        }

        holder.itemView.setOnClickListener((v -> {
            dialogListener.onFinishSelectionData(nfcData);
        }));

    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    class ViewHolderNfcData extends RecyclerView.ViewHolder {
        private TextView tvNfcTimeStamp;
        private ImageView imgSendStatus;

        public ViewHolderNfcData(@NonNull View v) {
            super(v);
            tvNfcTimeStamp = (TextView) v.findViewById(R.id.tv_nfc_time_stamp);
            imgSendStatus = (ImageView) v.findViewById(R.id.img_send_status);
        }
    }
}
