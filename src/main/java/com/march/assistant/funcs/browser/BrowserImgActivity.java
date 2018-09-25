package com.march.assistant.funcs.browser;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.march.common.utils.BitmapUtils;
import com.march.common.utils.LgUtils;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistantActivity;

import java.io.File;
import java.util.Locale;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class BrowserImgActivity extends BaseAssistantActivity {

    public static final String DATA = "data";
    private Bitmap mBitmap;

    public static void startActivity(Context context, String path) {
        Intent intent = new Intent(context, BrowserImgActivity.class);
        intent.putExtra(DATA, path);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_img_activity);

        String  path= getIntent().getStringExtra(DATA);
        if(TextUtils.isEmpty(path)){
            try {
                ClipData clipData = getIntent().getClipData();
                if(clipData!=null) {
                    path = clipData.getItemAt(0).getText().toString();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(!TextUtils.isEmpty(path)){
           init(path);
        }
    }


    public void loadImg(String path){
        BitmapFactory.Options bitmapSize = BitmapUtils.getBitmapSize(path);
        int sampleSize = getSampleSize(bitmapSize.outWidth, bitmapSize.outHeight, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = sampleSize;
        LgUtils.e("sample size = " + sampleSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            opts.outConfig = Bitmap.Config.RGB_565;
        }
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mBitmap = BitmapFactory.decodeFile(path, opts);
        ImageView imageView = findViewById(R.id.content_iv);
        imageView.setImageBitmap(mBitmap);
    }

    public int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight){
        int maxIntegerFactor = (int)Math.ceil((double)Math.max((float)inHeight / (float)outHeight, (float)inWidth / (float)outWidth));
        int lesserOrEqualSampleSize = Math.max(1, Integer.highestOneBit(maxIntegerFactor));
        return lesserOrEqualSampleSize << (lesserOrEqualSampleSize < maxIntegerFactor ? 1 : 0);
    }

    private void init(String path){
        TextView detailTv = findViewById(R.id.detail_tv);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        BitmapFactory.Options bitmapSize = BitmapUtils.getBitmapSize(path);
        StringBuilder sb = new StringBuilder().append(file.getName())
                .append("\n")
                .append(file.getAbsolutePath())
                .append("\n")
                .append(bitmapSize.outWidth).append(" * ").append(bitmapSize.outHeight)
                .append("\n")
                .append("大小： ").append(String.format(Locale.getDefault(), "%.2f", file.length() / 2014f)).append("kb");
        detailTv.setText(sb.toString());
        loadImg(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BitmapUtils.recycleBitmaps(mBitmap);
    }
}
