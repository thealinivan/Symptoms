package com.example.symptoms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisAdapter extends RecyclerView.Adapter<DiagnosisAdapter.Holder> {

    private ArrayList<Diagnosis> list;
    Holder.DiagnosisClickListener listener;

    public DiagnosisAdapter(ArrayList<Diagnosis> list, Holder.DiagnosisClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, ICDname, ICDranking, accuracy, specialisationList;
        DiagnosisClickListener listener;

        public Holder(@NonNull View itemView, DiagnosisClickListener _listener) {
            super(itemView);
            name = itemView.findViewById(R.id.health_issue_name);
            ICDname = itemView.findViewById(R.id.icd_name);
            ICDranking = itemView.findViewById(R.id.icd_ranking_value);
            accuracy = itemView.findViewById(R.id.healt_issue_accuracy);
            specialisationList = itemView.findViewById(R.id.specialisation_value);
            listener = _listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onDiagnosisClick(getAdapterPosition());
        }

        public interface DiagnosisClickListener
        {
            public void onDiagnosisClick(int position);
        }
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.diagnosis_item, viewGroup, false);
        Holder holder = new Holder(v, listener);
        return holder;
    }

    //populate item
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.name.setText(list.get(i).getIssue().getName());
        holder.ICDname.setText(list.get(i).getIssue().getIcdName());
        holder.ICDranking.setText(String.valueOf(list.get(i).getIssue().getRanking()));
        Double acc = list.get(i).getIssue().getAccuracy();
        BigDecimal bd = new BigDecimal(acc).setScale(2, RoundingMode.HALF_UP);
        holder.accuracy.setText(bd.doubleValue() + "%");
        holder.specialisationList.setText(getSpecialisations(list.get(i).getSpecialisation()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getSpecialisations(List<Specialisation> specList){
        String specialisation = "";
        for (int i = 0; i < specList.size(); i++){
            specialisation += specList.get(i).getName() + " | ";
        }
        return specialisation;
    }

}
