package com.bekambimouen.miiokychallenge.instacropper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.bekambimouen.miiokychallenge.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Created by Yashar on 3/11/2017.
 */

public class InstaCropperActivity extends Activity {
    private static final String TAG = "InstaCropperActivity";

    public static final int DEFAULT_OUTPUT_QUALITY = 80;

    public static final String EXTRA_OUTPUT = MediaStore.EXTRA_OUTPUT;

    public static final String EXTRA_PREFERRED_RATIO = "preferred_ratio";
    public static final String EXTRA_MINIMUM_RATIO = "minimum_ratio";
    public static final String EXTRA_MAXIMUM_RATIO = "maximum_ratio";

    public static final String EXTRA_WIDTH_SPEC = "width_spec";
    public static final String EXTRA_HEIGHT_SPEC = "height_spec";

    public static final String EXTRA_OUTPUT_QUALITY = "output_quality";

    public static Intent getIntent(Context context, Uri src, Uri dst, int maxWidth, int outputQuality) {
        return getIntent(
                context,
                src,
                dst,
                View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                outputQuality
        );
    }

    public static Intent getIntent(Context context, Uri src, Uri dst, int widthSpec, int heightSpec, int outputQuality) {
        return getIntent(
                context,
                src,
                dst,
                InstaCropperView.DEFAULT_RATIO,
                InstaCropperView.DEFAULT_MINIMUM_RATIO,
                InstaCropperView.DEFAULT_MAXIMUM_RATIO,
                widthSpec,
                heightSpec,
                outputQuality
        );
    }

    public static Intent getIntent(Context context, Uri src, Uri dst,
                                   float preferredRatio, float minimumRatio, float maximumRatio,
                                   int widthSpec, int heightSpec, int outputQuality) {
        Intent intent = new Intent(context, InstaCropperActivity.class);

        intent.setData(src);

        intent.putExtra(EXTRA_OUTPUT, dst);

        intent.putExtra(EXTRA_PREFERRED_RATIO, preferredRatio);
        intent.putExtra(EXTRA_MINIMUM_RATIO, minimumRatio);
        intent.putExtra(EXTRA_MAXIMUM_RATIO, maximumRatio);

        intent.putExtra(EXTRA_WIDTH_SPEC, widthSpec);
        intent.putExtra(EXTRA_HEIGHT_SPEC, heightSpec);
        intent.putExtra(EXTRA_OUTPUT_QUALITY, outputQuality);

        return intent;
    }

    private InstaCropperView mInstaCropper;

    private int mWidthSpec;
    private int mHeightSpec;
    private int mOutputQuality;

    private Uri mOutputUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_cropper);

        mInstaCropper = findViewById(R.id.instacropper);

        Intent intent = getIntent();

        Uri uri = intent.getData();

        float defaultRatio = intent.getFloatExtra(EXTRA_PREFERRED_RATIO, InstaCropperView.DEFAULT_RATIO);
        float minimumRatio = intent.getFloatExtra(EXTRA_MINIMUM_RATIO, InstaCropperView.DEFAULT_MINIMUM_RATIO);
        float maximumRatio = intent.getFloatExtra(EXTRA_MAXIMUM_RATIO, InstaCropperView.DEFAULT_MAXIMUM_RATIO);

        mInstaCropper.setRatios(defaultRatio, minimumRatio, maximumRatio);
        mInstaCropper.setImageUri(uri);

        mWidthSpec = intent.getIntExtra(EXTRA_WIDTH_SPEC, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mHeightSpec = intent.getIntExtra(EXTRA_HEIGHT_SPEC, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mOutputQuality = intent.getIntExtra(EXTRA_OUTPUT_QUALITY, DEFAULT_OUTPUT_QUALITY);

        mOutputUri = intent.getParcelableExtra(EXTRA_OUTPUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_instacropper, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_crop);

        Drawable d = Objects.requireNonNull(menuItem.getIcon()).mutate();

        int color;

        color = getColor(R.color.black);

        d.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        menuItem.setIcon(d);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mInstaCropper.crop(mWidthSpec, mHeightSpec, mBitmapCallback);

        return true;
    }

    private final InstaCropperView.BitmapCallback mBitmapCallback = new InstaCropperView.BitmapCallback() {

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onBitmapReady(final Bitmap bitmap) {
            if (bitmap == null) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }

            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        OutputStream os = getContentResolver().openOutputStream(mOutputUri);

                        assert os != null;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, mOutputQuality, os);

                        os.flush();
                        os.close();

                        return true;
                    } catch (IOException e) {
                        Log.d(TAG, "doInBackground: "+e.getMessage());
                    }

                    return false;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    if (success) {
                        Intent data = new Intent();
                        data.setData(mOutputUri);
                        setResult(RESULT_OK, data);
                    }
                    else {
                        setResult(RESULT_CANCELED);
                    }

                    finish();
                }

            }.execute();
        }

    };

}