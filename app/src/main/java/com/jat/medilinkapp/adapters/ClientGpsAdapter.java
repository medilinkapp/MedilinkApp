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
import com.jat.medilinkapp.R;
import com.jat.medilinkapp.model.entity.ClientGps;
import java.util.ArrayList;

public class ClientGpsAdapter extends RecyclerView.Adapter<ClientGpsAdapter.ViewHolderNfcData> {

    ArrayList<ClientGps> list;
    ClientGpsAdapter.DialogListener dialogListener;

    public ClientGpsAdapter(ArrayList<ClientGps> list, ClientGpsAdapter.DialogListener dialogListener) {
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

        holder.imgDelete.setOnClickListener((v -> {
            dialogListener.onFinishSelectionDataClientGpsDelete(clientGps);
        }));

        if (clientGps.getLatitude() != null && clientGps.getLongitude() != null) {
            holder.imgStatus.setImageResource(R.drawable.ic_check_circle_nfc);
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_error_red_42);
        }
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
        private ImageView imgStatus;

        public ViewHolderNfcData(@NonNull View v) {
            super(v);
            tvClientGps = (TextView) v.findViewById(R.id.tv_client_gps);
            imgDelete = (ImageView) v.findViewById(R.id.img_delete_client);
            imgStatus = (ImageView) v.findViewById(R.id.img_gps_status);
        }
    }

    public interface DialogListener {
        void onFinishSelectionDataClientGpsDelete(ClientGps clientGps);

        void onDeleteDataClientGps();
    }
}
