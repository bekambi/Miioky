package com.bekambimouen.miiokychallenge.editprofile.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.editprofile.UpdateProfile;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class AddWorkPlaceAdapter extends RecyclerView.Adapter<AddWorkPlaceAdapter.MyViewHolder> {

    private final AppCompatActivity context;
    private final ArrayList<WorkAt> list;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final String user_id;

    public AddWorkPlaceAdapter(AppCompatActivity context, ArrayList<WorkAt> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_workplace_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WorkAt workAt = list.get(position);
        holder.bind(workAt);

        holder.menu_item.setOnClickListener(view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, holder.menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.more_delete);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    // show dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                    TextView dialog_title = v.findViewById(R.id.dialog_title);
                    TextView dialog_text = v.findViewById(R.id.dialog_text);
                    TextView negativeButton = v.findViewById(R.id.tv_no);
                    TextView positiveButton = v.findViewById(R.id.tv_yes);
                    builder.setView(v);

                    Dialog d = builder.create();
                    d.show();

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.yes));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_item)));
                    negativeButton.setOnClickListener(view1 -> d.dismiss());
                    positiveButton.setOnClickListener(view1 -> {
                        d.dismiss();
                        new Thread(() -> {
                            // delete images;
                            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.dbname_users))
                                    .child(user_id)
                                    .child(context.getString(R.string.field_workAts))
                                    .child(workAt.getWorkAtKey())
                                    .removeValue().addOnSuccessListener(unused -> new Handler().postDelayed(() -> {

                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    }, 300));

                        }).start();
                    });
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView workplace_name;
        private final ImageView menu_item;

        public MyViewHolder(View itemView) {
            super(itemView);

            workplace_name = itemView.findViewById(R.id.workplace_name);
            menu_item = itemView.findViewById(R.id.menu_item);
        }

        public void bind(WorkAt workAt) {
            workplace_name.setText(Html.fromHtml(workAt.getUser_work_at_header()+" <b>"+workAt.getUser_work_at()+"</b>"));

            // go to update fragment
            itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, UpdateProfile.class);
                intent.putExtra("update_workplace_name", "update_workplace_name");
                intent.putExtra("workplaceKey", workAt.getWorkAtKey());
                intent.putExtra("workplace_name", workAt.getUser_work_at());
                intent.putExtra("workplace_name_header", workAt.getUser_work_at_header());
                context.startActivity(intent);
            });
        }
    }
}

