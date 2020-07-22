package com.example.symptoms;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.widget.TextView;

public class BodySubLocationAdapter extends RecyclerView.Adapter<BodySubLocationAdapter.Holder> {

    private ArrayList<BodySubLocation> list;
    Holder.BodySubLocationClickListener listener;

    public BodySubLocationAdapter(ArrayList<BodySubLocation> list, Holder.BodySubLocationClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv;
        BodySubLocationClickListener listener;
        public Holder(@NonNull View itemView, BodySubLocationClickListener _listener) {
            super(itemView);
            tv = itemView.findViewById(R.id.selection_item);
            listener = _listener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) { listener.onBodySubLocationClick(getAdapterPosition()); }
        public interface BodySubLocationClickListener{ void onBodySubLocationClick(int position);}
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selection_item, viewGroup, false);
        Holder holder = new Holder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        //set title
        holder.tv.setText(list.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
