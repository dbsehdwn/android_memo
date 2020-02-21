package com.memo.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MemolistAdapter extends RecyclerView.Adapter<MemolistAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Memo> mData = new ArrayList<>() ;
    private Listener listener;


    interface Listener{
        void onClick(int position);
    }

    MemolistAdapter(Context context){
        this.context = context;
    }

    FileManager fm = new FileManager(context);

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
    public MemolistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_cardview, parent,false);
        return new MemolistAdapter.ViewHolder(cv);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MemolistAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView thumbnail = cardView.findViewById(R.id.thumbnail);
        if(!getImagelist(position).isEmpty()){
            thumbnail.setImageBitmap(getImage(getImagelist(position).get(0)));
            thumbnail.setVisibility(View.VISIBLE);
        }
        TextView title = cardView.findViewById(R.id.title);
        TextView main = cardView.findViewById(R.id.main);

        title.setText(getTitle(position));
        main.setText(getMain(position));
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

    public void additem(int no,String title, String main, ArrayList<String> imagelist){
        Memo memo  = new Memo();

        memo.setNo(no);
        memo.setTitle(title);
        memo.setMain(main);
        memo.setImagelist(imagelist);

        mData.add(memo);

    }

    public int getNo(int position){return mData.get(position).getNo();}
    public String getTitle(int position){return mData.get(position).getTitle();}
    public String getMain(int position){return mData.get(position).getMain();}
    public ArrayList<String> getImagelist(int position){return mData.get(position).getImagelist();}

    public  Bitmap getImage(String path){
        File file = context.getFilesDir();
        return BitmapFactory.decodeFile(file+"/"+path);
    }



}




