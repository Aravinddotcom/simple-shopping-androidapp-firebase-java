package com.tetradev.farmeroapp;

import android.content.Context;


import android.graphics.Color;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {
    private Context mContext;
    private List<ImageUpload> mUploads;
    private OnItemClickListener mListener;
   View mView;

    public ImageListAdapter(Context context, List<ImageUpload> uploads) {
        mContext = context;
        mUploads = uploads;

    }




    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.activity_home, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#22343C"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#30444E"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);



//        ImageUpload uploadCurrent = mUploads.get(position);
        ImageUpload uploadCurrent = mUploads.get(getItemCount() - (position + 1));


        holder.textViewName.setText(uploadCurrent.getName());

//        holder.textViewDesc.setText(uploadCurrent.getDesc());
        holder.textViewPrice.setText(uploadCurrent.getPrice());
   //     holder.textViewContact.setText(uploadCurrent.getContact());

        Picasso.with(mContext)
                .load(uploadCurrent.getUrl())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit()
             //   .centerCrop()
                .placeholder(shimmerDrawable)
                .into(holder.imageView);





    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;

       // public TextView textViewDesc;
        public TextView textViewPrice;
      //  public TextView textViewContact;


        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvImageName);
            imageView = itemView.findViewById(R.id.image_view_upload);
           // textViewDesc = itemView.findViewById(R.id.tvImageDesc);
            textViewPrice = itemView.findViewById(R.id.text_view_like_price);
          //  textViewContact = itemView.findViewById(R.id.text_view_like_contact);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (mListener !=null){
                       int position=getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION){
                           mListener.onItemClick(position);
                       }
                   }

                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);

        int getItemCount();
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
}

