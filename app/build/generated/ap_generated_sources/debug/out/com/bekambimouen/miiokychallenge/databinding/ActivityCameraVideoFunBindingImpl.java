package com.bekambimouen.miiokychallenge.databinding;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityCameraVideoFunBindingImpl extends ActivityCameraVideoFunBinding implements com.bekambimouen.miiokychallenge.generated.callback.OnClickListener.Listener {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.relLayout_section_camera, 6);
        sViewsWithIds.put(R.id.previewViewFender, 7);
        sViewsWithIds.put(R.id.captureVideo, 8);
        sViewsWithIds.put(R.id.linLayout_countDown, 9);
        sViewsWithIds.put(R.id.seconds_rec, 10);
        sViewsWithIds.put(R.id.mili_zero_rec, 11);
        sViewsWithIds.put(R.id.mili_second_rec, 12);
        sViewsWithIds.put(R.id.flashLight, 13);
        sViewsWithIds.put(R.id.iv_gallery, 14);
        sViewsWithIds.put(R.id.view_ok, 15);
        sViewsWithIds.put(R.id.relLayout_previewLayout, 16);
        sViewsWithIds.put(R.id.videoView, 17);
        sViewsWithIds.put(R.id.relLayout_stop_play, 18);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    @NonNull
    private final android.widget.RelativeLayout mboundView1;
    @NonNull
    private final android.widget.RelativeLayout mboundView4;
    @NonNull
    private final android.widget.RelativeLayout mboundView5;
    // variables
    @Nullable
    private final android.view.View.OnClickListener mCallback5;
    @Nullable
    private final android.view.View.OnClickListener mCallback3;
    @Nullable
    private final android.view.View.OnClickListener mCallback4;
    @Nullable
    private final android.view.View.OnClickListener mCallback1;
    @Nullable
    private final android.view.View.OnClickListener mCallback2;
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityCameraVideoFunBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 19, sIncludes, sViewsWithIds));
    }
    private ActivityCameraVideoFunBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[8]
            , (android.widget.ImageView) bindings[13]
            , (android.widget.RelativeLayout) bindings[14]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[11]
            , (androidx.camera.view.PreviewView) bindings[7]
            , (android.widget.RelativeLayout) bindings[2]
            , (android.widget.RelativeLayout) bindings[16]
            , (android.widget.RelativeLayout) bindings[6]
            , (android.widget.RelativeLayout) bindings[18]
            , (android.widget.RelativeLayout) bindings[3]
            , (android.widget.TextView) bindings[10]
            , (android.widget.VideoView) bindings[17]
            , (android.widget.ImageView) bindings[15]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.RelativeLayout) bindings[1];
        this.mboundView1.setTag(null);
        this.mboundView4 = (android.widget.RelativeLayout) bindings[4];
        this.mboundView4.setTag(null);
        this.mboundView5 = (android.widget.RelativeLayout) bindings[5];
        this.mboundView5.setTag(null);
        this.relLayoutFlashLight.setTag(null);
        this.relLayoutTunrFront.setTag(null);
        setRootTag(root);
        // listeners
        mCallback5 = new com.bekambimouen.miiokychallenge.generated.callback.OnClickListener(this, 5);
        mCallback3 = new com.bekambimouen.miiokychallenge.generated.callback.OnClickListener(this, 3);
        mCallback4 = new com.bekambimouen.miiokychallenge.generated.callback.OnClickListener(this, 4);
        mCallback1 = new com.bekambimouen.miiokychallenge.generated.callback.OnClickListener(this, 1);
        mCallback2 = new com.bekambimouen.miiokychallenge.generated.callback.OnClickListener(this, 2);
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.cameraListener == variableId) {
            setCameraListener((com.bekambimouen.miiokychallenge.fun.listener.CameraListener) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setCameraListener(@Nullable com.bekambimouen.miiokychallenge.fun.listener.CameraListener CameraListener) {
        this.mCameraListener = CameraListener;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.cameraListener);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.bekambimouen.miiokychallenge.fun.listener.CameraListener cameraListener = mCameraListener;
        // batch finished
        if ((dirtyFlags & 0x2L) != 0) {
            // api target 1

            this.mboundView1.setOnClickListener(mCallback1);
            this.mboundView4.setOnClickListener(mCallback4);
            this.mboundView5.setOnClickListener(mCallback5);
            this.relLayoutFlashLight.setOnClickListener(mCallback2);
            this.relLayoutTunrFront.setOnClickListener(mCallback3);
        }
    }
    // Listener Stub Implementations
    // callback impls
    public final void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0) {
        switch(sourceId) {
            case 5: {
                // localize variables for thread safety
                // cameraListener != null
                boolean cameraListenerJavaLangObjectNull = false;
                // cameraListener
                com.bekambimouen.miiokychallenge.fun.listener.CameraListener cameraListener = mCameraListener;



                cameraListenerJavaLangObjectNull = (cameraListener) != (null);
                if (cameraListenerJavaLangObjectNull) {



                    cameraListener.submitClick(callbackArg_0);
                }
                break;
            }
            case 3: {
                // localize variables for thread safety
                // cameraListener != null
                boolean cameraListenerJavaLangObjectNull = false;
                // cameraListener
                com.bekambimouen.miiokychallenge.fun.listener.CameraListener cameraListener = mCameraListener;



                cameraListenerJavaLangObjectNull = (cameraListener) != (null);
                if (cameraListenerJavaLangObjectNull) {



                    cameraListener.changeCamera(callbackArg_0);
                }
                break;
            }
            case 4: {
                // localize variables for thread safety
                // cameraListener != null
                boolean cameraListenerJavaLangObjectNull = false;
                // cameraListener
                com.bekambimouen.miiokychallenge.fun.listener.CameraListener cameraListener = mCameraListener;



                cameraListenerJavaLangObjectNull = (cameraListener) != (null);
                if (cameraListenerJavaLangObjectNull) {



                    cameraListener.closeBackToCamera(callbackArg_0);
                }
                break;
            }
            case 1: {
                // localize variables for thread safety
                // cameraListener != null
                boolean cameraListenerJavaLangObjectNull = false;
                // cameraListener
                com.bekambimouen.miiokychallenge.fun.listener.CameraListener cameraListener = mCameraListener;



                cameraListenerJavaLangObjectNull = (cameraListener) != (null);
                if (cameraListenerJavaLangObjectNull) {




                    cameraListener.playPause(callbackArg_0, captureVideo);
                }
                break;
            }
            case 2: {
                // localize variables for thread safety
                // cameraListener != null
                boolean cameraListenerJavaLangObjectNull = false;
                // cameraListener
                com.bekambimouen.miiokychallenge.fun.listener.CameraListener cameraListener = mCameraListener;



                cameraListenerJavaLangObjectNull = (cameraListener) != (null);
                if (cameraListenerJavaLangObjectNull) {




                    cameraListener.flashOff(callbackArg_0, flashLight);
                }
                break;
            }
        }
    }
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): cameraListener
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}