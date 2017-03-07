package com.apricot.cleanmaster.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apricot.cleanmaster.R;

/**
 * Created by Apricot on 2016/9/18.
 */
public class ItemCardView extends RelativeLayout{
    private ImageView cardImage;
    private TextView cardText;
    public ItemCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.ItemCardView);
        cardText.setText(a.getString(R.styleable.ItemCardView_card_name));
        cardImage.setImageDrawable(a.getDrawable(R.styleable.ItemCardView_card_image));
        a.recycle();
    }

    public ItemCardView(Context context){
        super(context);
        initViews(context);
    }

    private void initViews(Context context){
        View v=View.inflate(context,R.layout.item_card_view,this);
        cardImage= (ImageView) v.findViewById(R.id.card_image);
        cardText= (TextView) v.findViewById(R.id.card_name);
    }
}
