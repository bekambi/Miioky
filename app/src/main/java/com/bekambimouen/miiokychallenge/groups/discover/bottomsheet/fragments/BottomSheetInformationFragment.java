package com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetInformationFragment extends Fragment {
    private static final String TAG = "BottomSheetInformationFragment";

    // widgets
    private TextView total_members2;
    private TextView total_members_last_sevens_days;
    private TextView admin_master_username;
    private TextView publications_today;
    private TextView publications_last_month;
    private CircleImageView admin_master_profile_photo;
    private CircleImageView member_i, member_ii, member_iii, member_iv, member_v, member_vi, member_vii,
            member_viii, member_ix;
    private RelativeLayout relLayout_profile_members, relLayout_member_i, relLayout_member_ii,
            relLayout_member_iii, relLayout_member_iv, relLayout_member_v, relLayout_member_vi,
            relLayout_member_vii, relLayout_member_viii, relLayout_member_ix;
    private LinearLayout parent;

    // vars
    private MainActivity context;
    private UserGroups user_group;
    private ArrayList<String> membersList;
    private int n, m, t, k;

    // firebase
    private DatabaseReference myRef;

    public BottomSheetInformationFragment() {
        // Required empty public constructor
    }

    public static BottomSheetInformationFragment newInstance(UserGroups userGroups) {
        BottomSheetInformationFragment fragment = new BottomSheetInformationFragment();
        Gson gson = new Gson();
        String myGson = gson.toJson(userGroups);

        Bundle args = new Bundle();
        args.putString("user_group", myGson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            user_group = gson.fromJson(getArguments().getString("user_group"), UserGroups.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_information, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();

        membersList = new ArrayList<>();

        admin_master_profile_photo = view.findViewById(R.id.admin_master_profile_photo);
        total_members2 = view.findViewById(R.id.total_members2);
        admin_master_username = view.findViewById(R.id.admin_master_username);
        publications_today = view.findViewById(R.id.publications_today);
        publications_last_month = view.findViewById(R.id.publications_last_month);
        total_members_last_sevens_days = view.findViewById(R.id.total_members_last_sevens_days);
        TextView date_group_creation2 = view.findViewById(R.id.date_group_creation2);
        parent = view.findViewById(R.id.parent);
        relLayout_profile_members = view.findViewById(R.id.relLayout_profile_members);
        relLayout_member_i = view.findViewById(R.id.relLayout_member_i);
        relLayout_member_ii = view.findViewById(R.id.relLayout_member_ii);
        relLayout_member_iii = view.findViewById(R.id.relLayout_member_iii);
        relLayout_member_iv = view.findViewById(R.id.relLayout_member_iv);
        relLayout_member_v = view.findViewById(R.id.relLayout_member_v);
        relLayout_member_vi = view.findViewById(R.id.relLayout_member_vi);
        relLayout_member_vii = view.findViewById(R.id.relLayout_member_vii);
        relLayout_member_viii = view.findViewById(R.id.relLayout_member_viii);
        relLayout_member_ix = view.findViewById(R.id.relLayout_member_ix);
        member_i = view.findViewById(R.id.member_i);
        member_ii = view.findViewById(R.id.member_ii);
        member_iii = view.findViewById(R.id.member_iii);
        member_iv = view.findViewById(R.id.member_iv);
        member_v = view.findViewById(R.id.member_v);
        member_vi = view.findViewById(R.id.member_vi);
        member_vii = view.findViewById(R.id.member_vii);
        member_viii = view.findViewById(R.id.member_viii);
        member_ix = view.findViewById(R.id.member_ix);

        date_group_creation2.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                +context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, user_group.getDate_created())));

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_group.getAdmin_master());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();
                    String username = Objects.requireNonNull(ds.getValue(User.class)).getUsername();

                    admin_master_username.setText(Html.fromHtml("<b>"+username+"</b> "+context.getString(R.string.created_the_group2)));

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(profileUrl)
                                .into(admin_master_profile_photo);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getNewPublicatiosNumbers();
        getNewMembersNumbers();

        return view;
    }

    private void getNewPublicatiosNumbers() {
        n = 0;
        m = 0;
        Log.d(TAG, "getPhotos: getting list of photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_group))
                .child(user_group.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    BattleModel model = new BattleModel();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                    assert objectMap != null;
                    model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

                    long date_create = model.getDate_created();
                    // 1 day later
                    if (TimeUtils.isDateToday(date_create))
                        n++;

                    // 1 month later
                    if ((model.getDate_created() + 86400000L*28) > System.currentTimeMillis())
                        m++;
                }

                publications_today.setText(Html.fromHtml(n +" "+context.getString(R.string.publications_today)));
                publications_last_month.setText(Html.fromHtml(m +" "+context.getString(R.string.publications_last_month)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNewMembersNumbers() {
        k = 0;
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_group.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    GroupFollowersFollowing followersFollowing = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if ((followersFollowing.getDate_following() + 86400000L*7) > System.currentTimeMillis())
                        k++;

                    String member = followersFollowing.getUser_id();
                    membersList.add(member);
                }

                total_members2.setText(Html.fromHtml((t+1)+" "+context.getString(R.string.total_members)));
                total_members_last_sevens_days.setText(Html.fromHtml("+"+ k +" "+context.getString(R.string.total_members_last_sevens_days)));

                if (!membersList.isEmpty()) {
                    relLayout_profile_members.setVisibility(View.VISIBLE);

                    if (membersList.size() == 1) {
                        relLayout_member_i.setVisibility(View.VISIBLE);

                        Query query = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 2) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 3) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 4) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 5) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 6) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 7) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 8) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);
                        relLayout_member_viii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_viii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(7));

                        query_viii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_viii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 9) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);
                        relLayout_member_viii.setVisibility(View.VISIBLE);
                        relLayout_member_ix.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_viii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(7));

                        query_viii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_viii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ix = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(8));

                        query_ix.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    String profileUrl = Objects.requireNonNull(ds.getValue(User.class)).getProfileUrl();

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(profileUrl)
                                                .into(member_ix);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // to make fragment wrap view
        parent.requestLayout();
    }
}