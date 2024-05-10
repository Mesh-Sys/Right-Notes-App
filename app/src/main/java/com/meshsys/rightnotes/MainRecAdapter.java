package com.meshsys.rightnotes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class MainRecAdapter extends RecyclerView.Adapter<MainRecAdapter.cardviewholder>{
    public ArrayList<MainValue> cardPrinter = new ArrayList<>();
    private Context context;
    private int itemSet = 0;
    private Boolean isMultiSelectMode = false;
    private Boolean isMultiSelectModeUpdate = false;
    private int getAdtposition;
    public MainRecAdapter(Context context) {
        this.context = context;
    }

    public MainRecAdapter() {
    }

    @NonNull
    @Override
    public cardviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_layout,parent,false);
        cardviewholder cardvh = new cardviewholder(view);
        return cardvh;
    }

    @Override
    public void onBindViewHolder(@NonNull cardviewholder holder, int position) {
        getAdtposition = holder.getAdapterPosition();
        holder.cardbtn.setChecked(cardPrinter.get(position).getIsChecked());
        holder.maintxt.setText(cardPrinter.get(position).getInputText());
        holder.datetxt.setText(cardPrinter.get(position).getDateText());
        holder.cardbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!isMultiSelectMode && !holder.cardbtn.isChecked()){
                    MainActivity.getInstance().deletenote.show();
                    MainActivity.getInstance().cancelfab.show();
                    MainActivity.getInstance().addNote.hide();
                    holder.cardbtn.setChecked(!cardPrinter.get(holder.getAdapterPosition()).getIsChecked());
                    cardPrinter.get(holder.getAdapterPosition()).setChecked(true);
                    isMultiSelectMode = true;
                    itemSet++;
                }
                if (itemSet == 0)
                {
                    isMultiSelectMode = false;
                }
                return true;
            }
        });
        holder.cardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMultiSelectMode){

                    if(!holder.cardbtn.isChecked()){
                        holder.cardbtn.setChecked(!cardPrinter.get(holder.getAdapterPosition()).getIsChecked());
                        cardPrinter.get(holder.getAdapterPosition()).setChecked(true);
                        itemSet++;
                    }else{
                        holder.cardbtn.setChecked(!cardPrinter.get(holder.getAdapterPosition()).getIsChecked());
                        cardPrinter.get(holder.getAdapterPosition()).setChecked(false);
                        itemSet--;
                    }
                }else{
                    MainActivity.getInstance().updateItem(holder.getAdapterPosition());
                }
                if (itemSet == 0)
                {
                    isMultiSelectMode = false;
                    MainActivity.getInstance().deletenote.hide();
                    MainActivity.getInstance().cancelfab.hide();
                    MainActivity.getInstance().addNote.show();
                }
                getAdtposition = holder.getAdapterPosition();
            }
        });
        MainActivity.getInstance().cancelfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMultiSelectMode = false;
                unCheckItem();
                itemSet = 0;
                MainActivity.getInstance().deletenote.hide();
                MainActivity.getInstance().cancelfab.hide();
                MainActivity.getInstance().addNote.show();
            }
        });

        MainActivity.getInstance().deletenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMultiSelectMode = false;
                for (int i = 0;i < cardPrinter.size();i++){
                    if (cardPrinter.get(i).getIsChecked()){
                        MainActivity.getInstance().deleteItem(cardPrinter.get(i).getId());
                            notifyItemRemoved(i);
                            cardPrinter.remove(i);
                            i--;
                        }
                    }
                unCheckItem();
                itemSet = 0;
                MainActivity.getInstance().deletenote.hide();
                MainActivity.getInstance().cancelfab.hide();
                MainActivity.getInstance().addNote.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cardPrinter.size();
    }

    public void unCheckItem(){
        for (int i= 0;i < cardPrinter.size();i++){
            if(cardPrinter.get(i).getIsChecked()) {
                cardPrinter.get(i).setChecked(false);
            }
            notifyItemChanged(i);
        }
    }

    public int currentPosition()
    {
        return getAdtposition;
    }
    public void addItem(ArrayList<MainValue> item){

        this.cardPrinter = item;
        notifyDataSetChanged();
    }

    public static class cardviewholder extends RecyclerView.ViewHolder{
        private MaterialTextView maintxt,datetxt;
        private MaterialCardView cardbtn;
        public cardviewholder(@NonNull View itemView) {
            super(itemView);
            maintxt = itemView.findViewById(R.id.maintxt);
            datetxt = itemView.findViewById(R.id.datetxt);
            cardbtn = itemView.findViewById(R.id.cardbtn);
        }

    }
}
