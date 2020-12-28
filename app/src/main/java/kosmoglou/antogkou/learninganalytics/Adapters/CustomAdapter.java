package kosmoglou.antogkou.learninganalytics.Adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kosmoglou.antogkou.learninganalytics.Models.PostsModel;
import kosmoglou.antogkou.learninganalytics.R;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private static final String TAG = "CustomAdapter";

    private Context mContext;
    private List<PostsModel> dataSet;


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;


        public MyViewHolder(View itemView) {
            super(itemView);


            this.title = (TextView) itemView.findViewById(R.id.edit_title);
            this.description = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle your click listener
                    Log.w(TAG,"Clicked" + " "+ getAdapterPosition());


                }
            });


        }


    }

    public CustomAdapter(List<PostsModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_posts_feed, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.title.setText(dataSet.get(listPosition).getTitle());
        holder.description.setText(dataSet.get(listPosition).getDescription());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
