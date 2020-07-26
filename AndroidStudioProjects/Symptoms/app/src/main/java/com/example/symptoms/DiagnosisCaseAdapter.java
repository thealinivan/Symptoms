package com.example.symptoms;

import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.ViewGroup;
import android.widget.TextView;

public class DiagnosisCaseAdapter extends RecyclerView.Adapter<DiagnosisCaseAdapter.Holder> {

    private List<DiagnosisCase> list;
    Holder.DiagnosisCaseClickListener listener;

    public DiagnosisCaseAdapter(List<DiagnosisCase> list, Holder.DiagnosisCaseClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView symptoms, user;
        DiagnosisCaseClickListener listener;
        public Holder(@NonNull View itemView, DiagnosisCaseClickListener _listener) {
            super(itemView);
            symptoms = itemView.findViewById(R.id.item_symptoms);
            user = itemView.findViewById(R.id.item_name);
            listener = _listener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            listener.onDiagnosisCaseClick(getAdapterPosition());
        }
        public interface DiagnosisCaseClickListener{void onDiagnosisCaseClick(int position);}
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.symptoms_case_item, viewGroup, false);
        Holder holder = new Holder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        //set item elements values
        String name = (list.get(i).getUserEmail()).split("@")[0];
        String s1 = name.substring(0, 1).toUpperCase();

        holder.symptoms.setText(list.get(i).getDiagnosisSymptoms());
        holder.user.setText(s1 + name.substring(1));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
