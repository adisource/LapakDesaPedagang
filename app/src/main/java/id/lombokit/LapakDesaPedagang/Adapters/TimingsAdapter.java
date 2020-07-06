package id.lombokit.LapakDesaPedagang.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import id.lombokit.LapakDesaPedagang.R;


public class TimingsAdapter extends RecyclerView
        .Adapter<TimingsAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<String> timmingsArrayList;

    private Context context;
    private static MyClickListener myClickListener;
    private int selectedPos = -1;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            {

        TextView txt;


        public DataObjectHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.txt);

//  *****for line on text********
//            cut_price.setPaintFlags(cut_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Log.i(LOG_TAG, "Adding Listener");
          //  itemView.setOnClickListener(this);

        }

//        @Override
//        public void onClick(View v) {
//                   myClickListener.onItemClick(getAdapterPosition(), v);
//        }

    }

    public void setData(int selectedPos){

        this.selectedPos = selectedPos;

        Log.e("pos", ""+selectedPos);
        notifyDataSetChanged();

    }

//    public void setOnItemClickListener(MyClickListener myClickListener) {
//        this.myClickListener = myClickListener;
//    }

    public TimingsAdapter(Context context, ArrayList<String> timmingsArrayList) {
        this.context = context;
        this.timmingsArrayList = timmingsArrayList;
    }



    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timmings, parent, false);





        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {


        holder.txt.setText(timmingsArrayList.get(position));

        if (selectedPos == position){
            holder.txt.setTextColor(Color.parseColor("#28a7fc"));

        }else {
            holder.txt.setTextColor(Color.parseColor("#c3c4c4"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPos = position;
                notifyDataSetChanged();

            }
        });

    }

    public void deleteItem(int index) {
        timmingsArrayList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return timmingsArrayList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }
}