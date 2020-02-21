package com.memo.android;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImagelistAdapter extends RecyclerView.Adapter<ImagelistAdapter.ViewHolder> {

    private ArrayList<Image> mData = new ArrayList<>() ;

    private Listener listener;

    interface Listener{
        void onClick(int position);
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        ViewHolder(CardView v) {
            super(v) ;
            cardView = v;
        }

    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ImagelistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_cardview, parent,false);
        return new ImagelistAdapter.ViewHolder(cv);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ImagelistAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView image = cardView.findViewById(R.id.image);

        image.setImageBitmap(getImage_Bitmap(position));

        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public void additem(String route, Bitmap bitmap){
        Image image  = new Image();

        image.setImage_route(route);
        image.setImage_bitmap(bitmap);

        mData.add(image);

    }

    public void remove(int position){
        mData.remove(position);
    }


    public String getImage_Name(int position){return mData.get(position).getImage_route();}
    public Bitmap getImage_Bitmap(int position){return mData.get(position).getImage_bitmap();}



}
