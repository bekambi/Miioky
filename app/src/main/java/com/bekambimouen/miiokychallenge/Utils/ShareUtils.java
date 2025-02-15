package com.bekambimouen.miiokychallenge.Utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * Author: Mario Velasco Casquero
 * Date: 02/12/2015
 */
public class ShareUtils {

    /**
     * Share on Facebook. Using Facebook app if installed or web link otherwise.
     *
     * @param activity activity which launches the intent
     * @param text     not used/allowed on Facebook
     * @param url      url to share
     */
    public static void shareFacebook(AppCompatActivity activity, String text, String url, String photo_id) {

        countShare(activity, photo_id);

        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.packageName).contains("com.facebook.katana")) {
                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
            shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }
        activity.startActivity(shareIntent);
    }

    /**
     * Share on Twitter. Using Twitter app if installed or web link otherwise.
     *
     * @param activity activity which launches the intent
     * @param text     text to share
     * @param url      url to share
     * @param via      twitter username without '@' who shares
     * @param hashtags hashtags for tweet without '#' and separated by ','
     */
    public static void shareTwitter(AppCompatActivity activity, String text, String url, String via, String hashtags, String photo_id) {

        countShare(activity, photo_id);

        StringBuilder tweetUrl = new StringBuilder("https://twitter.com/intent/tweet?text=");
        tweetUrl.append(TextUtils.isEmpty(text) ? urlEncode(" ") : urlEncode(text));
        if (!TextUtils.isEmpty(url)) {
            tweetUrl.append("&url=");
            tweetUrl.append(urlEncode(url));
        }
        if (!TextUtils.isEmpty(via)) {
            tweetUrl.append("&via=");
            tweetUrl.append(urlEncode(via));
        }
        if (!TextUtils.isEmpty(hashtags)) {
            tweetUrl.append("&hastags=");
            tweetUrl.append(urlEncode(hashtags));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl.toString()));
        List<ResolveInfo> matches = activity.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        activity.startActivity(intent);
    }

    /**
     * Share on Whatsapp (if installed)
     *
     * @param activity activity which launches the intent
     * @param text     text to share
     * @param url      url to share
     */
    public static void shareWhatsapp(AppCompatActivity activity, String text, String url, String photo_id) {
        PackageManager pm = activity.getPackageManager();
        try {

            countShare(activity, photo_id);

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text + " " + url);
            activity.startActivity(Intent
                    .createChooser(waIntent, activity.getString(R.string.app_name)));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, activity.getString(R.string.share_whatsapp_not_instaled),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Share on Instagram (if installed)
     *
     * @param activity activity which launches the intent
     * @param text     text to share
     * @param url      url to share
     */
    public static void shareInstagram(AppCompatActivity activity, String text, String url, String photo_id) {
        PackageManager pm = activity.getPackageManager();
        try {
            countShare(activity, photo_id);

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info = pm.getPackageInfo("com.instagram.android", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            waIntent.setPackage("com.instagram.android");

            waIntent.putExtra(Intent.EXTRA_TEXT, text + " " + url);
            waIntent.putExtra(Intent.EXTRA_STREAM, url);
            activity.startActivity(Intent
                    .createChooser(waIntent, activity.getString(R.string.app_name)));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, activity.getString(R.string.share_instagram_not_instaled),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Share on Snapchat (if installed)
     *
     * @param activity activity which launches the intent
     * @param text     text to share
     * @param url      url to share
     */
    public static void shareSnapchat(AppCompatActivity activity, String text, String url, String photo_id) {
        PackageManager pm = activity.getPackageManager();
        try {
            countShare(activity, photo_id);

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/plain");

            PackageInfo info = pm.getPackageInfo("com.snapchat.android", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            waIntent.setPackage("com.snapchat.android");

            waIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //waIntent.putExtra(Intent.EXTRA_TEXT, text + " " + url);
            waIntent.putExtra(Intent.EXTRA_STREAM, url);
            activity.startActivity(Intent
                    .createChooser(waIntent, activity.getString(R.string.app_name)));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, activity.getString(R.string.share_snapchat_not_instaled),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convert to UTF-8 text to put it on url format
     *
     * @param s text to be converted
     * @return text on UTF-8 format
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("wtf", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    // share battle file
    public static void countShare(AppCompatActivity activity, String photoID) {
        String newLikeID = FirebaseDatabase.getInstance().getReference().push().getKey();
        assert newLikeID != null;
        FirebaseDatabase.getInstance().getReference().child(activity.getString(R.string.dbname_share_video))
                .child(photoID)
                .child(newLikeID)
                .setValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }
}

