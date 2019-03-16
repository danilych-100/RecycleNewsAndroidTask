package com.example.recyclenewstask.holder;

import android.view.View;
import android.widget.TextView;

import com.example.recyclenewstask.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsHeaderViewHolder extends RecyclerView.ViewHolder {

    private final TextView headerTitle;


    public NewsHeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        this.headerTitle = itemView.findViewById(R.id.headerTitle);
    }

    public TextView getHeaderTitle() {
        return headerTitle;
    }
}
