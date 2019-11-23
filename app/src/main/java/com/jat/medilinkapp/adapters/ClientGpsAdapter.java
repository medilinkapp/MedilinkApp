package com.jat.medilinkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jat.medilinkapp.BuildConfig;
import com.jat.medilinkapp.MyFragmentDialogClient;
import com.jat.medilinkapp.R;
import com.jat.medilinkapp.model.entity.ClientGps;
import java.util.ArrayList;

public class ClientGpsAdapter extends RecyclerView.Adapter<ClientGpsAdapter.ViewHolderNfcData> {

    ArrayList<ClientGps> list;
    MyFragmentDialogClient.DialogListener dialogListener;

    public ClientGpsAdapter(ArrayList<ClientGps> list, MyFragmentDialogClient.DialogListener dialogListener) {
        this.list = list;
        this.dialogListener = dialogListener;
    }

    public void setList(ArrayList<ClientGps> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderNfcData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_client_gps_layout, parent, false);
        return new ViewHolderNfcData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNfcData holder, int position) {
        ClientGps clientGps = list.get(position);

        TextView tvClientGps = holder.tvClientGps;
        if (BuildConfig.DEBUG) {
            tvClientGps.setText(clientGps.getUid() + " - Client: " + (clientGps.getClientId()
                    + " - Lat:" + clientGps.getLatitude()
                    + " - Long:" + clientGps.getLongitude()));
        } else {
            tvClientGps.setText("Client: " + (clientGps.getClientId()));
        }
        ImageView imgDelete = holder.imgDelete;
        holder.imgDelete.setOnClickListener((v -> {
            dialogListener.onFinishSelectionDataClientGps(clientGps);
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
        private TextView tvClientGps;
        private ImageView imgDelete;

        public ViewHolderNfcData(@NonNull View v) {
            super(v);
            tvClientGps = (TextView) v.findViewById(R.id.tv_client_gps);
            imgDelete = (ImageView) v.findViewById(R.id.img_delete_client);
        }
    }
}
